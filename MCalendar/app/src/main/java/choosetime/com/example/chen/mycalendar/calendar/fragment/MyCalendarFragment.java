package choosetime.com.example.chen.mycalendar.calendar.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.windhike.calendar.adapter.CalendarBaseAdpter;
import com.windhike.calendar.adapter.MonthCalendarAdpter;
import com.windhike.calendar.adapter.WeekCalendarAdpter;
import com.windhike.calendar.utils.DateUtils;
import com.windhike.calendar.utils.RefreshListener;
import com.windhike.calendar.widget.HandMoveLayout;
import com.windhike.calendar.widget.HasTwoAdapterViewpager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import choosetime.com.example.chen.mycalendar.R;


public class MyCalendarFragment extends Fragment implements RefreshListener {
    private Button btnSwitch;

    public MyCalendarFragment() {
        super();
    }

    private HandMoveLayout handMoveLayout;

    @SuppressLint("ValidFragment")
    public MyCalendarFragment(Handler os) {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_calender, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handMoveLayout = (HandMoveLayout) getView().findViewById(R.id.handmovelayout);

        initCalendar();
        btnSwitch = (Button)getView().findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handMoveLayout.expand();
//                handMoveLayout.collapse();
                handMoveLayout.toogle();
            }
        });

    }
    private HasTwoAdapterViewpager viewPager;
    private HasTwoAdapterViewpager viewpagerWeek;
    private List<View> views;
    private WeekCalendarAdpter weekCalendarAdpter;
    private ArrayList<String> timeList = new ArrayList<>();
    public void initCalendar() {
        viewPager = (HasTwoAdapterViewpager) getView().findViewById(R.id.calendar_viewpager);
        viewpagerWeek = (HasTwoAdapterViewpager) getView().findViewById(R.id.calendar_viewpager_week);

        viewPager.setListener(this);
        viewpagerWeek.setListener(this);

        //制造月视图所需view
        views = new ArrayList<>();
        LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout1 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout2 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        LinearLayout layout3 = (LinearLayout) View.inflate(getActivity(), R.layout.mouth, null);
        views.add(layout);
        views.add(layout1);
        views.add(layout2);
        views.add(layout3);

        adpter = new MonthCalendarAdpter(views, getActivity(), timeList);
        adpter.setUpdateListener(updateListener);

        //制造日试图所需view
        List viewss = new ArrayList();
        LinearLayout layoutri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout1ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout2ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        LinearLayout layout3ri = (LinearLayout) View.inflate(getActivity(), R.layout.week, null);
        viewss.add(layoutri);
        viewss.add(layout1ri);
        viewss.add(layout2ri);
        viewss.add(layout3ri);
        weekCalendarAdpter = new WeekCalendarAdpter(viewss, getActivity(), timeList);
        weekCalendarAdpter.setUpdateListener(updateListener);
        viewPager.setAdapter(adpter);
        viewPager.setCurrentItem(1200, true);
        viewpagerWeek.setAdapter(weekCalendarAdpter);
        viewpagerWeek.setCurrentItem(weekCalendarAdpter.getCount() / 2);

        //如果是周日，就翻到下一页
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("position", Integer.toString(position));

            }

            @Override
            public void onPageSelected(int position) {
                Calendar today = new GregorianCalendar();
                today.setTimeInMillis(System.currentTimeMillis());
                //距离当前时间的月数(按月算)
                int month = adpter.getCount() / 2 - position;
                today.add(Calendar.MONTH, -month);

                //更新currentItem
//                    viewPager.setTag(R.id.month_current,position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpagerWeek.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar today = new GregorianCalendar();
                today.setTimeInMillis(System.currentTimeMillis());

                int day_of_week = today.get(Calendar.DAY_OF_WEEK) - 1;
                if (day_of_week == 0) {
                    day_of_week = 7;
                }
                today.add(Calendar.DATE, -day_of_week);
                //距离当前时间的周数(按周算)
                int week = weekCalendarAdpter.getCount() / 2 - position;
                today.add(Calendar.DATE, -week * 7);
//                setBarTitle(getTopTitleTime(today));
                //刷新本页
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private MonthCalendarAdpter adpter;

    private CalendarBaseAdpter.CalendarUpdateListener updateListener = new CalendarBaseAdpter.CalendarUpdateListener() {
        @Override
        public void onDateSelected() {

        }

        @Override
        public void onPageNextSelected() {
            if (viewPager!=null){
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }

        @Override
        public void onPagePreviousSelected() {
            if (viewPager!=null){
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
            }
        }

        @Override
        public void updateSelectRow(int row) {
            handMoveLayout.setRowNum(row);
        }
    };

    int currentItem = 0;
    @Override
    public void refreshListener(final ViewPager viewPager) {
//        ToastUtils.shortMsg("刷新");
        //得到这个selecttime对应的currentItem
        currentItem = 0;
        if (viewPager.getAdapter() instanceof MonthCalendarAdpter) {
            adpter.getTimeList(timeList);
            //月视图
            currentItem = adpter.getMonthCurrentItem();
            int odl = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem, false);
            //刷新已经存在的3个视图view
            if (Math.abs(odl - currentItem) <= 1) {
                adpter.instantiateItem(viewPager, viewPager.getCurrentItem() - 1);

                adpter.instantiateItem(viewPager, viewPager.getCurrentItem());

                adpter.instantiateItem(viewPager, viewPager.getCurrentItem() + 1);
            }
            adpter.notifyDataSetChanged();
        } else {
            //周视图
            currentItem = weekCalendarAdpter.getWeekCurrentItem();
            //如果是周日，就是下一周，+1
            if (DateUtils.getWeekStr(DateUtils.stringToDate(adpter.getSelectTime())).equals("星期日")) {
                currentItem++;
            }
            weekCalendarAdpter.getTimeList(timeList);
            int odl = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem, false);
            //刷新已经存在的3个视图view
            if (Math.abs(odl - currentItem) <= 1) {
                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem() - 1);

                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem());

                weekCalendarAdpter.instantiateItem(viewPager, viewPager.getCurrentItem() + 1);
            }
            weekCalendarAdpter.notifyDataSetChanged();
        }

    }

}
