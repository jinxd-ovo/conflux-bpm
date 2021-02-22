package com.jeestudio.utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: Date util
 * @author: houxl
 * @author: whl
 * @Date: 2019-11-18
 */
public class DateUtil extends org.apache.commons.lang3.time.DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static Date parseDate(Object str) {
        if (str == null){
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     * Get current date string with pattern 'yyyy-MM-dd'
     * @return current date string
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * Get current date string with pattern.
     * @param pattern
     * @return current date string
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * Convert date to string time according to time type
     * @param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: time of string type
     */
    public static String dateToString(Date date, String timeType) {
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat(timeType);
        return simpleDateFormat.format(date);
    }

    /**
     * Convert string time to date according to time type
     * @Param: string time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: date
     * @exception: format exception
     */
    public static Date stringToDate(String date,String timeType) {
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat(timeType);
            return simpleDateFormat.parse(date);
        }catch (ParseException e){
            logger.warn(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Compare string type time size
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return:
     *          0: smallDate equal to largeDate
     *          1: smallDate less than largeDate
     *          2: smallDate greater than largeDate
     * @exception: format exception
     */
    public static String compareDate(String smallDate,String largeDate,String timeType){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeType);
        try {
            if ((simpleDateFormat.parse(smallDate)).before(simpleDateFormat.parse(largeDate))){
                return "1";
            }else if ((simpleDateFormat.parse(largeDate)).before(simpleDateFormat.parse(smallDate))){
                return "2";
            }else{
                return "0";
            }
        }catch (ParseException parse){
            return "";
        }
    }

    /**
     * Compare Date type time size
     * @Param: small time
     * @Param: big time
     * @return:
     *          0: smallDate equal to largeDate
     *          1: smallDate less than largeDate
     *          2: smallDate greater than largeDate
     */
    public static String compareDate(Date smallDate,Date largeDate){
        if (smallDate.before(largeDate)){
            return "1";
        }else if (largeDate.before(smallDate)){
            return "2";
        }else{
            return "0";
        }
    }

    /**
     * Calculate the number of days between two dates (String type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: days
     */
    public static int daysBetween(String smallDate,String largeDate,String timeType){
        if((compareDate(smallDate,largeDate,timeType)).equals("0")){
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(smallDate,timeType));
        long smallTime = calendar.getTimeInMillis();
        calendar.setTime(stringToDate(largeDate,timeType));
        long largeTime = calendar.getTimeInMillis();
        return Integer.parseInt(String.valueOf(Math.abs((largeTime-smallTime))/(1000*3600*24)));
    }

    /**
     * Calculate the number of days between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @return: days
     */
    public static int daysBetween(Date smallDate,Date largeDate){
        if((compareDate(smallDate,largeDate)).equals("0")){
            return 0;
        }else if((compareDate(smallDate,largeDate)).equals("2")){
            Date temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(smallDate);
        long smallTime = calendar.getTimeInMillis();
        calendar.setTime(largeDate);
        long largeTime = calendar.getTimeInMillis();
        return Integer.parseInt(String.valueOf((largeTime-smallTime)/(1000*3600*24)));
    }

    /**
     * Calculate the number of months between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: months
     */
    public static int monthsBetween(String smallDate,String largeDate,String timeType){
        if((compareDate(smallDate,largeDate,timeType)).equals("0")){
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(smallDate,timeType));
        long smallTime = calendar.get(Calendar.MONTH);
        calendar.setTime(stringToDate(largeDate,timeType));
        long largeTime = calendar.get(Calendar.MONTH);
        long sl = Math.abs(largeTime - smallTime) == 0 ? 1 : Math.abs(largeTime - smallTime);
        return Integer.parseInt(String.valueOf(sl));
    }

    /**
     * Calculate the number of months between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: months
     */
    public static int monthsBetween(Date smallDate,Date largeDate){
        if((compareDate(smallDate,largeDate)).equals("0")){
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(smallDate);
        long smallTime = calendar.get(Calendar.MONTH);
        calendar.setTime(largeDate);
        long largeTime = calendar.get(Calendar.MONTH);
        long sl = Math.abs(largeTime - smallTime) == 0 ? 1 : Math.abs(largeTime - smallTime);
        return Integer.parseInt(String.valueOf(sl));
    }

    /**
     * Calculate the number of years between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: years
     */
    public static int yearsBetween(String smallDate,String largeDate,String timeType){
        if((compareDate(smallDate,largeDate,timeType)).equals("0")){
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(smallDate,timeType));
        long smallTime = calendar.get(Calendar.YEAR);
        calendar.setTime(stringToDate(largeDate,timeType));
        long largeTime = calendar.get(Calendar.YEAR);
        long sl = Math.abs(largeTime - smallTime) == 0 ? 1 : Math.abs(largeTime - smallTime);
        return Integer.parseInt(String.valueOf(sl));
    }

    /**
     * Calculate the number of years between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @return: years
     */
    public static int yearsBetween(Date smallDate,Date largeDate){
        if((compareDate(smallDate,largeDate)).equals("0")){
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(smallDate);
        long smallTime = calendar.get(Calendar.YEAR);
        calendar.setTime(largeDate);
        long largeTime = calendar.get(Calendar.YEAR);
        long sl = Math.abs(largeTime - smallTime) == 0 ? 1 : Math.abs(largeTime - smallTime);
        return Integer.parseInt(String.valueOf(sl));
    }

    /**
     * Calculate working days between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: date list
     */
    public static List<Date> workDays(String smallDate, String largeDate, String timeType){
        List<Date> list = new ArrayList<Date>();
        if((compareDate(smallDate,largeDate,timeType)).equals("2")){
            String temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(stringToDate(smallDate,timeType));
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(stringToDate(largeDate,timeType));
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) != 7 && smallCalendar.get(Calendar.DAY_OF_WEEK) != 1)
                list.add(smallCalendar.getTime());
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate working days between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @return: date list
     */
    public static List<Date> workDays(Date smallDate, Date largeDate){
        List<Date> list = new ArrayList<Date>();
        if((compareDate(smallDate,largeDate)).equals("2")){
            Date temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(smallDate);
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(largeDate);
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) != 7 && smallCalendar.get(Calendar.DAY_OF_WEEK) != 1)
                list.add(smallCalendar.getTime());
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate working days between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> workDaysString(String smallDate, String largeDate, String timeType){
        List<String> list = new ArrayList<String>();
        if((compareDate(smallDate,largeDate,timeType)).equals("2")){
            String temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(stringToDate(smallDate,timeType));
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(stringToDate(largeDate,timeType));
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) != 7 && smallCalendar.get(Calendar.DAY_OF_WEEK) != 1)
                list.add(dateToString(smallCalendar.getTime(),timeType));
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate working days between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> workDaysString(Date smallDate, Date largeDate, String timeType){
        List<String> list = new ArrayList<String>();
        if((compareDate(smallDate,largeDate)).equals("2")){
            Date temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(smallDate);
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(largeDate);
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) != 7 && smallCalendar.get(Calendar.DAY_OF_WEEK) != 1)
                list.add(dateToString(smallCalendar.getTime(),timeType));
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate the rest day between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: date list
     */
    public static List<Date> restDays(String smallDate, String largeDate, String timeType){
        List<Date> list = new ArrayList<Date>();
        if((compareDate(smallDate,largeDate,timeType)).equals("2")){
            String temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(stringToDate(smallDate,timeType));
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(stringToDate(largeDate,timeType));
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) == 7 || smallCalendar.get(Calendar.DAY_OF_WEEK) == 1)
                list.add(smallCalendar.getTime());
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate the rest day between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: date list
     */
    public static List<Date> restDays(Date smallDate, Date largeDate){
        List<Date> list = new ArrayList<Date>();
        if((compareDate(smallDate,largeDate)).equals("2")){
            Date temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(smallDate);
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(largeDate);
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) == 7 || smallCalendar.get(Calendar.DAY_OF_WEEK) == 1)
                list.add(smallCalendar.getTime());
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate the rest day between two dates (string type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> restDaysString(String smallDate, String largeDate, String timeType){
        List<String> list = new ArrayList<String>();
        if((compareDate(smallDate,largeDate,timeType)).equals("2")){
            String temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(stringToDate(smallDate,timeType));
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(stringToDate(largeDate,timeType));
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) == 7 || smallCalendar.get(Calendar.DAY_OF_WEEK) == 1)
                list.add(dateToString(smallCalendar.getTime(),timeType));
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Calculate working days between two dates (date type)
     * @Param: small time
     * @Param: big time
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> restDaysString(Date smallDate, Date largeDate, String timeType){
        List<String> list = new ArrayList<String>();
        if((compareDate(smallDate,largeDate)).equals("2")){
            Date temp = largeDate;
            largeDate = smallDate;
            smallDate = temp;
        }
        Calendar smallCalendar = Calendar.getInstance();
        smallCalendar.setTime(smallDate);
        Calendar largeCalendar = Calendar.getInstance();
        largeCalendar.setTime(largeDate);
        while (smallCalendar.compareTo(largeCalendar) <= 0) {
            if (smallCalendar.get(Calendar.DAY_OF_WEEK) == 7 || smallCalendar.get(Calendar.DAY_OF_WEEK) == 1)
                list.add(dateToString(smallCalendar.getTime(),timeType));
            smallCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    /**
     * Get weeks before / after current time
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @Param: several weeks
     *          > 0 after several weeks
     *          = 0 today
     *          < 0 before several weeks
     * @return: date
     */
    public static String getNextWeekDay(String date,String timeType,int weekNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.add(Calendar.DATE,7 * weekNum);
        return dateToString(calendar.getTime(),timeType);
    }

    /**
     * Get weeks before / after current time
     * @Param: date
     * @Param: several weeks
     *          > 0 after several weeks
     *          = 0 today
     *          < 0 before several weeks
     * @return: date
     */
    public static Date getNextWeekDay(Date date,int weekNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,7 * weekNum);
        return calendar.getTime();
    }

    /**
     * Get the specific time range of weeks before / after the current time
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @Param: several weeks
     *          > 0 after several weeks
     *          = 0 today
     *          < 0 before several weeks
     * @return: string list
     */
    public static List<String> getWeekRange(String date,String timeType,int weekNum){
        List<String> list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.add(Calendar.DATE,7 * weekNum);
        list = getWeekStartEnd(dateToString(calendar.getTime(),timeType),timeType);
        return list;
    }

    /**
     * Get the specific time range of weeks before / after the current time
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @Param: several weeks
     *          > 0 after several weeks
     *          = 0 today
     *          < 0 before several weeks
     * @return: string list
     */
    public static List<Date> getWeekRange(Date date,int weekNum){
        List<Date> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,7 * weekNum);
        list = getWeekStartEnd(calendar.getTime());
        return list;
    }

    /**
     * Get the start and end of the week
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> getWeekStartEnd(String date,String timeType){
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayWeek == 1){
            dayWeek = 8;
        }
        calendar.add(Calendar.DATE,calendar.getFirstDayOfWeek() - dayWeek);
        list.add(dateToString(calendar.getTime(),timeType));
        calendar.add(Calendar.DATE,calendar.getFirstDayOfWeek() + dayWeek);
        list.add(dateToString(calendar.getTime(),timeType));
        return list;
    }

    /**
     * Get the start and end of the week
     * @Param: date
     * @return: string list
     */
    public static List<Date> getWeekStartEnd(Date date){
        List<Date> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayWeek == 1){
            dayWeek = 8;
        }
        calendar.add(Calendar.DATE,calendar.getFirstDayOfWeek() - dayWeek);
        list.add(calendar.getTime());
        calendar.add(Calendar.DATE,calendar.getFirstDayOfWeek() + dayWeek);
        list.add(calendar.getTime());
        return list;
    }

    /**
     * Get months before / after current time
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @Param: several months
     *          > 0 after several months
     *          = 0 today
     *          < 0 before several months
     * @return: date
     */
    public static String getNextMonthDay(String date,String timeType,int monthNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.add(Calendar.MONTH,1 * monthNum);
        return dateToString(calendar.getTime(),timeType);
    }

    /**
     * Get months before / after current time
     * @Param: date
     * @Param: several months
     *          > 0 after several months
     *          = 0 today
     *          < 0 before several months
     * @return: date
     */
    public static Date getNextMonthDay(Date date,int monthNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1 * monthNum);
        return calendar.getTime();
    }

    /**
     * Get the start and end of the month
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> getMonthStartEnd(String date,String timeType){
        List<String> list = new ArrayList<>();
        GregorianCalendar gregorianCalendar = (GregorianCalendar)Calendar.getInstance();
        gregorianCalendar.setTime(stringToDate(date,timeType));
        gregorianCalendar.set(Calendar.DAY_OF_MONTH,1);
        list.add(dateToString(gregorianCalendar.getTime(),timeType));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DATE));
        list.add(dateToString(calendar.getTime(),timeType));
        return list;
    }

    public static String getMonthEnd(String date, String timeType){
        GregorianCalendar gregorianCalendar = (GregorianCalendar)Calendar.getInstance();
        gregorianCalendar.setTime(stringToDate(date,timeType));
        gregorianCalendar.set(Calendar.DAY_OF_MONTH,1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DATE));
        return dateToString(calendar.getTime(),timeType);
    }

    /**
     * Get the start and end of the month
     * @Param: date
     * @return: date list
     */
    public static List<Date> getMonthStartEnd(Date date){
        List<Date> list = new ArrayList<>();
        GregorianCalendar gregorianCalendar = (GregorianCalendar)Calendar.getInstance();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH,1);
        list.add(gregorianCalendar.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DATE));
        list.add(calendar.getTime());
        return list;
    }

    /**
     * Get days before / after current time
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @Param: several days
     *          > 0 after several days
     *          = 0 today
     *          < 0 before several days
     * @return: date
     */
    public static String getNextDay(String date,String timeType,int weekNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(stringToDate(date,timeType));
        calendar.add(Calendar.DATE,1 * weekNum);
        return dateToString(calendar.getTime(),timeType);
    }

    /**
     * Get days before / after current time
     * @Param: date
     * @Param: several days
     *          > 0 after several days
     *          = 0 today
     *          < 0 before several days
     * @return: date
     */
    public static Date getNextDay(Date date,int weekNum){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,1 * weekNum);
        return calendar.getTime();
    }

    /**
     * Get day start and end times
     * @Param: date
     * @Param: time type, eg. 'yyyy-MM-dd'
     * @return: string list
     */
    public static List<String> getDayRange(String date,String timeType){
        List<String> list = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(stringToDate(date, timeType));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        list.add(dateToString(calendar.getTime(),timeType));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        list.add(dateToString(calendar.getTime(),timeType));
        return list;
    }

    /**
     * Get day start and end times
     * @Param: date
     * @return: date list
     */
    public static List<Date> getDayRange(Date date){
        List<Date> list = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        list.add(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        list.add(calendar.getTime());
        return list;
    }
}