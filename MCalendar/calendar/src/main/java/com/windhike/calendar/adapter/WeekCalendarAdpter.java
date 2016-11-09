package com.windhike.calendar.adapter;

import android.content.Context;
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
 * Created by Administrator on 2016/1/16 0016.
 */
public class WeekCalendarAdpter extends CalendarBaseAdpter {
    private List<View> views;
    private Context context;

    private CalendarUpdateListener os = null;

    private String strToday = "";

    public WeekCalendarAdpter(List<View> views, Context context) {
        this.views = views;
        this.context = context;
        //选中今天
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        strToday = DateUtils.getTagTimeStr(today);
        selectTime = DateUtils.getTagTimeStr(today);
    }

    public void setUpdateListener(CalendarUpdateListener os) {
        this.os = os;
    }


    @Override
    public int getCount() {
        return 9600;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(views.get(position % views.size()));
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

        //给view 填充内容

        //设置开始时间为本周日
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        today.add(Calendar.DATE, -day_of_week);
        //距离当前时间的周数
        int week = getCount() / 2 - position;
        today.add(Calendar.DATE, -week * 7);

        render(view, today);
        return view;
    }

    /**
     * 渲染page中的view：7天
     */
    private void render(final ViewGroup view, final Calendar today) {
        for (int a = 0; a < 7; a++) {
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

            dayOfWeek.setTag(DateUtils.getTagTimeStr(today));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(today.getTimeInMillis());
            dayOfWeek.setTag(R.id.tag_calendar,calendar);
            dayOfWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is = true;
                    //TODO:发消息，告诉Activity我改变选中的日期了
                    if (WeekCalendarAdpter.this.os != null) {
                        os.onDateSelected((Calendar) v.getTag(R.id.tag_calendar));
                    }
                    selectTime = dayOfWeek.getTag().toString();
                    today.add(Calendar.DATE, -7);//因为已经渲染过7天，所以today往前推7天， 代表当前page重绘；

                    //界面特效：变为红色，执行动画
                    dayOfWeek.findViewById(R.id.cal_container).setActivated(true);
                    dayOfWeek.findViewById(R.id.cal_container).setSelected(true);
                    //显示的调用invalidate
                    dayOfWeek.invalidate();
                    //添加监听：动画开始时，恢复上个选中的day的状态，结束时执行刷新方法;

                    //将上一个选中的day的状态恢复
                    if (day != null) {
                        //特殊情况:上个选中的day今天
                        if (strToday.equals(tag)) {
                            day.findViewById(R.id.cal_container).setActivated(true);
                            day.findViewById(R.id.cal_container).setSelected(false);
                        } else {
                            day.findViewById(R.id.cal_container).setActivated(false);
                        }
                    }

                    render(view, today);
                    is = false;

                }
            });
            View dayContainer = dayOfWeek.findViewById(R.id.cal_container);
            if (strToday.equals(DateUtils.getTagTimeStr(today))) {
                dayContainer.setActivated(true);
                dayContainer.setSelected(true);
                if (!selectTime.equals(strToday)) {
                    dayContainer.setSelected(false);
                    today.add(Calendar.DATE, 1);
                    continue;
                }
            } else {
                dayContainer.setActivated(false);
            }
            if (selectTime.equals(DateUtils.getTagTimeStr(today))) {
                dayContainer.setActivated(true);
                dayContainer.setSelected(true);

                day = dayOfWeek;
                tag = selectTime;
            } else {
                dayContainer.setActivated(false);
            }
            View eventFlagView = dayOfWeek.findViewById(R.id.imv_point);
            if (calendarEventShowTimeList.contains(DateUtils.getTagTimeStr(today))) {
                eventFlagView.setVisibility(View.VISIBLE);
//                ((ImageView) dayOfWeek.findViewById(R.id.imv_point)).setImageResource(R.drawable.calendar_item_point);
            } else {
                eventFlagView.setVisibility(View.INVISIBLE);
            }

            today.add(Calendar.DATE, 1);
        }
    }

    private ViewGroup day = null;
    private String tag = "";

    public String getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(String selectTime) {
        this.selectTime = selectTime;
    }

    //得到周视图选中日期后的CurrentItem
    public int getWeekCurrentItem() {
        //此刻
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        //转为本周一
        int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        today.add(Calendar.DATE, -day_of_week);
        //选中时间
        String time = this.getSelectTime();
        Date date = DateUtils.stringToDate(time);
        Calendar sele = new GregorianCalendar();
        sele.setTimeInMillis(date.getTime());

        //选中时间的(day of yeay)-此刻(day of yeay)=天数
        int aa = ((int) (sele.getTime().getTime() / 1000) - (int) (today.getTime().getTime() / 1000)) / 3600 / 24;
        int aa2 = 0;
        if (Math.abs(aa) % 7 == 0) {
            aa2 = Math.abs(aa) / 7;
        } else {
            aa2 = Math.abs(aa) / 7;
        }
        if (aa >= 0) {
            return this.getCount() / 2 + aa2;
        } else {
            return this.getCount() / 2 - aa2 - 1;
        }
    }

}
