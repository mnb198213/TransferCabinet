package com.jintoufs.zj.transfercabinet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil
{
  private static Locale locale = Locale.CHINA;
  
  public static String DateToDayString(Date paramDate)
  {
    return new SimpleDateFormat("MM-dd").format(paramDate);
  }
  
  public static String DateToString(Date paramDate)
  {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale).format(paramDate);
  }
  
  public static String DateToTimeString(Date paramDate)
  {
    return new SimpleDateFormat("HH:mm").format(paramDate);
  }
  
  public static Calendar StringToCalendar(String paramString)
  {
    Date dete;
    dete = StringToDate(paramString);
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(dete);
    return localCalendar;
  }
  
  public static Date StringToDate(String paramString)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      Date date;
    try {
      date = localSimpleDateFormat.parse(paramString);
      return date;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Date StringToDate(String paramString1, String paramString2)
  {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString2);

    try {
      Date date = simpleDateFormat.parse(paramString1);
      return date;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static long getNowUnixTimeStamp()
  {
    return new Date().getTime() / 1000L;
  }
  
  public static String timeStampToStr(long paramLong)
  {
    return new Date(1000L * paramLong).toLocaleString();
  }
}