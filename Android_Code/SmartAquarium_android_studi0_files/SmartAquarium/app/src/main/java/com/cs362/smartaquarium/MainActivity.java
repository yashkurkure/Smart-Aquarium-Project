package com.cs362.smartaquarium;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PatternMatcher;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    Handler uiHandler;

    final static int UPDATE = 1;
    final static int ERROR = 2;
    //final static int POST_TO_TERMINAL = 3; //v.1.1


    //flag to prevent light button update loop
    boolean lightBtnChangeBySystem = false;


    //GUI
    ToggleButton lightBtn ;

    Button feedBtn;
    TextView temperatureTv;
    TextView waterLevelTv;
    TextView waterLevelAnalogTv;
    TextView rtcTimeTV;
    TextView autoFeedTv;
    EditText ipInputText;
    Button changeAutoFeedBtn;
    Button changeRtcTimeBtn;
    Button updateBtn;

    //TextView terminal; //v1.1

    httpThread t;

    static String ip = "N/A";

    int hr_RTC, min_RTC;

    int hr_Alarm, min_Alarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);







        initUiHandler();

        //UI

        lightBtn = findViewById(R.id.lightBtn);
        feedBtn = findViewById(R.id.foodBtn);
        changeAutoFeedBtn = findViewById(R.id.editAutoFeedBtn);
        changeRtcTimeBtn = findViewById(R.id.editRTCTimeBtn);
        temperatureTv = findViewById(R.id.temperatureTxt);
        waterLevelTv = findViewById(R.id.waterLevelTxt);
        rtcTimeTV = findViewById(R.id.rtcTimeTxt);
        autoFeedTv = findViewById(R.id.autoFeedTxt);
        updateBtn = findViewById(R.id.updateBtn);
        ipInputText = findViewById(R.id.ipEditText);
        waterLevelAnalogTv = findViewById(R.id.waterLevelAnalogTxt);

        //terminal = findViewById(R.id.terminal);

        setButtonListeners();







    }


    private void setButtonListeners()
    {
        ipInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String input = s.toString().trim();
                MainActivity.ip = input;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String input = s.toString().trim();
                MainActivity.ip = input;

            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = s.toString().trim();
                MainActivity.ip = input;

            }
        });




        //Listeners


        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lightBtn.isChecked())
                {
                    lightBtn.setBackground(getDrawable(R.drawable.bulb_selected));
                    lightBtn.setForeground(getDrawable(R.drawable.bulb_selected));

                }
                else
                {
                    lightBtn.setBackground(getDrawable(R.drawable.bulb_unselected));
                    lightBtn.setForeground(getDrawable(R.drawable.bulb_unselected));
                }



                if(!isIpValid()) return;

                String btxt = lightBtn.getText().toString();

                StatusObject sObj = new StatusObject(lightBtn.isChecked());
                //terminal.setText(Boolean.toString(btxt.equals("ON")));
                Message msg = new Message();
                msg.what = httpHandler.TOGGLE_LIGHT;
                msg.obj = sObj;


                t.myHandler.sendMessage(msg);

            }


        });



        /*
        feedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isIpValid()) return;


                //StatusObject sObj = new StatusObject(lightBtn.isSelected());
                Message msg = new Message();
                msg.what = httpHandler.START_STEPPER;
                //msg.obj = sObj;


                t.myHandler.sendMessage(msg);

            }
        });

         */

        /*
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isIpValid()) return;


                //StatusObject sObj = new StatusObject(lightBtn.isSelected());
                Message msg = new Message();
                msg.what = httpHandler.GET_UPDATE;
                //msg.obj = sObj;


                t.myHandler.sendMessage(msg);

            }
        });

         */

        changeRtcTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isIpValid()) return;




                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hr_RTC = hourOfDay;
                                min_RTC = minute;

                                Toast.makeText(MainActivity.this,"RTC Time = " + hr_RTC +":"+min_RTC,Toast.LENGTH_SHORT).show();

                                StatusObject sObj = new StatusObject();
                                sObj.hr_RTC = hr_RTC;
                                sObj.min_RTC = min_RTC;
                                Message msg = new Message();
                                msg.what = httpHandler.CHANGE_RTC;
                                msg.obj = sObj;


                                t.myHandler.sendMessage(msg);


                            }
                        }, 12,0,true);

                tpd.updateTime(hr_RTC,min_RTC);
                tpd.show();




            }

        });

        changeAutoFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isIpValid()) return;


                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hr_Alarm = hourOfDay;
                                min_Alarm = minute;

                                Toast.makeText(MainActivity.this,"Alarm Time = " + hr_Alarm +":"+min_Alarm,Toast.LENGTH_SHORT).show();

                                StatusObject sObj = new StatusObject();
                                sObj.hr_Alarm = hr_Alarm;
                                sObj.min_Alarm = min_Alarm;
                                Message msg = new Message();
                                msg.what = httpHandler.CHANGE_ALARM;
                                msg.obj = sObj;


                                t.myHandler.sendMessage(msg);


                            }
                        }, 12,0,true);


                tpd.updateTime(hr_Alarm,min_Alarm);
                tpd.show();

            }
        });



        updateBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button view = (Button) v;

                        view.setBackground(getDrawable(R.drawable.connect_unselected));
                        view.setForeground(getDrawable(R.drawable.connect_unselected));
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        Button view = (Button) v;
                        view.setBackground(getDrawable(R.drawable.connect_selected));
                        view.setForeground(getDrawable(R.drawable.connect_selected));


                        if(!isIpValid()) break;


                        //StatusObject sObj = new StatusObject(lightBtn.isSelected());
                        Message msg = new Message();
                        msg.what = httpHandler.GET_UPDATE;
                        //msg.obj = sObj;


                        t.myHandler.sendMessage(msg);



                        break;
                    }


                }
                return true;
            }
        });

        feedBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button view = (Button) v;

                        view.setBackground(getDrawable(R.drawable.fish_unselected));
                        view.setForeground(getDrawable(R.drawable.fish_unselected));
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        Button view = (Button) v;
                        view.setBackground(getDrawable(R.drawable.fish_selected));
                        view.setForeground(getDrawable(R.drawable.fish_selected));

                        if(!isIpValid()) break;


                        //StatusObject sObj = new StatusObject(lightBtn.isSelected());
                        Message msg = new Message();
                        msg.what = httpHandler.START_STEPPER;
                        //msg.obj = sObj;


                        t.myHandler.sendMessage(msg);



                        break;
                    }


                }
                return true;
            }
        });



