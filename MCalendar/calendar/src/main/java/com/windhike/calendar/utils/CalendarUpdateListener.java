package com.windhike.calendar.utils;

import android.support.v4.view.ViewPager;

/**
 * author: zyongjun on 2016/11/7 0007.
 * email: zhyongjun@windhike.cn
 */
public interface CalendarUpdateListener{
    void onDateSelected();
    void onPageNextSelected();
    void onPagePreviousSelected();
    void updateSelectRow(int row);
    void refreshCalendar(ViewPager viewPager);
}