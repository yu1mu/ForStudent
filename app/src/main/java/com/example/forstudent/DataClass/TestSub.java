package com.example.forstudent.DataClass;

import java.util.Calendar;

public class TestSub implements Comparable<TestSub>{


    //시험 정보 저장


    private String Name=null;

    private Calendar TestDate=Calendar.getInstance();

    private int startHour=-1;
    private int startMinute;

    private int endHour=-1;
    private int endMinute;

    private String Range=null;

    public int sorting;

    public TestSub(String name, Calendar calendar, int starthour, int startminute, int endhour, int  endminute, String range){
        this.Name=name;
        TestDate.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        this.startHour = starthour;
        this.startMinute = startminute;
        this.endHour = endhour;
        this.endMinute = endminute;
        this.sorting = calendar.get(Calendar.YEAR)*10000+calendar.get(Calendar.MONTH)*100+calendar.get(Calendar.DAY_OF_MONTH);
    }


    public String getName(){
        return Name;
    }

    public Calendar getTestDate(){
        return TestDate;
    }

    public int getStartHour(){
        return startHour;
    }

    public int getStartMinute(){
        return startMinute;
    }

    public int getEndHour(){
        return endHour;
    }

    public int getEndMinute(){
        return endMinute;
    }

    public String getRange(){
        if(Range!=null){
            return Range;
        }
        else{
            return "";
        }
    }


    @Override
    public int compareTo(TestSub t) {
        if(this.sorting<t.sorting){
            return -1;
        }
        else if(this.sorting==t.sorting){
            if(this.startHour<t.startHour){
                return -1;
            }
            else{
                return 1;
            }
        }
        return 1;
    }
}