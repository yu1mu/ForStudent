package com.CAMPS.camps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.CAMPS.camps.BoxHelperClass.ScheduleHelper;
import com.CAMPS.camps.DataClass.Assignment;
import com.CAMPS.camps.DataClass.Event;
import com.CAMPS.camps.DataClass.Schedule;
import com.CAMPS.camps.DataClass.TestSub;
import com.CAMPS.camps.DataClass.Timetable;
import com.CAMPS.camps.ListViewAdapter.CalendarListAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class CalendarFragment extends Fragment{

    MainActivity main;

    /*** ArrayList for Events ***/
    ArrayList<Assignment> assignmentList;
    ArrayList<Schedule> schedules;
    ArrayList<TestSub> testList;

    ArrayList<Event> events = new ArrayList<>();

    //When pick a specific day, The events of that day will add to dayEvent.
    //Especially Schedule managed by scheduleDayEvent.
    ArrayList<Event> dayEvent;
    ArrayList<Event> scheduleDayEvent;


    /*** View Attributes ***/
    ListView upperListView;
    ListView lowerListView;
    TextView mUheader;
    TextView mLheader;

    MaterialCalendarView calendarView;
    // upper for assignment & exam
    CalendarListAdapter upperAdapter;
    // lower for schedule
    CalendarListAdapter lowerAdapter;
    View view;

    //Calendar dot color
    int colorAccent;

    int upperSize=0;
    int lowerSize=0;

    loadData load;
    saveData save;

    public CalendarFragment getInstance(){
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        main = (MainActivity)getActivity();
        colorAccent = getResources().getColor(R.color.colorAccent);
        load = new loadData();
        save = new saveData();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View)inflater.inflate(R.layout.fragment_calendar,container,false);
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        upperListView = view.findViewById(R.id.upperCalendarListView);
        lowerListView = view.findViewById(R.id.lowerCalendarListView);


        mUheader = view.findViewById(R.id.calendar_as_ex);
        mLheader  = view.findViewById(R.id.calendar_sche);


        /*** toolbar setting ***/
        main.setActionBarTitle(" 캘린더");
        main.centerToolbarTitle.setText("");
        main.invalidateOptionsMenu();


        load.run();

        //뷰설정
        ListViewSetter setter = new ListViewSetter();

        if(!main.getTimetableBox().isEmpty()){

            for(Object tmp:main.timeTables) {
                Timetable temp = (Timetable) tmp;
                System.out.println(temp.toString());
            }
        }


        // pick a day
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dayEvent = new ArrayList<>();
                                scheduleDayEvent = new ArrayList<>();
                                for(Event tmp:events){

                                    switch(tmp.getType()){
                                        case 1:
                                            Assignment assignment = (Assignment)tmp;

                                            if(assignment.getPeriod().get(Calendar.YEAR)!=date.getYear()) break;
                                            if(assignment.getPeriod().get(Calendar.MONTH)!=date.getMonth()-1) break;
                                            if(assignment.getPeriod().get(Calendar.DAY_OF_MONTH)!=date.getDay())break;
                                            dayEvent.add(tmp);

                                            break;
                                        case 2:
                                            Schedule schedule = (Schedule)tmp;
                                            if(schedule.getDate().get(Calendar.YEAR)!=date.getYear()) break;
                                            if(schedule.getDate().get(Calendar.MONTH)!=date.getMonth()-1) break;
                                            if(schedule.getDate().get(Calendar.DAY_OF_MONTH)!=date.getDay())break;
                                            scheduleDayEvent.add(tmp);

                                            break;
                                        case 3:
                                            TestSub testSub = (TestSub)tmp;
                                            if(testSub.getTestDate().get(Calendar.YEAR)!=date.getYear())break;
                                            if(testSub.getTestDate().get(Calendar.MONTH)!=date.getMonth()-1) break;
                                            if(testSub.getTestDate().get(Calendar.DAY_OF_MONTH)!=date.getDay())break;
                                            dayEvent.add(tmp);

                                            break;
                                    }
                                }
                                upperSize = dayEvent.size();
                                lowerSize = scheduleDayEvent.size();
                                upperAdapter = new CalendarListAdapter(dayEvent);
                                lowerAdapter = new CalendarListAdapter(scheduleDayEvent);
                                //listView = view.findViewById(R.id.upperCalendarListView);
                                upperListView.setAdapter(upperAdapter);
                                lowerListView.setAdapter(lowerAdapter);
                                setter.setListViewHeight(upperListView);
                                setter.setListViewHeight(lowerListView);
                                setHeader();
                            }
                        });
                    }
                }).start();
            }
        });

        setHeader();
        mLheader.setVisibility(View.GONE);
        mUheader.setVisibility(View.GONE);


        //pick a list
        lowerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final int pos = position;
                                try{
                                    String name = lowerAdapter.eventList.get(position).getTitle();
                                    String[] menu = {"수정", "삭제"};


                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                    dialog.setTitle(name);
                                    dialog.setItems(menu, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    dialog.dismiss();
                                                    modifySchedule(lowerAdapter.eventList.get(pos),lowerAdapter);
                                                    break;
                                                case 1:
                                                    AlertDialog.Builder remove = new AlertDialog.Builder(getContext());
                                                    remove.setTitle("삭제");
                                                    remove.setMessage("일정을 삭제 합니다.");
                                                    remove.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            removeSchedule(lowerAdapter.eventList.get(pos),lowerAdapter);
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    remove.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();

                                                        }
                                                    });
                                                    remove.show();
                                                    break;

                                            }
                                        }
                                    });
                                    dialog.create();
                                    dialog.show();
                                }
                                catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();


            }
        });

        calendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        main.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AddNewSchedule addFragment = AddNewSchedule.newInstance();
                                MainActivity main = (MainActivity)getActivity();
                                addFragment.year = date.getYear();
                                addFragment.month = date.getMonth();
                                addFragment.day = date.getDay();
                                main.FragmentAdd(addFragment);
                            }
                        });
                    }
                }).start();

            }
        });

        /*** draw a dot in calendar. In the future these three method can be merged ***/
        dotAssignment();
        dotSchedule();
        dotTest();

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        MainActivity main = (MainActivity)getActivity();
        save.run();
    }



    public void dotTest(){
        Collection<CalendarDay> testDaysList = new ArrayList<>();
        CalendarDay calendarDay;

        for(TestSub tmp : testList){

            Calendar calendar = tmp.getTestDate();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendarDay = CalendarDay.from(year,month,day);
            testDaysList.add(calendarDay);
        }
        calendarView.addDecorators(new EventDecorator(colorAccent,testDaysList));
    }
    public void dotAssignment(){
        Collection<CalendarDay> assignmentDaysList= new ArrayList<>();
        CalendarDay calendarDay;

        for(Assignment tmp : assignmentList){

            Calendar calendar = tmp.getPeriod();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendarDay = CalendarDay.from(year,month,day);
            assignmentDaysList.add(calendarDay);
        }
        calendarView.addDecorators(new EventDecorator(colorAccent,assignmentDaysList));
    }
    public void dotSchedule(){
        Collection<CalendarDay> scheduleDaysList = new ArrayList<>();

        CalendarDay calendarDay;

        for(Schedule tmp : schedules){

            Calendar calendar = tmp.getDate();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendarDay = CalendarDay.from(year,month,day);
            scheduleDaysList.add(calendarDay);
        }

        calendarView.addDecorators(new EventDecorator(colorAccent,scheduleDaysList));
    }

    public void addTransparent(){
        TransparentFragment addFragment = TransparentFragment.newInstance();
        MainActivity main = (MainActivity)getActivity();
        main.FragmentAdd(addFragment);
    }

    public void removeSchedule(Event event,CalendarListAdapter adapter){
        MainActivity main = (MainActivity)getActivity();
        Schedule schedule = (Schedule)event;
        main.alarmDelete(schedule);
        schedules.remove((Schedule)event);
        scheduleDayEvent.remove(event);
        events.remove(event);
        addTransparent();
        adapter.notifyDataSetChanged();
    }
    public void modifySchedule(Event event,CalendarListAdapter adapter){
        MainActivity main = (MainActivity)getActivity();
        AddNewSchedule addFragment = AddNewSchedule.newInstance();
        Schedule schedule = (Schedule)event;
        addFragment.year = schedule.getDate().get(Calendar.YEAR);
        addFragment.month = schedule.getDate().get(Calendar.MONTH)+1;
        addFragment.day = schedule.getDate().get(Calendar.DAY_OF_MONTH);
        addFragment.removeTarget = event;
        addFragment.MOD=true;
        main.FragmentAdd(addFragment);
        adapter.notifyDataSetChanged();
        addTransparent();

    }
    public MaterialCalendarView getCalendarView() {
        return calendarView;
    }

    private void setHeader(){
        TextView mNoschedule = view.findViewById(R.id.calendar_null);
        if(upperSize>0){
            mUheader.setVisibility(View.VISIBLE);
        }
        else{
            mUheader.setVisibility(View.GONE);
        }
        if(lowerSize>0){
            mLheader.setVisibility(View.VISIBLE);
        }
        else{
            mLheader.setVisibility(View.GONE);
        }
        if(!(upperSize>0) && !(lowerSize>0)){
            mNoschedule.setVisibility(View.VISIBLE);
        }
        else{
            mNoschedule.setVisibility(View.GONE);
        }
    }

    private class saveData extends Thread{
        public saveData(){

        }

        public void run(){
            try{
                main.getScheduleBox().removeAll();
                Collections.sort(schedules);
                for(int i=0; i<schedules.size(); i++){
                    ScheduleHelper helper = new ScheduleHelper((long)i+1, schedules.get(i).getTitle(), schedules.get(i).getDate(), schedules.get(i).getMemo(),false);
                    ScheduleHelper.putSchedule(helper);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    private class loadData extends Thread{
        public loadData(){

        }

        public void run(){
            try{
                schedules = main.schedules;
                assignmentList = main.assignment;
                testList = main.testSub;
                for(Schedule tmp:schedules){
                    //Event event = new Event(tmp.getTitle(),tmp.getDate().get(Calendar.HOUR),tmp.getDate().get(Calendar.MINUTE),tmp.getMemo(),2);
                    tmp.setHour(tmp.getDate().get(Calendar.HOUR_OF_DAY));
                    tmp.setMinute(tmp.getDate().get(Calendar.MINUTE));
                    tmp.setType(2);
                    if(!events.contains(tmp)) {
                        events.add(tmp);
                    }

                }
                for(Assignment tmp:assignmentList){
                    //Event event = new Event(tmp.getName(),tmp.getPeriod().get(Calendar.HOUR),tmp.getPeriod().get(Calendar.MINUTE),tmp.getMemo(),1);
                    tmp.setTitle(tmp.getName());
                    tmp.setHour(tmp.getPeriod().get(Calendar.HOUR_OF_DAY));
                    tmp.setMinute(tmp.getPeriod().get(Calendar.MINUTE));
                    tmp.setType(1);
                    if(!events.contains(tmp)) {
                        events.add(tmp);
                    }
                }
                for(TestSub tmp:testList){
                    //Event event = new Event(tmp.getName(),tmp.getStartHour(),tmp.getStartMinute(),tmp.getRange(),3);
                    tmp.setTitle(tmp.getName());
                    tmp.setHour(tmp.getStartHour());
                    tmp.setMinute(tmp.getStartMinute());
                    tmp.setMemo(tmp.getRange());
                    tmp.setType(3);
                    if(!events.contains(tmp)){
                        events.add(tmp);
                    }
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}