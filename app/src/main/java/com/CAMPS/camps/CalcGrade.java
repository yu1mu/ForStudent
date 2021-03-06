package com.CAMPS.camps;

import com.CAMPS.camps.DataClass.Grade;

import java.util.ArrayList;

public class CalcGrade{

    ArrayList<Grade> data = new ArrayList<>();
    int size;
    int totalCredit = 0;
    double grade=0;

    public CalcGrade(ArrayList<Grade> grade){
        this.data = grade;
        size = grade.size();
    }

    public void Calculate(){
        for(Grade tmp : data){
            grade += (tmp.grade * tmp.credit);
        }
        grade = grade / totalCredit;
    }

    public double getGrade(){
        return grade;
    }

    public double getCredit(){
        return totalCredit;
    }

}
