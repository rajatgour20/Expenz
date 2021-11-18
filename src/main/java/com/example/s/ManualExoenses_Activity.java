package com.example.s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ManualExoenses_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rcv;
    ArrayList<CourseModal> a;
    ArrayList<String> Message1;
    RecyclerView.Adapter Messageadapter;
    RecyclerView.LayoutManager layoutManager;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String msgData = "";
    Spinner month;
    ImageView add;

    DBHandler database;
    ArrayList<String> lst1 = new ArrayList<>();
    static String amts = "";
    String avail = "";
    static String amtsfordisplay = "";
    TextView totaldebtv, totalcredtv;

    private ArrayList<CourseModal> courseModalArrayList;
    private DBHandler dbHandler;

    double totalcred=0;double totaldeb=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_exoenses_);
        rcv = findViewById(R.id.rcv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        month = findViewById(R.id.month);

        Message1=new ArrayList<>();
        dbHandler = new DBHandler(ManualExoenses_Activity.this);

        setmonthspinner();
        String mon_year=month.getSelectedItem().toString();
        String split_mon_year[]=mon_year.split(" ");
        int monthno=getMonthnumber(split_mon_year[0]);
        Toast.makeText(this, ""+monthno, Toast.LENGTH_SHORT).show();
        a=new ArrayList<>();
        a= dbHandler.read(monthno+"",split_mon_year[1]);

        Messageadapter = new manual_expense_adapter(ManualExoenses_Activity.this,a);
        rcv.setAdapter(Messageadapter);
        database=new DBHandler(ManualExoenses_Activity.this);
        layoutManager=new LinearLayoutManager(ManualExoenses_Activity.this);
        rcv.setLayoutManager(layoutManager);

        add = findViewById(R.id.add);
        totalcredtv = findViewById(R.id.total_cred);
        totaldebtv = findViewById(R.id.total_deb);


        for(int i=0;i<a.size();i++){
            if(a.get(i).getTransactionType().trim().equals("Expense")){
                totaldeb= totaldeb+Double.parseDouble(a.get(i).getAmout());
            }
            else{
                totalcred= totalcred+Double.parseDouble(a.get(i).getAmout());
            }
        }
        totalcredtv.setText(totalcred+"");
        totaldebtv.setText(totaldeb+"");

        drawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigationbar, R.string.close_navigationbar);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navi_view);
        navigationView.setNavigationItemSelectedListener(this);



        setbottomnav();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(ManualExoenses_Activity.this);
                d.setContentView(R.layout.add_manual_expense);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();
                EditText title = d.findViewById(R.id.et_title);
                EditText amount = d.findViewById(R.id.et_amount);
                Spinner category = d.findViewById(R.id.et_tag);
                EditText date = d.findViewById(R.id.et_when);
                Spinner transtype = d.findViewById(R.id.et_transactionType);
                Button save = d.findViewById(R.id.btn_save_transaction);
                ImageView cal = d.findViewById(R.id.cal);
                final Calendar c = Calendar.getInstance();
                date.setEnabled(false);


                ArrayList<String> transtypearraylist = new ArrayList<>();
                transtypearraylist.add("Select Transaction Type");
                transtypearraylist.add("Income");
                transtypearraylist.add("Expense");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManualExoenses_Activity.this, android.R.layout.simple_spinner_item, transtypearraylist);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                transtype.setAdapter(arrayAdapter);

                ArrayList<String> cattypearray = new ArrayList<>();
                cattypearray.add("Select a category");
                cattypearray.add("Housing");
                cattypearray.add("Transportation");
                cattypearray.add("Food");
                cattypearray.add("Entertainment");
                cattypearray.add("Health Care");
                cattypearray.add("Other");

                ArrayAdapter<String> catarrayAdapter = new ArrayAdapter<String>(ManualExoenses_Activity.this, android.R.layout.simple_spinner_item, cattypearray);
                catarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(catarrayAdapter);

                cal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dp = new DatePickerDialog(ManualExoenses_Activity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                date.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dp.show();
                        dp.getDatePicker().setMaxDate(c.getTimeInMillis());

                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(title.getText().toString().equals("")){
                            title.setError("Missing Field");
                        }else if(amount.getText().toString().equals("")){
                            amount.setError("Missing Field");
                        }else if(date.getText().toString().equals("")){
                            date.setError("Missing Field");
                        }else if(transtype.getSelectedItem().toString().equals("Select Transaction Type")){
                            Toast.makeText(ManualExoenses_Activity.this, "Missing Transaction Type", Toast.LENGTH_SHORT).show();
                        }else if(category.getSelectedItem().toString().equals("Select a category")){
                            Toast.makeText(ManualExoenses_Activity.this, "Missing Category", Toast.LENGTH_SHORT).show();
                        }else{
                            String splitdate[]=date.getText().toString().trim().split("/",3);
                            String transtype1=transtype.getSelectedItem().toString();
                            String category1=category.getSelectedItem().toString();
                            String title1=title.getText().toString();
                            String amount1=amount.getText().toString();

                            CourseModal courseModal=new CourseModal(title1,amount1,splitdate[0],splitdate[1],splitdate[2],transtype1,category1);
                            int id=dbHandler.insert(ManualExoenses_Activity.this,courseModal);
                            //Toast.makeText(ManualExoenses_Activity.this, ""+title1+amount1+splitdate[0]+splitdate[1]+splitdate[2]+category1+transtype1, Toast.LENGTH_SHORT).show();

                            if(id>0) {
                                courseModal.setId(id);

                                String mon_year[] = month.getSelectedItem().toString().trim().split(" ", 2);
                                int mon = getMonthnumber(mon_year[0]);
                                if ((mon + "").equals(splitdate[1]) && mon_year[1].trim().equals(splitdate[2])) {
                                    a.add(courseModal);
                                    Messageadapter.notifyDataSetChanged();
                                    if (transtype1.equals("Expense")) {
                                        totaldeb = totaldeb + Double.parseDouble(amount1);
                                        totaldebtv.setText(totaldeb + "");
                                    } else {
                                        totalcred = totalcred + Double.parseDouble(amount1);
                                        totalcredtv.setText(totalcred + "");
                                    }
                                }
                            }

                            d.dismiss();

                        }
                    }
                });


            }

        });



    }

    private int getMonthnumber(String s) {
        Map<String,String> map=new HashMap<>();
        map.put("Jan","1");
        map.put("Feb","2");
        map.put("Mar","3");
        map.put("Apr","4");
        map.put("May","5");
        map.put("Jun","6");
        map.put("Jul","7");
        map.put("Aug","8");
        map.put("Sep","9");
        map.put("Oct","10");
        map.put("Nov","11");
        map.put("Dec","12");
        return Integer.parseInt(map.get(s));

    }


    private void setbottomnav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bank:
                        startActivity(new Intent(ManualExoenses_Activity.this, MainActivity.class));
                        break;
                    case R.id.cash:
                        break;
                    case R.id.bill:
                        startActivity(new Intent(ManualExoenses_Activity.this, BillActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void setmonthspinner() {
        ArrayList<String> months = new ArrayList<>();
        Calendar cc = new GregorianCalendar();
        cc.setTime(new Date());
        for (int i = 0; i < 24; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM YYYY");
            months.add(sdf.format(cc.getTime()));
            cc.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(arrayAdapter);

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mon_year=month.getSelectedItem().toString();
                String split_mon_year[]=mon_year.split(" ");
                int monthno=getMonthnumber(split_mon_year[0]);
                //Toast.makeText(ManualExoenses_Activity.this, ""+monthno, Toast.LENGTH_SHORT).show();
                a.clear();
                a.addAll(dbHandler.read(monthno+"",split_mon_year[1]));
                totalcred=0;
                totaldeb=0;
                for(int i=0;i<a.size();i++){
                    if(a.get(i).getTransactionType().trim().equals("Expense")){
                        totaldeb= totaldeb+Double.parseDouble(a.get(i).getAmout());
                    }
                    else{
                        totalcred= totalcred+Double.parseDouble(a.get(i).getAmout());
                    }
                }
                totalcredtv.setText(totalcred+"");
                totaldebtv.setText(totaldeb+"");
                Messageadapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        changenotificationcolor();
    }

    private void changenotificationcolor() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.notification));


    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.trip) {
            Toast.makeText(this, "trip", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SplitScreen.class));
        }
        else if (item.getItemId() == R.id.chart) {
            Toast.makeText(this, "stats", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, stats.class));

        }
        else if (item.getItemId() == R.id.privacy) {
            Toast.makeText(this, "privacy", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Privacy_Policy.class));
        }
        else if (item.getItemId() == R.id.connect_us) {
            Toast.makeText(this, "connect us", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SENDTO)
                    .setData(new Uri.Builder().scheme("mailto").build())
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{"ritvikdubeyrk@gmail.com"});
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.prediction) {
            Toast.makeText(this, "privacy", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FutureActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }


    public void delete() {
        String mon_year=month.getSelectedItem().toString();
        String split_mon_year[]=mon_year.split(" ");
        int monthno=getMonthnumber(split_mon_year[0]);
        a.clear();
        a.addAll(dbHandler.read(monthno+"",split_mon_year[1]));
        totalcred=0;
        totaldeb=0;
        for(int i=0;i<a.size();i++){
            if(a.get(i).getTransactionType().trim().equals("Expense")){
                totaldeb= totaldeb+Double.parseDouble(a.get(i).getAmout());
            }
            else{
                totalcred= totalcred+Double.parseDouble(a.get(i).getAmout());
            }
        }
        totalcredtv.setText(totalcred+"");
        totaldebtv.setText(totaldeb+"");
        Messageadapter.notifyDataSetChanged();

    }
}