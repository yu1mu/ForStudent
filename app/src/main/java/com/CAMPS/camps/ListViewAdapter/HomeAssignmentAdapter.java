package com.CAMPS.camps.ListViewAdapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.CAMPS.camps.DataClass.Assignment;
import com.CAMPS.camps.MainActivity;
import com.CAMPS.camps.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeAssignmentAdapter extends BaseAdapter {
    ArrayList<Assignment> assignments = new ArrayList<>();

    public HomeAssignmentAdapter(ArrayList<Assignment> ass) {
        assignments = ass;
    }

    @Override
    public int getCount() {
        return this.assignments.size();
    }

    @Override
    public Object getItem(int position) {
        return assignments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_assignment_list_view,parent, false);
        TextView mDate = (TextView)convertView.findViewById(R.id.period2);
        TextView mAssignment = (TextView)convertView.findViewById(R.id.assign2);

        Assignment assignment = assignments.get(position);

        mDate.setText(String.format("%d.%2d",assignment.getPeriod().get(Calendar.MONTH)+1,assignment.getPeriod().get(Calendar.DAY_OF_MONTH)));
        mAssignment.setText(assignment.getName());

        Calendar today = Calendar.getInstance();
        if(assignment.getPeriod().get(Calendar.MONTH)==today.get(Calendar.MONTH) && assignment.getPeriod().get(Calendar.DAY_OF_MONTH)==today.get(Calendar.DAY_OF_MONTH)){
            mDate.setTextColor(Color.parseColor("#ec525b"));
            mAssignment.setTextColor(Color.parseColor("#ec525b"));
        }

        MainActivity main = MainActivity.getInstance();






        return convertView;
    }

}
