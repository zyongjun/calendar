package com.windhike.calendar.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public abstract class CalendarBaseAdpter extends PagerAdapter {
    protected static final String HOLIDAY_TXT = "休";
    protected static final String WEEKDAY_TXT = "班";
    protected static final String DAY_CHINESE_MONTH_FIRST = "初一";
    protected static final String EMPTY_VALUE = "";
    /**
     * 选中时间：xx-xx-xx
     * */
    public static String selectTime = "";
    protected List<String> calendarEventShowTimeList = new ArrayList<>();
    protected List<String> calendarHolidayList = new ArrayList<>();
    protected  List<String> calendarWeekdayList = new ArrayList<>();

    public void setEventShowTimeList(List<String> showTimeList) {
        calendarEventShowTimeList.clear();
        if(showTimeList != null) {
            calendarEventShowTimeList.addAll(showTimeList);
        }
    }

    public void setCalendarHolidayList(List<String> holidayList) {
        calendarHolidayList.clear();
        if (holidayList != null) {
            calendarHolidayList.addAll(holidayList);
        }
    }

    public void setCalendarWeekdayList(List<String> weekdayList) {
        calendarWeekdayList.clear();
        if (weekdayList != null) {
            calendarWeekdayList.addAll(weekdayList);
        }
    }
    /**
     * 是否拦截事件
     * */
    public static boolean is=false;

    public static boolean is() {
        return is;
    }

    @Override
    public int getCount() {//无所谓
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {//无所谓
        return false;
    }

}
