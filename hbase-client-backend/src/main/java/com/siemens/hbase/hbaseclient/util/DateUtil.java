package com.siemens.hbase.hbaseclient.util;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static com.siemens.hbase.hbaseclient.util.BaseConstant.*;

/**
 * @author zxp.ext@siemens.com
 * @date 2019/04/09
 * @Description 日期时间工具类
 */
public final class DateUtil {

    private DateUtil(){}

    /**
     * 计算开始时间和结束时间内，所有满足间隔的时间集合
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param gap 时间间隔
     * @return
     */
    public static List<Date> getTimeSegment(Date beginTime, Date endTime, Integer gap) {
        List<Date> result = new ArrayList<>();
        if (beginTime == null || endTime == null || gap == null) {
            return result;
        }
        if (beginTime.compareTo(endTime) >= 0) {
            return result;
        }
        Date firstGapDate = addOffsetMinute(beginTime,gap);
        if (firstGapDate.compareTo(endTime) > 0) {
            return result;
        }
        while (beginTime.compareTo(endTime) <= 0) {
            result.add(beginTime);
            beginTime = addOffsetMinute(beginTime, gap);
        }
        return result;
    }

    /**
     * 计算指定时间间隔偏移指定分钟的日期
     * @param time 需要计算的日期
     * @param offset 分钟间隔
     * @return 偏移的时间
     */
    public static Date addOffsetMinute(Date time, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.MINUTE, offset);
        return c.getTime();
    }

    /**
     * 计算指定时间间隔偏移指定天的日期
     * @param time 需要计算的日期
     * @param offset 天间隔
     * @return 偏移的时间
     */
    public static Date addOffsetDay(Date time, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.DAY_OF_MONTH, offset);
        return c.getTime();
    }

    /**
     * 计算开始时间和结束时间相差小时数
     * @param beginTime 需要计算的日期
     * @param endTime 时间间隔
     * @return 偏移小时
     */
    public static float getHourGap(Date beginTime, Date endTime) {
        if (beginTime == null || endTime == null) {
            return 0;
        }
        long begin = beginTime.getTime();
        long end = endTime.getTime();
        float gap = (end - begin)/(float)(60*60*1000);
        return gap;
    }

    /**
     * 获取指定时间对应的分钟
     * @param time 指定时间
     * @return 指定时间-分钟
     */
    public static int getMinutesOfTime(Date time){
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取秒和毫秒都为0值的当前时间
     * @return 秒和毫秒都为0的时间
     */
    public static Date getNowZeroSecondAndMillisecondDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }

    /**
     * 获取秒和毫秒都为0值的当前时间戳
     * @return 秒和毫秒都为0的时间戳
     */
    public static Long getNowZeroSecondAndMillisecondDate(Long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }
    /**
     * 获取秒和毫秒都为0值的当前时间戳
     * @return 秒和毫秒都为0的时间戳
     */
    public static Long getZeroMillisecondDate(Long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }
    /**
     * 计算两个日期之间相差的天数
     * @param beginTime 开始的时间
     * @param endTime  结束的时间
     * @return 相差天数
     */
    public static Double daysBetween(Long beginTime,Long endTime) {
        return (double)(endTime - beginTime) / BaseConstant.ONE_DAY_MILLISECOND;
    }

    /**
     * 将当前时间格式化成yyyy-MM-dd HH:m0:ss的格式，及分钟一直是10的倍数 如：00,10,20,30,40,50
     * @param date
     * @return
     */
    public static Date setMinIsTen(Date date){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        int minute = rightNow.get(Calendar.MINUTE);
        minute = Math.round(minute/BaseConstant.FLOAT_TEN*BaseConstant.FLOAT_TEN);
        /**计算10的整数分钟*/
        rightNow.set(Calendar.MINUTE, minute);
        rightNow.set(Calendar.SECOND, 0);
        return rightNow.getTime();
    }

    /**
     * 将当前时间秒格式化
     * @param date
     * @return
     */
    public static Date setSecondIsZero(Date date){
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(Calendar.SECOND, 0);
        return rightNow.getTime();
    }
    /**
     * 获取当前时间的i个小时后，如i为8，就是8小时后，-8则为8小时前
     * @param date
     * @param i
     * @return
     */
    public static Date getHourByTimeZone(Date date , int i){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, i);
        return cal.getTime();
    }

    /**
     * 获取分钟数
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 将当前给定时间向整10分钟后靠
     * @param timestamp 指定时间时间戳
     * @return 当前分钟靠近后一个整十分钟最近的时间戳
     */
    public static Long towardAfterMultipleTen(Long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute >= 0 && minute <= TEN) {
            minute = TEN;
        } else if (minute > TEN && minute <= TWENTY) {
            minute = TWENTY;
        } else if (minute > TWENTY && minute <= THIRTY) {
            minute = THIRTY;
        } else if (minute > THIRTY && minute <= FORTY) {
            minute = FORTY;
        } else if (minute > FORTY && minute <= FIFTY) {
            minute = FIFTY;
        } else {
            minute = SIXTY;
        }
        if (SIXTY.equals(minute)) {
            calendar.set(Calendar.MINUTE, INITIAL_ZERO_INT);
            calendar.add(Calendar.HOUR_OF_DAY, ONE);
        } else {
            calendar.set(Calendar.MINUTE, minute);
        }
        calendar.set(Calendar.SECOND, INITIAL_ZERO_INT);
        calendar.set(Calendar.MILLISECOND, INITIAL_ZERO_INT);
        return calendar.getTimeInMillis();
    }

    /**
     * 将当前给定时间向整10分钟前靠
     * @param timestamp 指定时间时间戳
     * @return 当前分钟靠近前一个整十分钟最近的时间戳
     */
    public static Long towardBeforeMultipleTen(Long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute >= 0 && minute <= TEN) {
            minute = INITIAL_ZERO_INT;
        } else if (minute > TEN && minute <= TWENTY) {
            minute = TEN;
        } else if (minute > TWENTY && minute <= THIRTY) {
            minute = TWENTY;
        } else if (minute > THIRTY && minute <= FORTY) {
            minute = THIRTY;
        } else if (minute > FORTY && minute <= FIFTY) {
            minute = FORTY;
        } else {
            minute = FIFTY;
        }
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, INITIAL_ZERO_INT);
        calendar.set(Calendar.MILLISECOND, INITIAL_ZERO_INT);
        return calendar.getTimeInMillis();
    }
}
