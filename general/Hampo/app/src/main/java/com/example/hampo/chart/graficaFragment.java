package com.example.hampo.chart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hampo.R;
import com.example.hampo.main.SectionsPagerAdapterMiHampo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class graficaFragment extends Fragment {

    private LineChart miGrafica;
    private LineData datosGrafica;
    private View vistaGrafica;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utils.init(getContext());
        View vista = getLayoutInflater().inflate(R.layout.graficas, container, false);
        // Creo la grafica
        miGrafica = (LineChart) vista.findViewById(R.id.lineChart);
        datosGrafica = generateDataLine(SectionsPagerAdapterMiHampo.datosTemperatura, SectionsPagerAdapterMiHampo.datosHumedad, SectionsPagerAdapterMiHampo.datosLuminosidad);
        miGrafica.setDragEnabled(true);
        miGrafica.setScaleEnabled(false);
        miGrafica.setData(datosGrafica);
        // Pongo valores a los axis de la grafica
        miGrafica.getXAxis().setValueFormatter(new MyAxisValueFormatter(SectionsPagerAdapterMiHampo.fechasTemperatura));
        miGrafica.getXAxis().setGranularity(1);
        miGrafica.getXAxis().setTextSize(12f);
        miGrafica.getLegend().setTextSize(17.5f);
        miGrafica.getLegend().setFormSize(20f);
        miGrafica.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        // Margin bottom de la grafica
        miGrafica.setExtraOffsets(10, 10, 10, 13);
        return vista;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private LineData generateDataLine(ArrayList<Float> datosTemperatura, ArrayList<Float> datosHumedad, ArrayList<Float> datosLuminosidad) {

        // Temperatura
        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < datosTemperatura.size(); i++) {
            values1.add(new Entry(i, datosTemperatura.get(i)));
        }

        LineDataSet d1 = new LineDataSet(values1, "Temperatura");
        d1.setLineWidth(3.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        int colorVerde = ContextCompat.getColor(getContext(), R.color.colorVerde);
        d1.setColor(colorVerde);
        d1.setCircleColor(colorVerde);
        d1.setDrawValues(true);
        d1.setValueTextSize(15f);

        // Humedad
        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < datosHumedad.size(); i++) {
            values2.add(new Entry(i, datosHumedad.get(i)));
        }

        LineDataSet d2 = new LineDataSet(values2, "Humedad");
        d2.setLineWidth(3.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        int colorAzul = ContextCompat.getColor(getContext(), R.color.colorAzul);
        d2.setColor(colorAzul);
        d2.setCircleColor(colorAzul);
        d2.setDrawValues(true);
        d2.setValueTextSize(15f);

        // Luminosidad
        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < datosLuminosidad.size(); i++) {
            values3.add(new Entry(i, datosLuminosidad.get(i)));
        }

        LineDataSet d3 = new LineDataSet(values3, "Luminosidad");
        d3.setLineWidth(3.5f);
        d3.setCircleRadius(4.5f);
        d3.setHighLightColor(Color.rgb(255, 55, 55));
        int colorAmarillo = ContextCompat.getColor(getContext(), R.color.colorAmarillo);
        d3.setColor(colorAmarillo);
        d3.setCircleColor(colorAmarillo);
        d3.setDrawValues(true);
        d3.setValueTextSize(15f);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);
        sets.add(d3);

        return new LineData(sets);
    }

    private LineData generateDataLine2(int cnt) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "Temperatura");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        d1.setValueTextSize(20f);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values2.add(new Entry(i, values1.get(i).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(values2, "Humedad");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(true);
        d2.setValueTextSize(20f);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }
}
