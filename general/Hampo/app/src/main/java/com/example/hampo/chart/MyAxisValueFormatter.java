package com.example.hampo.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class MyAxisValueFormatter extends ValueFormatter{

    private ArrayList<String> days;

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return days.get((int)value);
    }

    public MyAxisValueFormatter(ArrayList<String> days) {
        super();
        this.days = days;
    }
}
