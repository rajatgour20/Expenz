package com.example.s;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.math.Stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FutureActivity extends AppCompatActivity {

    TextView tv;
    Spinner yearspinner;
    String msgData = "";
    static String amts = "";
    String avail = "";
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    static String amtsfordisplay = "";
    static double jantotalb = 0.0;
    static double febtotalb = 0.0;
    static double martotalb = 0.0;
    static double aprtotalb = 0.0;
    static double maytotalb = 0.0;
    static double juntotalb = 0.0;
    static double jultotalb = 0.0;
    static double augtotalb = 0.0;
    static double septotalb = 0.0;
    static double octtotalb = 0.0;
    static double novtotalb = 0.0;
    static double dectotalb = 0.0;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);
        tv = findViewById(R.id.textView);
        yearspinner = findViewById(R.id.year);
        setspinner();

        lineChart = findViewById(R.id.lineChart);
        getEntries();
//        lineDataSet = new LineDataSet(lineEntries, "");
//        lineData = new LineData(lineDataSet);
//        lineChart.setData(lineData);
//        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        lineDataSet.setValueTextColor(Color.BLACK);
//        lineDataSet.setValueTextSize(18f);


        if (ContextCompat.checkSelfPermission(FutureActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FutureActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {
            msgData = getAllSms(FutureActivity.this, yearspinner.getSelectedItem().toString());
        }
        setEntries();

    }
    private void setEntries() {
        // calling method to get bar entries.
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "");
        lineData = new LineData(lineDataSet);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.setVisibleYRangeMinimum(1, YAxis.AxisDependency.LEFT);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setData(lineData);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(12);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColors(ColorTemplate.getHoloBlue());
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(0f);
        String[] values = new String[]{"Oct","Nov","Dec"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1f);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int x = lineChart.getLineData().getDataSetForEntry(e).getEntryIndex(e);
                String month = values[x];
                String amount1 = String.valueOf(lineEntries.get(x));
                int strlen = amount1.length();
                String amount = "Amount is:-" + amount1.substring(16, strlen) + "₹";
                AlertDialog.Builder builder = new AlertDialog.Builder(FutureActivity.this);
                builder.setCancelable(true);
                View nview = LayoutInflater.from(FutureActivity.this).inflate(R.layout.monthly_stats, null);
                TextView month_txt = nview.findViewById(R.id.month);
                TextView expense_txt = nview.findViewById(R.id.expense);
                month_txt.setText(month);
                expense_txt.setText(amount);
                builder.setView(nview);
                alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });



    }

    public  class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }
        @Override
        public  String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }


    private void setspinner() {
        ArrayList<String> yeararraylist = new ArrayList<>();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        yeararraylist.add(year + "");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yeararraylist);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearspinner.setAdapter(arrayAdapter);

        yearspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(FutureActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FutureActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                } else {
                    msgData = getAllSms(FutureActivity.this, yearspinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public String getAllSms(Context context, String year) {
        double jantotal = 0.0;
        double febtotal = 0.0;
        double martotal = 0.0;
        double aprtotal = 0.0;
        double maytotal = 0.0;
        double juntotal = 0.0;
        double jultotal = 0.0;
        double augtotal = 0.0;
        double septotal = 0.0;
        double octtotal = 0.0;
        double novtotal = 0.0;
        double dectotal = 0.0;

        jantotalb = 0.0;
        febtotalb = 0.0;
        martotalb = 0.0;
        aprtotalb = 0.0;
        maytotalb = 0.0;
        juntotalb = 0.0;
        jultotalb = 0.0;
        augtotalb = 0.0;
        septotalb = 0.0;
        octtotalb = 0.0;
        novtotalb = 0.0;
        dectotalb = 0.0;
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        String s = "";
        double totaldeb = 0;
        Pattern regEx = Pattern.compile("(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*|.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)");


        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    String bodylowercase = body.toLowerCase();
                    Date dateFormat = new Date(Long.valueOf(smsDate));
                    long millisecond = Long.parseLong(smsDate);

                    String dateString = DateFormat.format("dd/MMM/yyyy", new Date(millisecond)).toString();
                    String type;

                    String splitedate[] = dateString.split("/");
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            type = "inbox";

                            if (number.length() == 10 || number.length() == 13) {
                                s = s + "NULL message";
                            } else {
                                Matcher m = regEx.matcher(bodylowercase);
                                String amts = "";

                                if (m.find()) {
                                    if (splitedate[2].trim().equals(year)) {
                                        if (bodylowercase.contains("debited") || bodylowercase.contains("paid") || bodylowercase.contains("spent")) {
                                            amts = getamts(bodylowercase, "debited");
                                            if (amts.length() > 0) {
                                                if (amts.charAt(0) == '.') {
                                                    String am[] = amts.split(".", 2);
                                                    amts = am[1];
                                                }

                                                if (splitedate[1].trim().equalsIgnoreCase("jan")) {
                                                    jantotal = jantotal + Double.parseDouble(amts);
                                                    jantotalb = jantotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("feb")) {
                                                    febtotal = febtotal + Double.parseDouble(amts);
                                                    febtotalb = febtotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("mar")) {
                                                    martotal = martotal + Double.parseDouble(amts);
                                                    martotalb = martotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("apr")) {
                                                    aprtotal = aprtotal + Double.parseDouble(amts);
                                                    aprtotalb = aprtotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("may")) {
                                                    maytotal = maytotal + Double.parseDouble(amts);
                                                    maytotalb = maytotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("jun")) {
                                                    juntotal = juntotal + Double.parseDouble(amts);
                                                    juntotalb = juntotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("jul")) {
                                                    jultotal = jultotal + Double.parseDouble(amts);
                                                    jultotalb = jultotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("aug")) {
                                                    augtotal = augtotal + Double.parseDouble(amts);
                                                    augtotalb = augtotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("sep")) {
                                                    septotal = septotal + Double.parseDouble(amts);
                                                    septotalb = septotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("oct")) {
                                                    octtotal = octtotal + Double.parseDouble(amts);
                                                    octtotalb = octtotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("nov")) {
                                                    novtotal = novtotal + Double.parseDouble(amts);
                                                    novtotalb = novtotal;
                                                } else if (splitedate[1].trim().equalsIgnoreCase("dec")) {
                                                    dectotal = dectotal + Double.parseDouble(amts);
                                                    dectotalb = dectotal;
                                                }
                                                //    totaldeb = totaldeb + Double.parseDouble(amts);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
//                        case Telephony.Sms.MESSAGE_TYPE_SENT:
//                            type = "sent";
//                            break;
//                        case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
//                            type = "outbox";
//                            break;
                        default:
                            break;
                    }
                    c.moveToNext();

                }
                dectotalb =  novtotalb + (novtotalb*4.48/100);
                //tv.setText(jantotal + "\n" + febtotal + "\n" + martotal + "\n" + aprtotal + "\n" + maytotal + "\n" + juntotal + "\n" + jultotal + "\n" + augtotal + "\n" + septotal + "\n" + octtotal + "\n" + novtotal + "\n" + dectotal);

            }
            c.close();
            // tv.setText(jantotal+"\n"+febtotal+"\n"+martotal+"\n"+aprtotal+"\n"+maytotal+"\n"+juntotal+"\n"+jultotal+"\n"+augtotal+"\n"+septotal+"\n"+octtotal+"\n"+novtotal+"\n"+dectotal);


        } else {
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
        }
        return year;
    }

    private String getamts(String bodylowercase, String creordeb) {
        String amt[] = {};
        amts = "";
        amtsfordisplay = "";
        if (bodylowercase.contains(" inr")) {
            amt = bodylowercase.split("inr", 2);
        } else if (bodylowercase.contains("rs")) {
            amt = bodylowercase.split("rs", 2);
        }
        for (int i = 0; i < amt[1].length(); i++) {
            if (amt[1].charAt(i) >= 48 && amt[1].charAt(i) <= 57 || amt[1].charAt(i) == ' ' || amt[1].charAt(i) == ',' || amt[1].charAt(i) == '.') {
                if (amt[1].charAt(i) == ',') {
                    amtsfordisplay = amtsfordisplay + amt[1].charAt(i);
                } else {
                    amts = amts + amt[1].charAt(i);
                    amtsfordisplay = amtsfordisplay + amt[1].charAt(i);
                }
            } else {
                break;
            }
        }
        //amts = "\n" + creordeb + " amount" + amts;
        return amts;
    }

    private void getEntries() {
        lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(0f, (float) octtotalb));
        lineEntries.add(new Entry(1f, (float) novtotalb));
        lineEntries.add(new Entry(2f, (float) dectotalb));
    }
}
