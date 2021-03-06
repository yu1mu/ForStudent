package com.CAMPS.camps.BoxHelperClass;

import com.CAMPS.camps.BoxClass.Assignment_Model;
import com.CAMPS.camps.DataClass.Assignment;
import com.CAMPS.camps.MainActivity;

import java.util.Calendar;

public class  AssignmentHelper{

    public Assignment_Model assignmentModel;

    public AssignmentHelper(long id,String name,Calendar period, String memo, boolean flag){
        assignmentModel = new Assignment_Model(id,name,period.get(Calendar.YEAR),period.get(Calendar.MONTH)+1,period.get(Calendar.DATE),period.get(Calendar.HOUR),period.get(Calendar.MINUTE),memo,flag);
    }

    public static void setSubject(long id,String subject){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        assignmentModel.setSubject(subject);
        MainActivity.getInstance().getAssignmentBox().put(assignmentModel);
    }
    public static void setMemo(long id,String memo){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        assignmentModel.setMemo(memo);
        MainActivity.getInstance().getAssignmentBox().put(assignmentModel);
    }
    public static void setPeriod(long id,Calendar period){
        int year = period.get(Calendar.YEAR);
        int month = period.get(Calendar.MONTH);
        int day = period.get(Calendar.DATE);
        int hour = period.get(Calendar.HOUR_OF_DAY);
        int minute = period.get(Calendar.MINUTE);

        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        assignmentModel.setDeadLineYear(year);
        assignmentModel.setDeadLineMonth(month);
        assignmentModel.setDeadLineDay(day);
        assignmentModel.setDeadLineHour(hour);
        assignmentModel.setDeadLineMinute(minute);
        MainActivity.getInstance().getAssignmentBox().put(assignmentModel);
    }

    public static void setFlag(long id, boolean flag){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        assignmentModel.setFlag(flag);
        MainActivity.getInstance().getAssignmentBox().put(assignmentModel);
    }

    public static void putAssignment(AssignmentHelper assignment){
        MainActivity main = MainActivity.getInstance();
        MainActivity.getInstance().getAssignmentBox().put(assignment.assignmentModel);
    }

    public static String getSubject(long id){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        return assignmentModel.getSubject();

    }
    public static String getMemo(long id){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        return assignmentModel.getMemo();

    }
    public static Calendar getPeriod(long id){
        int year;
        int month;
        int day;
        int hour;
        int minute;
        Calendar period = Calendar.getInstance();
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        period.set(assignmentModel.getDeadLineYear(),assignmentModel.getDeadLineMonth()-1,assignmentModel.getDeadLineDay(),assignmentModel.getDeadLineHour(),assignmentModel.getDeadLineMinute());
        return period;
    }

    public static long getSorting(long id) {
        Calendar period = Calendar.getInstance();
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        period.set(assignmentModel.getDeadLineYear(),assignmentModel.getDeadLineMonth(),assignmentModel.getDeadLineDay(),assignmentModel.getDeadLineHour(),assignmentModel.getDeadLineMinute());
        return period.getTimeInMillis();
    }

    public static boolean getFlag(long id){
        Assignment_Model assignmentModel = (Assignment_Model) MainActivity.getInstance().getAssignmentBox().get(id);
        return assignmentModel.getFlag();
    }

    public long getId(){
        return assignmentModel.getId();
    }

    public static Assignment getAssignment(long id){
        Assignment assignment = new Assignment(getSubject(id),getPeriod(id),getMemo(id),getFlag(id));
        return assignment;
    }

    @Override
    public String toString() {
        return "AssignmentHelper{" +
                "assignmentModel=" + assignmentModel.toString() +
                '}';
    }
}
