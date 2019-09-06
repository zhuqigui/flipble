package com.android.flipble.util;

public class AccelerationAndGyroscopeNumberUtils {
    Utils mUtils;

    private static AccelerationAndGyroscopeNumberUtils instance=new AccelerationAndGyroscopeNumberUtils();

    private AccelerationAndGyroscopeNumberUtils(){
        mUtils=new Utils();
        //默认提供的构造方法是public的 因此在此需要自定义一个无参的构造 并将访问权限该为private的
    }

    public static AccelerationAndGyroscopeNumberUtils getInstance(){
        return instance;
    }

    /**
     * 获取欧拉y坐标
     * @param yValue
     */
    public double getOuNaDatayValue(String yValue){
        return mUtils.oneHalfHexToInt(yValue)/10.0;
    }
    /**
     * 获取欧拉x坐标
     * @param xValue
     */
    public double getOuNaDataxValue(String xValue){
        return mUtils.oneHalfHexToInt(xValue)/10.0;
    }
    /**
     * 获取欧拉z坐标
     * @param zValue
     */
    public double getOuNaDatazValue(String zValue){
        return mUtils.oneHalfHexToInt(zValue)/10.0;
    }
    /**
     * 获取加速度y坐标
     * @param yValue
     */
    public double getAccelerationDatayValue(String yValue){
        return mUtils.oneHalfHexToInt(yValue)/0x7FF*16;
    }
    /**
     * 获取加速度x坐标
     * @param xValue
     */
    public double getAccelerationDataxValue(String xValue){
        return mUtils.oneHalfHexToInt(xValue)/0x7FF*16;
    }
    /**
     * 获取加速度z坐标
     * @param zValue
     */
    public double getAccelerationDatazValue(String zValue){
        return mUtils.oneHalfHexToInt(zValue)/0x7FF*16;
    }
    /**
     * 获取陀螺仪x坐标
     * @param xValue
     */
    public double getGyroscopeDataxValue(String xValue){
        return mUtils.oneHalfHexToInt(xValue)/0x7FF*2000;
    }
    /**
     * 获取陀螺仪x坐标
     * @param yValue
     */
    public double getGyroscopeDatayValue(String yValue){
        return mUtils.oneHalfHexToInt(yValue)/0x7FF*2000;
    }
    /**
     * 获取陀螺仪y坐标
     * @param zValue
     */
    public double getGyroscopeDatazValue(String zValue){
        return mUtils.oneHalfHexToInt(zValue)/0x7FF*2000;
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
    public double getMoveEventXPoint(String move){
        return mUtils.oneHalfHexToInt(move);
    }
    /**
     *  得到触摸移动事件y坐标
     * @param move
     */
    public double getMoveEventYPoint(String move){
        return mUtils.oneHalfHexToInt(move);
    }
}
