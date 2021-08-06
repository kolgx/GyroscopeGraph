package xyz.lailin.atool;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
*加速度传感器（陀螺仪）管理类
 */
public class AccelerationManger{

    /**
     * 上一次的值
     */
    private float[] prevData=new float[3];

    /**
     * 传感器控制
     */
    private SensorManager mSensorManager=null;

    /**
     * 传感器
     */
    private Sensor mSensor=null;

    /**
     * 监听器
     */
    private SensorEventListener mSensorEventListener;

    /**
     * 构造函数，传感器初始化
     * @param mSensorEventListener 监听器
     */
    AccelerationManger(SensorManager mSensorManager,SensorEventListener mSensorEventListener){

        //获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象
        this.mSensorManager = mSensorManager;

        //通过SensorManager获取相应的（加速度感应器）Sensor类型对象
        mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mSensorEventListener = mSensorEventListener;
    }

    /**
     * 注册加速度传感器
     */
    void register(){
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 取消注册加速度传感器
     */
    void unRegister(){
        mSensorManager.unregisterListener(mSensorEventListener, mSensor);
    }


    /**
     * 获取当前的舒适状况
     */
    public void  setComfortRate(RatingBar rate ,float comfort ,ChartView chart){
        float r=0f;
        if(-(comfort-5f)>0){
            r=-comfort+5f;
        }
        chart.addData(r,3);
        rate.setRating(r);
    }

    public float[] getPrevData() {
        return prevData;
    }

    public void setPrevData(SensorEvent event) {
        System.arraycopy(event.values, 0, prevData, 0, 3);
    }
}
