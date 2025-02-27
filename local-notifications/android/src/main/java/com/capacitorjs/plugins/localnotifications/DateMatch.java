package com.capacitorjs.plugins.localnotifications;

import java.util.Calendar;
import java.util.Date;

/**
 * Class that holds logic for on triggers
 * (Specific time)
 */
public class DateMatch {

    private static final String separator = " ";

    private Integer year;
    private Integer month;
    private Integer day;
    private Integer weekday;
    private Integer hour;
    private Integer minute;
    private Integer second;

    // Unit used to save the last used unit for a trigger.
    // One of the Calendar constants values
    private Integer unit = -1;

    public DateMatch() {}

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getWeekday() {
      return weekday;
    }

    public void setWeekday(Integer weekday) {
      this.weekday = weekday;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    /**
     * Gets a calendar instance pointing to the specified date.
     *
     * @param date The date to point.
     */
    private Calendar buildCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Calculates next trigger date for
     *
     * @param date base date used to calculate trigger
     * @return next trigger timestamp
     */
    public long nextTrigger(Date date) {
        Calendar current = buildCalendar(date);
        Calendar next = buildNextTriggerTime(date);
        return postponeTriggerIfNeeded(current, next);
    }

    /**
     * Postpone trigger if first schedule matches the past
     */
    private long postponeTriggerIfNeeded(Calendar current, Calendar next) {
        if (next.getTimeInMillis() <= current.getTimeInMillis() && unit != -1) {
            Integer incrementUnit = -1;
            if (unit == Calendar.YEAR || unit == Calendar.MONTH) {
                incrementUnit = Calendar.YEAR;
            } else if (unit == Calendar.DAY_OF_MONTH) {
                incrementUnit = Calendar.MONTH;
            } else if (unit == Calendar.DAY_OF_WEEK) {
                incrementUnit = Calendar.WEEK_OF_MONTH;
            } else if (unit == Calendar.HOUR_OF_DAY) {
                incrementUnit = Calendar.DAY_OF_MONTH;
            } else if (unit == Calendar.MINUTE) {
                incrementUnit = Calendar.HOUR_OF_DAY;
            } else if (unit == Calendar.SECOND) {
                incrementUnit = Calendar.MINUTE;
            }

            if (incrementUnit != -1) {
                next.set(incrementUnit, next.get(incrementUnit) + 1);
            }
        }
        return next.getTimeInMillis();
    }

    private Calendar buildNextTriggerTime(Date date) {
        Calendar next = buildCalendar(date);
        if (year != null) {
            next.set(Calendar.YEAR, year);
            if (unit == -1) unit = Calendar.YEAR;
        }
        if (month != null) {
            next.set(Calendar.MONTH, month);
            if (unit == -1) unit = Calendar.MONTH;
        }
        if (day != null) {
            next.set(Calendar.DAY_OF_MONTH, day);
            if (unit == -1) unit = Calendar.DAY_OF_MONTH;
        }
        if (weekday != null) {
            next.set(Calendar.DAY_OF_WEEK, weekday);
            if (unit == -1) unit = Calendar.DAY_OF_WEEK;
        }
        if (hour != null) {
            next.set(Calendar.HOUR_OF_DAY, hour);
            if (unit == -1) unit = Calendar.HOUR_OF_DAY;
        }
        if (minute != null) {
            next.set(Calendar.MINUTE, minute);
            if (unit == -1) unit = Calendar.MINUTE;
        }
        if (second != null) {
            next.set(Calendar.SECOND, second);
            if (unit == -1) unit = Calendar.SECOND;
        }
        return next;
    }

    @Override
    public String toString() {
        return (
            "DateMatch{" +
            "year=" +
            year +
            ", month=" +
            month +
            ", day=" +
            day +
            ", weekday=" +
            weekday +
            ", hour=" +
            hour +
            ", minute=" +
            minute +
            ", second=" +
            second +
            '}'
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateMatch dateMatch = (DateMatch) o;

        if (year != null ? !year.equals(dateMatch.year) : dateMatch.year != null) return false;
        if (month != null ? !month.equals(dateMatch.month) : dateMatch.month != null) return false;
        if (day != null ? !day.equals(dateMatch.day) : dateMatch.day != null) return false;
        if (weekday != null ? !weekday.equals(dateMatch.weekday) : dateMatch.weekday != null) return false;
        if (hour != null ? !hour.equals(dateMatch.hour) : dateMatch.hour != null) return false;
        if (minute != null ? !minute.equals(dateMatch.minute) : dateMatch.minute != null) return false;
        return second != null ? second.equals(dateMatch.second) : dateMatch.second == null;
    }

    @Override
    public int hashCode() {
        int result = year != null ? year.hashCode() : 0;
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (weekday != null ? weekday.hashCode() : 0);
        result = 31 * result + (hour != null ? hour.hashCode() : 0);
        result = 31 * result + (minute != null ? minute.hashCode() : 0);
        result = 31 + result + (second != null ? second.hashCode() : 0);
        return result;
    }

    /**
     * Transform DateMatch object to CronString
     *
     * @return
     */
    public String toMatchString() {
        String matchString =
            year + separator 
            + month + separator 
            + day + separator 
            + weekday + separator 
            + hour + separator 
            + minute + separator 
            + second + separator 
            + unit;
        return matchString.replace("null", "*");
    }

    /**
     * Create DateMatch object from stored string
     *
     * @param matchString
     * @return
     */
    public static DateMatch fromMatchString(String matchString) {
        DateMatch date = new DateMatch();
        String[] split = matchString.split(separator);

        if (split != null && split.length == 7) {
            date.setYear(getValueFromCronElement(split[0]));
            date.setMonth(getValueFromCronElement(split[1]));
            date.setDay(getValueFromCronElement(split[2]));
            date.setWeekday(getValueFromCronElement(split[3]));
            date.setHour(getValueFromCronElement(split[4]));
            date.setMinute(getValueFromCronElement(split[5]));
            date.setUnit(getValueFromCronElement(split[6]));
        }

        if (split != null && split.length == 8) {
            date.setYear(getValueFromCronElement(split[0]));
            date.setMonth(getValueFromCronElement(split[1]));
            date.setDay(getValueFromCronElement(split[2]));
            date.setWeekday(getValueFromCronElement(split[3]));
            date.setHour(getValueFromCronElement(split[4]));
            date.setMinute(getValueFromCronElement(split[5]));
            date.setSecond(getValueFromCronElement(split[6]));
            date.setUnit(getValueFromCronElement(split[7]));
        }

        return date;
    }

    public static Integer getValueFromCronElement(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }
}
