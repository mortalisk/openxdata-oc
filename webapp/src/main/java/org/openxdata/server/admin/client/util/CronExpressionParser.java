package org.openxdata.server.admin.client.util;

/**
 * Parses Cron expressions
 * @author Ronald.K
 */
public class CronExpressionParser {

    private final CronEntity mSecond;
    private final CronEntity mMinute;
    private final CronEntity mHour;
    private final CronEntity mDayOfMonth;
    private final CronEntity mMonth;
    private final CronEntity mDayOfWeek;

    public CronExpressionParser(String line) {
        String[] split   = line.split(" ");
        mSecond          = new CronEntity(split[0], 0, 59, CronEntity.SECOND);
        mMinute          = new CronEntity(split[1], 0, 59, CronEntity.MINUTE);
        mHour            = new CronEntity(split[2], 0, 23, CronEntity.HOUR);
        mDayOfMonth      = new CronEntity(split[3], 1, 31, CronEntity.DAY_OF_MONTH);
        mMonth           = new CronEntity(split[4], 1, 12, CronEntity.MONTH);
        mDayOfWeek       = new CronEntity(split[5], 1, 07, CronEntity.DAY_OF_WEEK);
    }

    public CronEntity getDayOfMonth() {
        return mDayOfMonth;
    }

    public CronEntity getDayOfWeek() {
        return mDayOfWeek;
    }

    public CronEntity getHour() {
        return mHour;
    }

    public CronEntity getMinute() {
        return mMinute;
    }

    public CronEntity getMonth() {
        return mMonth;
    }

    public CronEntity getSecond() {
        return mSecond;
    }
}
