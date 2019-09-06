package com.android.flipble.util;

import android.util.Log;

import java.text.NumberFormat;

public class ONaUtils {
    Utils mUtils;

    private static ONaUtils instance=new ONaUtils();

    private ONaUtils(){
        mUtils=new Utils();
        //默认提供的构造方法是public的 因此在此需要自定义一个无参的构造 并将访问权限该为private的
    }

    public static ONaUtils getInstance(){
        return instance;
    }
    /**
     * 01----表示该数据包为欧拉角数据帧
     * 00----reserved
     * fc da ce --y -818.2478
     * 01 a6 f5 --x 10.8277
     * fc 3c 64 --z -814.1924
     * 00 06 3f --第 1599 次数据
     * 00 ----扳机是松开的
     * ff 08 ----power button 是按下的
     * 81 90 ---x 10 00 0001 1001 0000 表示移动事件，x=400
     * 02 02---y y=514
     */
    /**
     * 得到欧拉角y坐标
     * @param yStr 16进制字符串
     * @return
     */
    public String getONaYPoint(String yStr){
        return mUtils.getCoordinatesResult(yStr);
    }

    /**
     * 得到欧拉角x坐标
     * @param xStr
     * @return
     */
    public String getONaXPoint(String xStr){
        return mUtils.getCoordinatesResult(xStr);
    }

    /**
     * 得到欧拉角z坐标
     * @param zStr
     * @return
     */
    public String getONaZPoint(String zStr){
        return mUtils.getCoordinatesResult(zStr);
    }

    /**
     * 得到扳机状态
     * @param trigger
     */
    public void getTriggerStatus(String trigger){
        mUtils.triggerEvent(trigger);
    }

    /**
     * 得到按键状态
     * @param key
     */
    public void getKeydownStatus(String key){
        mUtils.keyDownEvent(key);
    }

    /**
     *  得到触摸板是否触摸状态
     * @param move
     */
    public void getMoveEventStatus(String move){
        mUtils.moveEventStatus(move);
    }
    /**
     *  得到触摸移动事件x坐标
     * @param move
     */
    public void getMoveEventXPoint(String move){
        Log.i(DataUtils.TAG,"欧拉X轴坐标为："+mUtils.xOryMoveEvent(move));
    }
    /**
     *  得到触摸移动事件y坐标
     * @param move
     */
    public void getMoveEventYPoint(String move){
        Log.i(DataUtils.TAG,"欧拉Y轴坐标为："+mUtils.xOryMoveEvent(move));
    }
}
