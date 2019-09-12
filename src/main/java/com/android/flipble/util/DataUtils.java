package com.android.flipble.util;

import android.util.Log;

public class DataUtils {
    public static final String TAG="liu";
    int dataType;
    String mONay,mONax,mONaz;
    String trigger,keyDown,xMoveEvent,yMoveEvent;
    String fourYuanw,fourYuany,fourYuanx,fourYuanz;
    String mAccelerationy,mAccelerationx,mAccelerationz;
    String mGyroscopey,mGyroscopex,mGyroscopez;
    Utils mUtils;

    private static DataUtils instance=new DataUtils();

    private DataUtils(){
        mUtils=new Utils();
        //默认提供的构造方法是public的 因此在此需要自定义一个无参的构造 并将访问权限该为private的
    }

    public static DataUtils getInstance(){
        return instance;
    }
    public void handleData(String data){
        dataType=mUtils.dataTypeEvent(data.substring(0,2));
        Log.i(TAG,"data=="+data+"dataTypeResult=="+dataType);
        if(dataType==1){
            Log.i(TAG,"数据类型为欧拉角类型,开始解析欧拉角数据");
            mONay=data.substring(4,10);
            mONax=data.substring(10,16);
            mONaz=data.substring(16,22);
            trigger=data.substring(28,30);
            keyDown=data.substring(30,32);
            xMoveEvent=data.substring(32,36);
            yMoveEvent=data.substring(36,40);
            Log.i(TAG,"y=="+ONaUtils.getInstance().getONaYPoint(mONay)); //fcdace
            Log.i(TAG,"x=="+ONaUtils.getInstance().getONaYPoint(mONax)); //01a6f5
            Log.i(TAG,"z=="+ONaUtils.getInstance().getONaYPoint(mONaz)); //fc3c64

            ONaUtils.getInstance().getTriggerStatus(trigger);//255
            ONaUtils.getInstance().getKeydownStatus(keyDown);//App 按键按下
            ONaUtils.getInstance().getMoveEventStatus(xMoveEvent);
            ONaUtils.getInstance().getMoveEventXPoint(xMoveEvent);
            ONaUtils.getInstance().getMoveEventYPoint(yMoveEvent);
        }else if(dataType==2){
            Log.i(TAG,"数据类型为四元数类型，开始解析四元数数据");
            fourYuanw=data.substring(4,10);
            fourYuany=data.substring(10,16);
            fourYuanx=data.substring(16,22);
            fourYuanz=data.substring(22,28);
            trigger=data.substring(28,30);
            keyDown=data.substring(30,32);
            xMoveEvent=data.substring(32,36);
            yMoveEvent=data.substring(36,40);
            Log.i(TAG,"四元数数据w=="+FourYuanNumberUtils.getInstance().getFourYuanDatayValue(fourYuanw)); //fcdace
            Log.i(TAG,"四元数数据y=="+FourYuanNumberUtils.getInstance().getFourYuanDatayValue(fourYuany)); //fcdace
            Log.i(TAG,"四元数数据x=="+FourYuanNumberUtils.getInstance().getFourYuanDataxValue(fourYuanx)); //01a6f5
            Log.i(TAG,"四元数数据z=="+FourYuanNumberUtils.getInstance().getFourYuanDatazValue(fourYuanz)); //fc3c64

            FourYuanNumberUtils.getInstance().getTriggerStatus(trigger);//255
            FourYuanNumberUtils.getInstance().getKeydownStatus(keyDown);//App 按键按下
            FourYuanNumberUtils.getInstance().getMoveEventStatus(xMoveEvent);
            FourYuanNumberUtils.getInstance().getMoveEventXPoint(xMoveEvent);
            FourYuanNumberUtils.getInstance().getMoveEventYPoint(yMoveEvent);
        }//else if(dataType==32){
//            Log.i(TAG,"数据类型为欧拉角与加速度与陀螺仪类型，开始解析加速度与陀螺仪数据");
//             mONay=data.substring(3,6);
//             mONax=data.substring(6,9);
//             mONaz=data.substring(9,12);
//
//             mAccelerationx=data.substring(12,15);
//             mAccelerationy=data.substring(15,18);
//             mAccelerationz=data.substring(18,21);
//
//             mGyroscopex=data.substring(21,24);
//             mGyroscopey=data.substring(24,27);
//             mGyroscopez=data.substring(27,30);
//
//             trigger=data.substring(30,32);
//             keyDown=data.substring(32,34);
//             xMoveEvent=data.substring(34,37);
//             yMoveEvent=data.substring(37,40);
//            Log.i(TAG,"加速度与陀螺仪数据欧拉角y轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getOuNaDataxValue(mONay));//29.1
//            Log.i(TAG,"加速度与陀螺仪数据欧拉角x轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getOuNaDatayValue(mONax));
//            Log.i(TAG,"加速度与陀螺仪数据欧拉角z轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getOuNaDatazValue(mONaz));
//            Log.i(TAG,"加速度与陀螺仪数据加速度x轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getAccelerationDataxValue(mAccelerationx));//2.2745481191988275
//            Log.i(TAG,"加速度与陀螺仪数据加速度y轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getAccelerationDatayValue(mAccelerationy));
//            Log.i(TAG,"加速度与陀螺仪数据加速度z轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getAccelerationDatazValue(mAccelerationz));
//            Log.i(TAG,"加速度与陀螺仪数据陀螺仪x轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getGyroscopeDataxValue(mGyroscopex));//284.3185148998534
//            Log.i(TAG,"加速度与陀螺仪数据陀螺仪y轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getGyroscopeDatayValue(mGyroscopey));
//            Log.i(TAG,"加速度与陀螺仪数据陀螺仪z轴为:"+AccelerationAndGyroscopeNumberUtils.getInstance().getGyroscopeDatazValue(mGyroscopez));
//            AccelerationAndGyroscopeNumberUtils.getInstance().getTriggerStatus(trigger);//0
//            //AccelerationAndGyroscopeNumberUtils.getInstance().getTriggerStatus("ff");//255
////        AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus("f0");//App 按键按下
////        AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus("08");//Power/Home 按键按下
////        AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus("04");//Touchpad 按键按下
////        AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus("02");//Hand (left) 按键按下
////        AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus("01");//Hand (right 按键按下
//            AccelerationAndGyroscopeNumberUtils.getInstance().getKeydownStatus(keyDown);//Hand (right 按键按下
//            Log.i(TAG,"触摸移动事件x坐标为"+AccelerationAndGyroscopeNumberUtils.getInstance().getMoveEventXPoint(xMoveEvent));
//            Log.i(TAG,"触摸移动事件x坐标为"+AccelerationAndGyroscopeNumberUtils.getInstance().getMoveEventYPoint(yMoveEvent));
//        }
    }
}
