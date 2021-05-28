package com.cs362.smartaquarium;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpHandler extends Handler {

    final static int TOGGLE_LIGHT = 11;
    final static int START_STEPPER = 12;
    final static int GET_UPDATE = 13;
    final static int CHANGE_ALARM = 14;
    final static int CHANGE_RTC = 15;

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;


    //UI handler
    private Handler uiHandler;

    private String ip;

    public httpHandler(Handler uiHandler, String ip)
    {
        this.uiHandler = uiHandler;
        this.ip = ip.trim();
    }

    public void handleMessage(Message msg)
    {
        int what = msg.what;

        switch(what)
        {
            case TOGGLE_LIGHT:
            {
                boolean lightState = ((StatusObject) msg.obj).light;
                String cmnd = "http://" + MainActivity.ip + "/" + (lightState ? "!L:ON!":"!L:OFF!");
                Log.i("toggle lt", cmnd);

                String result = requestCommand(cmnd);

                if (sendErrorMsgOnNull(result)) break;

                //result = parseArduinoResponse(result); //test
                //sendTextMessage(result); //test
                sendUpdateMessage(result);

                break;
            }

            case START_STEPPER:
            {
                String cmnd = "http://" + MainActivity.ip  + "/" + "!S:START!";

                String result = requestCommand(cmnd);

                //if (sendErrorMsgOnNull(result)) break;

                //result = parseArduinoResponse(result); //test
                //sendTextMessage(result); //test
                //sendUpdateMessage(result);
                break;
            }

            case GET_UPDATE:
            {
                String cmnd = "http://" + MainActivity.ip  + "/" + "!U:ALL!";


                String result = requestCommand(cmnd);

                if (sendErrorMsgOnNull(result)) break;

                //result = parseArduinoResponse(result); //test
                //sendTextMessage(result); //test
                this.sendUpdateMessage(result);
                break;
            }

            case CHANGE_ALARM:
            {
                StatusObject sObj = (StatusObject) msg.obj;
                int hr = sObj.hr_Alarm;
                int min = sObj.min_Alarm;

                String cmnd = "http://" + MainActivity.ip + "/" + "!CA:"+ hr + ","+ min+"!";

                Log.i("httpAlarm", cmnd);

                String result = requestCommand(cmnd);

                if (sendErrorMsgOnNull(result)) break;

                //result = parseArduinoResponse(result); //test
                //sendTextMessage(result); //test
                this.sendUpdateMessage(result);
                break;


            }

            case CHANGE_RTC:
            {
                StatusObject sObj = (StatusObject) msg.obj;
                int hr = sObj.hr_RTC;
                int min = sObj.min_RTC;

                String cmnd = "http://" + MainActivity.ip  + "/" + "!CRTC:"+ hr + ","+ min+"!";


                String result = requestCommand(cmnd);

                if (sendErrorMsgOnNull(result)) break;

                //result = parseArduinoResponse(result); //test
                //sendTextMessage(result); //test
                this.sendUpdateMessage(result);
                break;

            }
        }


        //getLooper().quit();

    }







    private String requestCommand(String sUrl){

        String inputLine;
        String result;

        try {

            Log.i("cmnd", sUrl);
            URL myUrl = new URL(sUrl);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);

            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();

        }



        catch (Exception e) {
            e.printStackTrace();
            result = null;
        }



        return result;
    }



    private void sendUpdateMessage(String result)
    {
        StatusObject obj = new StatusObject(result);

        Message msg = new Message();
        msg.what = MainActivity.UPDATE;
        msg.obj = obj;
        uiHandler.sendMessage(msg);

    }


    /* v1.1
    private void sendTextMessage(String result)
    {


        Message msg = new Message();
        msg.what = MainActivity.POST_TO_TERMINAL;
        msg.obj = result;
        uiHandler.sendMessage(msg);

    }*/

    private boolean sendErrorMsgOnNull(String result)
    {
        boolean flag = false;
        if(result == null)
        {
            flag = true;
            Message msg = new Message();
            msg.what = MainActivity.ERROR;
            uiHandler.sendMessageAtFrontOfQueue(msg);

        }
        return flag;
    }

}
