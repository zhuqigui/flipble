package com.android.flipble.util;

import android.util.Log;

import java.text.NumberFormat;

public class Utils {
    char[] oneHexToChar;
    char[] oneHalfHexToChar;
    char[] twoHexToChar;
    double oneHalfResult;

    int eventResult;//转换的移动事件类型结果
    int moveResult;//转换的移动结果
    int moveStatus;//移动状态

    int keyDownResult;//转换的按键类型结果
    String keyDownToBin;//按键类型转换为的二进制数据
    int key;//按键值

    int dataTypeResult;//转换的数据类型结果

    int triggerResult;//转换的扳机结果

    boolean fuhaoweiIsZhen = false;
    char[] strToChar;
    int fuhaowei;
    String binStr;
    String y1,y2,y3;
    int y1Result,y2Result,y3Result;
    String s;
    double v;
    NumberFormat format1;

    String w1,w2,w3;
    int w1Result,w2Result,w3Result;
    double wv;
    String ws;
    /**
     * 8位16进制字符串转为10进制
     * @param hex 单个16进制字符串
     */
    public int oneHexToInt(String hex){
        oneHexToChar= hex.toCharArray(); // 先把它变为字符数组
        return charToDec(oneHexToChar[0])*16+charToDec(oneHexToChar[1]);
    }
    /**
     * 12位16进制字符串转为10进制
     * @param oneHalfhex 12位16进制字符串
     */
    public double oneHalfHexToInt(String oneHalfhex){
        oneHalfHexToChar= oneHalfhex.toCharArray(); // 先把它变为字符数组
        oneHalfResult=charToDec(oneHalfHexToChar[0])*16*16+charToDec(oneHalfHexToChar[1])*16+charToDec(oneHalfHexToChar[2]);
        fuhaowei=(charToDec(oneHalfHexToChar[0]) & 0x8) >> 3;
        if(fuhaowei==1){
            fuhaoweiIsZhen=false;
            oneHalfResult=oneHalfResult*-1;
        }
        //Log.i(TAG,"s=="+s);
        fuhaoweiIsZhen=false;
        fuhaowei=-1;
        return oneHalfResult;
    }
    /**
     * 24位16进制字符串拼接转为10进制
     * @param twoHex 两个16进制字符串
     */
    public int twoHexToInt(String twoHex){
        twoHexToChar= twoHex.toCharArray(); // 先把它变为字符数组
        eventResult=charToDec(twoHexToChar[0])*16*16*16+charToDec(twoHexToChar[1])*16*16
                    +charToDec(twoHexToChar[2])*16+charToDec(twoHexToChar[3]);
        return eventResult;
    }
    /**
     * 单个字符转换成10进制整数
     * @param c 字符
     * @return 10进制整数
     */
    public int charToDec(char c){
        if(c=='0'){
            return 0;
        }else if(c=='1'){
            return 1;
        }else if(c=='2'){
            return 2;
        }else if(c=='3'){
            return 3;
        }else if(c=='4'){
            return 4;
        }else if(c=='5'){
            return 5;
        }else if(c=='6'){
            return 6;
        }else if(c=='7'){
            return 7;
        }else if(c=='8'){
            return 8;
        }else if(c=='9'){
            return 9;
        }else if(c=='a'){
            return 10;
        }else if(c=='b'){
            return 11;
        }else if(c=='c'){
            return 12;
        }else if(c=='d'){
            return 13;
        }else if(c=='e'){
            return 14;
        }else if(c=='f'){
            return 15;
        }
        return 0;
    }
    /**
     * 解析触摸板触摸状态
     * @param move
     */
    public void moveEventStatus(String move){
        moveResult=twoHexToInt(move);
        //Log.i(DataUtils.TAG,"moveResult=="+moveResult);
        //1.截取计算event事件对应码，后14位清零
        moveStatus=(moveResult >> 14) & 0x3;
        if(moveStatus==0){
            Log.i(DataUtils.TAG,"触摸状态为空置状态=="+moveStatus);
        }else if(moveStatus==1){
            Log.i(DataUtils.TAG,"触摸状态为触摸状态=="+moveStatus);
        }else if(moveStatus==2){
            Log.i(DataUtils.TAG,"触摸状态为移动状态=="+moveStatus);
        }else if(moveStatus==3){
            Log.i(DataUtils.TAG,"触摸状态为释放状态=="+moveStatus);
        }
    }
    /**
     *  解析触摸板x y坐标
     * @param move 16进制字符串
     */
    public int xOryMoveEvent(String move){
        moveResult=twoHexToInt(move);
        //截取计算x或y坐标值,前6位清零
        return moveResult & 0x3ff;
    }
    /**
     * //Bit 7 6 5 4 3 2 1 0
     * //Key Reserved App Power/Home Touchpad Hand (left) Hand (right
     *解析按键事件
     * @param keyDown
     */
    public  void keyDownEvent(String keyDown){
        keyDownResult=oneHexToInt(keyDown);
        //截取后五位
        keyDownResult=keyDownResult & 0x1f;
        //转换为二进制 8->1000
        keyDownToBin=Integer.toBinaryString(keyDownResult);
        //判断是哪个按键的值为1，就是按下了哪个按键
        for (int i = 0; i <keyDownToBin.length(); i++) {
            key = (keyDownResult >> i) & 0x1;
            if(key==1){//
                switch(i){
                    case 4:
                        Log.i(DataUtils.TAG,"App 按键按下....");
                        break;
                    case 3:
                        Log.i(DataUtils.TAG,"Power/Home 按键按下....");
                        break;
                    case 2:
                        Log.i(DataUtils.TAG,"Touchpad 按键按下....");
                        break;
                    case 1:
                        Log.i(DataUtils.TAG,"Hand (left) 按键按下....");
                        break;
                    case 0:
                        Log.i(DataUtils.TAG,"Hand (right 按键按下....");
                        break;
                }

            }
        }
    }
    /**
     *解析数据类型01 02 20
     * @param dataType
     */
    public  int dataTypeEvent(String dataType){
        dataTypeResult=oneHexToInt(dataType);
        return  dataTypeResult;
    }

