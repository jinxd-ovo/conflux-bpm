package com.jeestudio.utils;

import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * @Description: Time util
 * @author: houxl
 * @Date: 2019-11-20
 */
public class TimeUtils {

    public static String toTimeString(long time) {
        TimeUtils t = new TimeUtils(time);
        int day = t.get(TimeUtils.DAY);
        int hour = t.get(TimeUtils.HOUR);
        int minute = t.get(TimeUtils.MINUTE);
        int second = t.get(TimeUtils.SECOND);
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("天");
        }
        if (hour > 0) {
            sb.append(hour).append("时");
        }
        if (minute > 0) {
            sb.append(minute).append("分");
        }
        if (second > 0) {
            sb.append(second).append("秒");
        }
        return sb.toString();
    }

    public static String toTimeString_EN(long time) {
        TimeUtils t = new TimeUtils(time);
        int day = t.get(TimeUtils.DAY);
        int hour = t.get(TimeUtils.HOUR);
        int minute = t.get(TimeUtils.MINUTE);
        int second = t.get(TimeUtils.SECOND);
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("days");
        }
        if (hour > 0) {
            sb.append(hour).append("hours");
        }
        if (minute > 0) {
            sb.append(minute).append("minute");
        }
        if (second > 0) {
            sb.append(second).append("second");
        }
        return sb.toString();
    }

    public final static int SECOND = 0;

    public final static int MINUTE = 1;

    public final static int HOUR = 2;

    public final static int DAY = 3;

    private final int[] maxFields = {59, 59, 23, Integer.MAX_VALUE - 1};

    private final int[] minFields = {0, 0, 0, Integer.MIN_VALUE};

    private String timeSeparator = ":";

    private int[] fields = new int[4];

    public TimeUtils() {
        this(0, 0, 0, 0);
    }

    public TimeUtils(int hour, int minute) {
        this(0, hour, minute, 0);
    }

    public TimeUtils(int hour, int minute, int second) {
        this(0, hour, minute, second);
    }

    public TimeUtils(String time) {
        this(time, null);
    }

    public TimeUtils(long time) {
        this(new Date(time));
    }

    public TimeUtils(Date date) {
        this(DateFormatUtils.formatUTC(date, "HH:mm:ss"));
    }

    public TimeUtils(int day, int hour, int minute, int second) {
        initialize(day, hour, minute, second);
    }

    public TimeUtils(String time, String timeSeparator) {
        if (timeSeparator != null) {
            setTimeSeparator(timeSeparator);
        }
        parseTime(time);
    }

    public void set(int field, int value) {
        if (value < minFields[field]) {
            throw new IllegalArgumentException(value + ", time value must be positive.");
        }
        fields[field] = value % (maxFields[field] + 1);
        int carry = value / (maxFields[field] + 1);
        if (carry > 0) {
            int upFieldValue = get(field + 1);
            set(field + 1, upFieldValue + carry);
        }
    }

    public int get(int field) {
        if (field < 0 || field > fields.length - 1) {
            throw new IllegalArgumentException(field + ", field value is error.");
        }
        return fields[field];
    }

    public TimeUtils addTime(TimeUtils time) {
        TimeUtils result = new TimeUtils();
        int up = 0;     // 进位标志
        for (int i = 0; i < fields.length; i++) {
            int sum = fields[i] + time.fields[i] + up;
            up = sum / (maxFields[i] + 1);
            result.fields[i] = sum % (maxFields[i] + 1);
        }
        return result;
    }

    public TimeUtils subtractTime(TimeUtils time) {
        TimeUtils result = new TimeUtils();
        int down = 0;
        for (int i = 0, k = fields.length - 1; i < k; i++) {
            int difference = fields[i] + down;
            if (difference >= time.fields[i]) {
                difference -= time.fields[i];
                down = 0;
            } else {
                difference += maxFields[i] + 1 - time.fields[i];
                down = -1;
            }
            result.fields[i] = difference;
        }
        result.fields[DAY] = fields[DAY] - time.fields[DAY] + down;
        return result;
    }

    public String getTimeSeparator() {
        return timeSeparator;
    }

    public void setTimeSeparator(String timeSeparator) {
        this.timeSeparator = timeSeparator;
    }

    private void initialize(int day, int hour, int minute, int second) {
        set(DAY, day);
        set(HOUR, hour);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    private void parseTime(String time) {
        if (time == null) {
            initialize(0, 0, 0, 0);
            return;
        }
        String t = time;
        int field = DAY;
        set(field--, 0);
        int p = -1;
        while ((p = t.indexOf(timeSeparator)) > -1) {
            parseTimeField(time, t.substring(0, p), field--);
            t = t.substring(p + timeSeparator.length());
        }
        parseTimeField(time, t, field--);
    }

    private void parseTimeField(String time, String t, int field) {
        if (field < SECOND || t.length() < 1) {
            parseTimeException(time);
        }
        char[] chs = t.toCharArray();
        int n = 0;
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] <= ' ') {
                continue;
            }
            if (chs[i] >= '0' && chs[i] <= '9') {
                n = n * 10 + chs[i] - '0';
                continue;
            }
            parseTimeException(time);
        }
        set(field, n);
    }

    private void parseTimeException(String time) {
        throw new IllegalArgumentException(time + ", time format error, HH"
                + this.timeSeparator + "mm" + this.timeSeparator + "ss");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(16);
        sb.append(fields[DAY]).append(',').append(' ');
        buildString(sb, HOUR).append(timeSeparator);
        buildString(sb, MINUTE).append(timeSeparator);
        buildString(sb, SECOND);
        return sb.toString();
    }

    private StringBuilder buildString(StringBuilder sb, int field) {
        if (fields[field] < 10) {
            sb.append('0');
        }
        return sb.append(fields[field]);
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + Arrays.hashCode(fields);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TimeUtils other = (TimeUtils) obj;
        if (!Arrays.equals(fields, other.fields)) {
            return false;
        }
        return true;
    }
}
