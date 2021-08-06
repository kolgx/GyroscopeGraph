package xyz.lailin.atool;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import xyz.lailin.atool.Storage.FileStorage;

import static android.R.attr.max;
import static android.R.attr.value;


public class MainActivity extends AppCompatActivity {

    private TextView textValX;
    private TextView  textValY;
    private TextView  textValZ;

    private RatingBar rate;

    private ChartView chart;
    LineChart mLineChart;

    private List<String> accData=new ArrayList<String>();

    private boolean isSaveData=false;

    private AccelerationManger acceleration = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textValX= findViewById(R.id.valueX);
        textValY= findViewById(R.id.valueY);
        textValZ= findViewById(R.id.valueZ);
        //舒适度设置
        rate= findViewById(R.id.rate);

        mLineChart= findViewById(R.id.chart);

        chart=new ChartView(mLineChart);

        //加速度传感器初始化
        acceleration = new AccelerationManger((SensorManager)getSystemService(SENSOR_SERVICE),
                mSensorEventListener);

        //文件保存
        final FileStorage fileStorage = new FileStorage("ATool");

        //获取switch btn
        final Switch switchButton= findViewById(R.id.switchButton);

        //通过switch btn的状态设置是否监听
        switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    acceleration.register();
                    toast("测试开始！");
                }else {
                    acceleration.unRegister();
                    if (isSaveData){
                        fileStorage.addData(accData);
                        toast("测试结束！文件已保存在："+fileStorage.getFile().getPath());
                    }else {
                        toast("测试结束！");
                    }
                }
                switchButton.setChecked(isChecked);
            }
        });


        final Switch isSaveSwitch= findViewById(R.id.isSave);
        isSaveSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSaveData=isChecked;
            }
        });

        //设置曲线是否显示
        Switch switchX= findViewById(R.id.switchX);
        Switch switchY= findViewById(R.id.switchY);
        Switch switchZ= findViewById(R.id.switchZ);
        lineControl(switchX,0);
        lineControl(switchY,1);
        lineControl(switchZ,2);


    }

    /**
     * 加速度传感器监听方法
     */
    public final SensorEventListener mSensorEventListener=new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                float x=event.values[0];
                float y=event.values[1];
                float z=event.values[2];

                float[] prevData =  acceleration.getPrevData();
                if (prevData!=null){
                    float comfort=0f;
                    for (int i = 0; i < 3; i++) {
                        if(Math.abs(prevData[i]-event.values[i])>comfort){
                            comfort=Math.abs(prevData[i]-event.values[i]);
                        }
                    }
                    acceleration.setComfortRate(rate,comfort,chart);
                }
                acceleration.setPrevData(event);

                /*显示左右、前后、垂直方向加速度*/
                DecimalFormat decimalFormat=new DecimalFormat("00.00");
                accData.add(decimalFormat.format(x)+"\t"+decimalFormat.format(y)+"\t"+decimalFormat.format(z));
                textValX.setText(decimalFormat.format(x));
                textValY.setText(decimalFormat.format(y));
                textValZ.setText(decimalFormat.format(z));

                chart.addData(x,0);
                chart.addData(y,1);
                chart.addData(z,2);

            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void restart(View v){
        accData=new ArrayList<>();
        chart.reset();
        chart=null;
        chart=new ChartView(mLineChart);
    }

    private void lineControl(Switch mSwitch, final int id){
        mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chart.getDataSets().get(id).setVisible(isChecked);
            }
        });
    }

    private void toast(String string){
        Toast toast = Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

}
