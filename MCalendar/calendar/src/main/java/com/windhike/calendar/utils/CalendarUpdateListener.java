package com.windhike.calendar.utils;

import android.support.v4.view.ViewPager;

import java.util.Calendar;

/**
 * author: zyongjun on 2016/11/7 0007.
 * email: zhyongjun@windhike.cn
 */
public interface CalendarUpdateListener{
    void onDateSelected(Calendar calendar);
    void onPageNextSelected();
    void onPagePreviousSelected();
    void updateSelectRow(int row);
    void refreshCalendar(ViewPager viewPager);
}