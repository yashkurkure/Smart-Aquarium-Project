package com.cs362.smartaquarium;

public class StatusObject {

    public String currentAlarm;
    public String currentRTC;
    public boolean light;
    public String temperature;
    public String waterLevel;
    public String waterLevelAnalog;
    public boolean feedAllowed;

    public int hr_RTC;
    public int min_RTC;

    public int hr_Alarm;
    public int min_Alarm;

    StatusObject()
    {

    }

    StatusObject(boolean light)
    {
        this.light = light;
    }

    StatusObject(String s)
    {
        infoParser(s);
    }

    private void infoParser(String result)
    {


        String temp;
        String[] tempA;

        result = result.replace('!', ' ').trim();

        String[] status = result.split(",");

        if(status[0].equals("L:ON"))
        {
            light = true;
        }
        else if(status[0].equals("L:OFF"))
        {
            light = false;
        }


        /*
        temp = status[1];
        tempA = temp.split(":");
        feedAllowed = tempA[1].equals("1");

         */

        temp = status[1];
        tempA = temp.split(":");
        temperature = tempA[1];

        temp = status[2];
        tempA = temp.split(":");
        currentAlarm = tempA[1] +":"+tempA[2];

        temp = status[3];
        tempA = temp.split(":");
        waterLevel = tempA[1];
        waterLevelAnalog = tempA[2];

        temp = status[4];
        tempA = temp.split(":");
        currentRTC = tempA[1] +":"+tempA[2];
    }



}