/*
        lightBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    buttonView.setBackground(getDrawable(R.drawable.bulb_selected));
                    buttonView.setForeground(getDrawable(R.drawable.bulb_selected));

                    if(!lightBtnChangeBySystem)
                    {
                        if(!isIpValid()) return;

                        StatusObject sObj = new StatusObject(isChecked);
                        //terminal.setText(Boolean.toString(btxt.equals("ON")));
                        Message msg = new Message();
                        msg.what = httpHandler.TOGGLE_LIGHT;
                        msg.obj = sObj;

                        t.myHandler.sendMessage(msg);

                    }


                }
                else
                {
                    buttonView.setBackground(getDrawable(R.drawable.bulb_unselected));
                    buttonView.setForeground(getDrawable(R.drawable.bulb_unselected));

                    if(!lightBtnChangeBySystem)
                    {
                        if(!isIpValid()) return;

                        StatusObject sObj = new StatusObject(isChecked);
                        //terminal.setText(Boolean.toString(btxt.equals("ON")));
                        Message msg = new Message();
                        msg.what = httpHandler.TOGGLE_LIGHT;
                        msg.obj = sObj;

                        t.myHandler.sendMessage(msg);

                    }

                }



                lightBtnChangeBySystem = false;
            }
        }); */
    }

    private void initUiHandler()
    {
        this.uiHandler = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg)
            {
                int what = msg.what;

                switch(what)
                {
                    case UPDATE:
                    {
                        StatusObject sObj = (StatusObject) msg.obj;

                        temperatureTv.setText(sObj.temperature.substring(0,2) + " °C");
                        waterLevelTv.setText(sObj.waterLevel);
                        waterLevelAnalogTv.setText("Analog: " + sObj.waterLevelAnalog);
                        autoFeedTv.setText(sObj.currentAlarm);
                        rtcTimeTV.setText(sObj.currentRTC);

                        lightBtn.setChecked(sObj.light);
                        if(lightBtn.isChecked())
                        {
                            lightBtn.setBackground(getDrawable(R.drawable.bulb_selected));
                            lightBtn.setForeground(getDrawable(R.drawable.bulb_selected));

                        }
                        else
                        {
                            lightBtn.setBackground(getDrawable(R.drawable.bulb_unselected));
                            lightBtn.setForeground(getDrawable(R.drawable.bulb_unselected));
                        }

                        break;
                    }

                    case ERROR:
                    {
                        Toast.makeText(MainActivity.this, "Error getting result!", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    //case POST_TO_TERMINAL:
                   // {
                       // String txt = (String) msg.obj;

                        //terminal.setText(txt);


                        //break;
                    //}

                }

            }

        };
        t = new httpThread(uiHandler, ip);
        t.start();
    }

    public boolean isIpValid()
    {

        if(this.ip.equals("N/A"))
        {
            Toast.makeText(MainActivity.this, "Enter IP!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";
        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(this.ip);


        if(!m.matches())
        {
            Toast.makeText(MainActivity.this, this.ip + " is not a valid IP!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

}