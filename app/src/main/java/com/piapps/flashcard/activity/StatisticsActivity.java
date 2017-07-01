package com.piapps.flashcard.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.piapps.flashcard.R;
import com.piapps.flashcard.db.StatsDb;
import com.piapps.flashcard.model.Stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.chartTrueAnswers)
    LineChart lineChart;

    @BindView(R.id.chartTimeSpent)
    LineChart lineChartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        String setName = getIntent().getStringExtra("SET");
        getSupportActionBar().setTitle(setName + " Statistics");

        String setId = getIntent().getStringExtra("SET_ID");
        StatsDb statsDb = new StatsDb(this);
        List<Stats> list = statsDb.getSetCards(setId);

        if (list != null)
            if (list.size() >= 1) {
                Log.d("STATS_LIST", "onCreateValhalla: list.size() = " + list.size());
                List<Entry> entryList = new ArrayList<>();
                List<Entry> entryListTime = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    entryList.add(new Entry(i, Float.parseFloat(list.get(i).getTrueAnswers())));
                    entryListTime.add(new Entry(i, (Float.parseFloat(list.get(i).getTimeSpent()) / 1000 / 60)));
                }

                final List<String> labels = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
                    Date date = new Date(Long.parseLong(list.get(i).getDate()));
                    labels.add(sdf.format(date).toString());
                }

                IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        //Log.d("IAXIS", "getFormattedValue: value = " + value + "\nlabels.size() = " + labels.size());
                        if (((int) value) == -1)
                            return "Past";
                        if (((int) value) >= labels.size())
                            return "Future";
                        return labels.get((int) (value));
                    }
                };

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(iAxisValueFormatter);

                LineDataSet lineDataSet = new LineDataSet(entryList, getString(R.string.efficency_chart));
                lineDataSet.setDrawFilled(true);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColors(getResources().getColor(R.color.md_green_500));
                lineDataSet.setFillColor(getResources().getColor(R.color.md_green_500));
                LineData data = new LineData(lineDataSet);
                Description description = new Description();
                description.setText(getString(R.string.true_answers));
                lineChart.setDescription(description);
                lineChart.setData(data);
                lineChart.animateY(2048);
                float scaleX = list.size() / 4;
                lineChart.zoom(scaleX, 0, 0, 0);
                lineChart.centerViewTo(list.size() - 1, 0, null);

                IAxisValueFormatter iAxisValueFormatterTime = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        //Log.d("IAXIS", "getFormattedValue: value = " + value + "\nlabels.size() = " + labels.size());
                        if (((int) value) == -1)
                            return "Past";
                        if (((int) value) >= labels.size())
                            return "Future";
                        return labels.get((int) (value));
                    }
                };

                XAxis xAxisTime = lineChartTime.getXAxis();
                xAxisTime.setGranularity(1f);
                xAxisTime.setValueFormatter(iAxisValueFormatterTime);

                LineDataSet lineDataSetTime = new LineDataSet(entryListTime, getString(R.string.time_chart));
                lineDataSetTime.setDrawFilled(true);
                lineDataSetTime.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSetTime.setColors(getResources().getColor(R.color.colorAccent));
                lineDataSetTime.setFillColor(getResources().getColor(R.color.colorAccent));
                LineData dataTime = new LineData(lineDataSetTime);
                Description descriptionTime = new Description();
                descriptionTime.setText(getString(R.string.minutes));
                lineChartTime.setDescription(descriptionTime);
                lineChartTime.setData(dataTime);
                lineChartTime.animateY(2048);
                float scaleXTime = list.size() / 4;
                lineChartTime.zoom(scaleXTime, 0, 0, 0);
                lineChartTime.centerViewTo(list.size() - 1, 0, null);
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
