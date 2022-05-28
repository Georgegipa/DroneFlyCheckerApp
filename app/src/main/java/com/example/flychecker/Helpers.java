package com.example.flychecker;

public abstract class Helpers {
    //convert unix timestamp to time with date
    public static String convertUnixToDate(int unixTime,boolean return12HourFormat) {
        java.util.Date date = new java.util.Date(unixTime * 1000L);
        //change hour to 12 hour format
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getDefault());

        //concat amPm to the end
        String formatted= sdf.format(date);
        if(return12HourFormat) {//TODO: add 12hour format support
            //convert a 24hr format to 12hr format
            int hour = date.getHours();
            String amPm = hour >= 12 ? "PM" : "AM";
            if(hour > 12) {
                hour = hour - 12;
            }
            formatted = formatted.replace("HH", String.valueOf(hour));
            return formatted + " " + amPm;
        }
        else
            return formatted;
    }
}