    /**解析扳机是否按下 0 表示松开，255 表示按下
     * @param trigger 16进制字符串 00或FF
     * @return
     */
    public void triggerEvent(String trigger){
        triggerResult=oneHexToInt(trigger);
        Log.i(DataUtils.TAG,"triggerResult=="+triggerResult);
        if(triggerResult==0){
            Log.i(DataUtils.TAG,"扳机松开...");
        }else if(triggerResult==255){
            Log.i(DataUtils.TAG,"扳机按下...");
        }
    }


    /**
     * 得到各坐标数值 x y z
     * @param coordinaStr 16进制字符串
     * @return 坐标数值
     */
    public  String getCoordinatesResult(String coordinaStr){
        /**
         * 1.确认符号位，（转换成二进制）假如0就是正的，1就是负数
         * 2.去掉符号位
         * 3.16进制字符串转换为10进制数，
         * 4.合并（符号位是0就*1不变，1就*-1）
         */
        //y="fcdace";//fc->11111100 & 01111111->转换成10进制*256*256
        //da->10*256
        //ce->10
        //最后再三个加一起得到y坐标
        //1.确认符号位，（转换成二进制）假如0就是正的，1就是负数
        strToChar= coordinaStr.toCharArray(); // 先把它他变为字符数组
        fuhaowei=(charToDec(strToChar[0]) & 0x8) >> 3;
        if(fuhaowei==0){
            fuhaoweiIsZhen=true;
        }else if(fuhaowei==1){
            fuhaoweiIsZhen=false;
        }

        y1=coordinaStr.substring(0,2);
        y2=coordinaStr.substring(2,4);
        y3=coordinaStr.substring(4,6);

        y1Result=oneHexToInt(y1);
        y1Result=y1Result & 0x7f;
        y2Result=oneHexToInt(y2);
        y3Result=oneHexToInt(y3);

        //计算总坐标结果并显示小数点后4位
        format1=NumberFormat.getNumberInstance() ;
        format1.setMaximumFractionDigits(4);
        v = (y1Result * 256 * 256 + y2Result * 256 + y3Result) / 10000.0;
        if(fuhaoweiIsZhen){
            s= format1.format(v);
        }else {
            s = format1.format((v) * -1);
        }
        //Log.i(TAG,"s=="+s);
        fuhaoweiIsZhen=false;
        fuhaowei=-1;
        return s;
    }
    public String getWValue(String w){
        w1=w.substring(0,2);
        w2=w.substring(2,4);
        w3=w.substring(4,6);
        //Log.i(TAG,"w1=="+w1+",w2=="+w2+",w3=="+w3);
        w1Result=oneHexToInt(w1);
        w2Result=oneHexToInt(w2);
        w3Result=oneHexToInt(w3);
        //Log.i(TAG,"w1Result=="+w1Result+",w2Result=="+w2Result+",w3Result=="+w3Result);
        //计算总坐标结果并显示小数点后4位
        format1=NumberFormat.getNumberInstance() ;
        format1.setMaximumFractionDigits(8);
        wv = (w1Result * 256 * 256 + w2Result * 256 + w3Result) ;
        //System.out.print("wv=="+wv);
        wv =wv/0x7FFFFF;
        ws= format1.format(wv);
//        System.out.print("wv=="+wv+",ws=="+ws);
//        Log.i(TAG,"wv=="+wv);
        return ws;
    }
}
