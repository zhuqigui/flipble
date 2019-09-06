package com.android.flipble.util;

public class FourYuanNumberUtils {
    Utils mUtils;

    private static FourYuanNumberUtils instance=new FourYuanNumberUtils();

    private FourYuanNumberUtils(){
        mUtils=new Utils();
        //默认提供的构造方法是public的 因此在此需要自定义一个无参的构造 并将访问权限该为private的
    }

    public static FourYuanNumberUtils getInstance(){
        return instance;
    }


    /**
     *  //四元数 w 的实际值=w/0x7FFFFF,例如：
     *  //Dec(1193046) Float(1193046.0/0x7FFFFF) 0.14222218301560677
     * 获取w值
     * @param wValue
     */
    public void getFourYuanDataWValue(String wValue){
        //1.先转为10进制如123456->‭1193046‬
        mUtils.getWValue(wValue);
    }
    /**
     * 获取y坐标
     * @param yValue
     */
    public String getFourYuanDatayValue(String yValue){
        return mUtils.getCoordinatesResult(yValue);
    }
    /**
     * 获取x坐标
     * @param xValue
     */
    public String getFourYuanDataxValue(String xValue){
        return mUtils.getCoordinatesResult(xValue);
    }
    /**
     * 获取z坐标
     * @param zValue
     */
    public String getFourYuanDatazValue(String zValue){
        return mUtils.getCoordinatesResult(zValue);
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
        mUtils.xOryMoveEvent(move);
    }
    /**
     *  得到触摸移动事件y坐标
     * @param move
     */
    public void getMoveEventYPoint(String move){
        mUtils.xOryMoveEvent(move);
    }
}
