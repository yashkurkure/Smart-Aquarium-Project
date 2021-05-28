package com.cs362.smartaquarium;

import android.os.Handler;
import android.os.Looper;

public class httpThread extends Thread {

    public httpHandler myHandler;
    private Handler uiHandler;
    private String ip;


    public httpThread(Handler uiHandler, String ip)
    {
        this.uiHandler = uiHandler;
        this.ip = ip;
    }

    @Override
    public void run() {
        super.run();

        Looper.prepare();

        myHandler = new httpHandler(uiHandler, ip);

        Looper.loop();

    }
}