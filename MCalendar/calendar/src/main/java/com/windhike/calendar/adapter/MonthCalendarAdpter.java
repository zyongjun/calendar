package com.windhike.calendar.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.windhike.calendar.R;
import com.windhike.calendar.utils.CalendarUpdateListener;
import com.windhike.calendar.utils.CalendarUtil;
import com.windhike.calendar.utils.DateUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Administrator on 2016/5/19 0019.
 */
public class MonthCalendarAdpter extends CalendarBaseAdpter {
    private List<View> views;
    private Context context;

    private CalendarUpdateListener os = null;
    private int last_msg_tv_color;

    private String strToDay = "";

    public MonthCalendarAdpter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
        //选中今天
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());

        strToDay = DateUtils.getTagTimeStr(today);

        selectTime = DateUtils.getTagTimeStr(today);
        Resources res = context.getResources();
        last_msg_tv_color = ResourcesCompat.getColor(res,R.color.last_msg_tv_color,null);
    }

    public void setUpdateListener(CalendarUpdateListener os) {
        this.os = os;
    }

    @Override
    public int getCount() {
        return 2400;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ViewGroup view = (ViewGroup) views.get(position % views.size());
        int index = container.indexOfChild(view);
        if (index != -1) {
            container.removeView(view);
        }
        try {
            container.addView(view);
        } catch (Exception e) {

        }
        refresh(view, position);
        return view;
    }

    /**
     * 提供对外的刷新接口
     */
    public void refresh(ViewGroup view, int position) {
        //给view 填充内容

        //设置开始时间为本周日
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        //距离当前时间的月数
        int month = 1200 - position;
        today.add(Calendar.MONTH, -month);
        view.setTag(today.get(Calendar.MONTH) + "");
        //找到这个月的第一天所在星期的周日
        today.add(Calendar.DAY_OF_MONTH, -(today.get(Calendar.DAY_OF_MONTH) - 1));


        int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        today.add(Calendar.DATE, -day_of_week);

        render(view, today);
    }

    private static final String TAG = "MonthCalendarAdpter";
    /**
     * 渲染page中的view：7天
     */
    private void render(final ViewGroup view1, final Calendar today) {
        //一页显示一个月+7天，为42；
        for (int b = 0; b < 11; b=b+2) {
            final ViewGroup view = (ViewGroup) view1.getChildAt(b);
            for (int a = 0; a < 13; a=a+2) {
                final int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
                // int day_of_year=today.get(Calendar.DAY_OF_YEAR);
                final ViewGroup dayOfWeek = (ViewGroup) view.getChildAt(a);
                //((TextView) dayOfWeek.getChildAt(0)).setText(getStr(today.get(Calendar.DAY_OF_WEEK)));
                ((TextView) dayOfWeek.findViewById(R.id.gongli)).setText(dayOfMonth + "");
                String str = "";
                try {
                    str = new CalendarUtil().getChineseDay(today.get(Calendar.YEAR),
                            today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
                } catch (Exception e) {

                }
                if (str.equals("初一")) {//如果是初一，显示月份
                    str = new CalendarUtil().getChineseMonth(today.get(Calendar.YEAR),
                            today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));
                }
                ((TextView) dayOfWeek.findViewById(R.id.nongli)).setText(str);
                View eventFlagView = dayOfWeek.findViewById(R.id.imv_point);
                if (calendarEventShowTimeList.contains(DateUtils.getTagTimeStr(today))) {
                    eventFlagView.setVisibility(View.VISIBLE);
//                    ((ImageView) dayOfWeek.findViewById(R.id.imv_point)).setImageResource(R.drawable.calendar_item_point);
                } else {
                    eventFlagView.setVisibility(View.INVISIBLE);
                }
                dayOfWeek.setTag(DateUtils.getTagTimeStr(today));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(today.getTimeInMillis());
                dayOfWeek.setTag(R.id.tag_calendar,calendar);
                dayOfWeek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        is = true;
                        //TODO:发消息，告诉Activity我改变选中的日期了
                        if (MonthCalendarAdpter.this.os != null) {
                            os.onDateSelected((Calendar) v.getTag(R.id.tag_calendar));
                        }

                        selectTime = dayOfWeek.getTag().toString();
                        Log.e(TAG, "onClick: ---------"+selectTime, null);
                        today.add(Calendar.DATE, -42);//因为已经渲染过42天，所以today往前推42天， 代表当前page重绘；

                        //
                        //恢复上个选中的day的状态
                        if (day != null) {
                            day.findViewById(R.id.ll_day).setActivated(false);
                            //特殊情况
                            if (strToDay.equals(tag)) {
                                day.findViewById(R.id.ll_day).setActivated(true);
                                day.findViewById(R.id.ll_day).setSelected(false);

                            } else {
                                day.findViewById(R.id.ll_day).setActivated(false);
                            }
                        }
                        //变为红色
                        dayOfWeek.findViewById(R.id.ll_day).setActivated(true);
                        dayOfWeek.findViewById(R.id.ll_day).setSelected(true);
                        //显示的调用invalidate
                        dayOfWeek.invalidate();
                        //  添加监听：动画结束时执行刷新方法;
                        render(view1, today);
                        is = false;
                    }
                });
                if (strToDay.equals(DateUtils.getTagTimeStr(today))) {
                    dayOfWeek.findViewById(R.id.ll_day).setActivated(true);
                    dayOfWeek.findViewById(R.id.ll_day).setSelected(true);
                    if (!selectTime.equals(strToDay)) {
                        today.add(Calendar.DATE, 1);
                        dayOfWeek.findViewById(R.id.ll_day).setSelected(false);
                        continue;
                    }
                } else {
                    dayOfWeek.findViewById(R.id.ll_day).setActivated(false);
                }
                //不是当前月的显示为灰色
                if (today.get(Calendar.MONTH) != (Integer.parseInt((String) view1.getTag()))) {
                    ((TextView) dayOfWeek.findViewById(R.id.gongli)).setTextColor(last_msg_tv_color);
                    ((TextView) dayOfWeek.findViewById(R.id.nongli)).setTextColor(last_msg_tv_color);
                    if ((Integer.parseInt((String) view1.getTag())) > today.get(Calendar.MONTH)) {
                        //下个月
                        dayOfWeek.setOnClickListener(lastLister);
                    } else {
                        //上个月
                        dayOfWeek.setOnClickListener(nextLister);
                    }
                    today.add(Calendar.DATE, 1);
                    continue;
                } else {
                    ((TextView) dayOfWeek.findViewById(R.id.gongli)).setActivated(false);
                    ((TextView) dayOfWeek.findViewById(R.id.gongli)).setEnabled(true);
                    dayOfWeek.setAlpha(1.0f);
                }
                //如果是选中天的话显示为红色
                if (selectTime.equals(DateUtils.getTagTimeStr(today))) {
                    dayOfWeek.findViewById(R.id.ll_day).setActivated(true);
                    dayOfWeek.findViewById(R.id.ll_day).setSelected(true);

//                    if (strToDay.equals(DateUtils.getTagTimeStr(today))) {
//                        dayOfWeek.findViewById(R.id.ll_day).setSelected(false);
//                    }

                    day = dayOfWeek;
                    if (MonthCalendarAdpter.this.os != null) {
                        os.updateSelectRow(b);
                    }
                    tag = selectTime;
                } else {
                    dayOfWeek.findViewById(R.id.ll_day).setActivated(false);
                }
                today.add(Calendar.DATE, 1);
            }
        }

    }

    View.OnClickListener nextLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MonthCalendarAdpter.this.os != null) {
                os.onPageNextSelected();
            }
        }
    };
    View.OnClickListener lastLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MonthCalendarAdpter.this.os != null) {
                os.onPagePreviousSelected();
            }
        }
    };

    private ViewGroup day = null;

    public ViewGroup getDay() {
        return day;
    }

    private String tag = "";

    public String getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(String selectTime) {
        this.selectTime = selectTime;
    }

    //得到月视图选中日期后的CurrentItem
    public int getMonthCurrentItem() {
        //此刻
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        //选中时间
        String time = getSelectTime();
        Date date = DateUtils.stringToDate(time);
        Calendar sele = new GregorianCalendar();
        sele.setTimeInMillis(date.getTime());

        //选中时间的(MONTH)-此刻(MONTH)=月数
        int aa = sele.get(Calendar.MONTH) - today.get(Calendar.MONTH);

        return getCount() / 2 + aa;
    }
}
