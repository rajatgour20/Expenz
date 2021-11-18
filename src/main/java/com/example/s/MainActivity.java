package com.example.s;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rcv;
    ArrayList<MessageModelClass> Message;
    RecyclerView.Adapter Messageadapter;
    RecyclerView.LayoutManager mgr;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String msgData = "";
    Spinner month;


    ArrayList<String> lst1 = new ArrayList<>();
    static String amts = "";
    String avail = "";
    static String amtsfordisplay = "";
    TextView totaldebtv, totalcredtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcv = findViewById(R.id.rcv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        totalcredtv = findViewById(R.id.total_cred);
        totaldebtv = findViewById(R.id.total_deb);

        drawerLayout = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigationbar, R.string.close_navigationbar);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navi_view);
        navigationView.setNavigationItemSelectedListener(this);

        month = findViewById(R.id.month);
        setmonthspinner();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {
            String SelectedDate[] = month.getSelectedItem().toString().split(" ", 2);
            msgData = getAllSms(this, SelectedDate[0], SelectedDate[1]);
        }
        setbottomnav();

    }

    private void setbottomnav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bank:
                        break;
                    case R.id.cash:
                        startActivity(new Intent(MainActivity.this,ManualExoenses_Activity.class));
                        break;
                    case R.id.bill:
                        startActivity(new Intent(MainActivity.this, BillActivity.class));
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
                String monthname = parent.getItemAtPosition(position).toString();
              //  Toast.makeText(parent.getContext(), "Selected: " + monthname, Toast.LENGTH_LONG).show();
                String SelectedDate[] = month.getSelectedItem().toString().split(" ", 2);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                } else {
                    msgData = getAllSms(MainActivity.this, SelectedDate[0], SelectedDate[1]);
                }
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

    public String getAllSms(Context context, String month, String year) {
        Message = new ArrayList<>();
        setAdapter();
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        int count = 0;
        String s = "";
        String ban = "";
        double totalcred = 0;
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
                                amts = "";
                                amtsfordisplay = "";

                                if (m.find()) {
                                 //   Toast.makeText(context, "" + m.group(), Toast.LENGTH_SHORT).show();
                                    if (splitedate[1].trim().equals(month) && splitedate[2].trim().equals(year)) {
                                        String accno = getaccountno(bodylowercase);
                                        if (bodylowercase.contains("debited") || bodylowercase.contains("paid") || bodylowercase.contains("spent")) {
                                            amts = getamts(bodylowercase, "debited");
                                            type = "debit";
                                            avail = getavail(bodylowercase);
                                            amts=amts.trim();
                                            if (amts.length() > 0) {
                                                if (amts.charAt(0) == '.') {
                                                    String am[] = amts.split(".", 2);
                                                    amts = am[1];
                                                }

                                                try {
                                                    totaldeb = totaldeb + Double.parseDouble(amts);
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                        //abhi credited ka
                                        else if (bodylowercase.contains("credited") || bodylowercase.contains("received")) {
                                            amts = getamts(bodylowercase, "credited");
                                            type = "credit";
                                            avail = getavail(bodylowercase);
                                            amts=amts.trim();
                                            if (amts.length() > 0) {
                                                if (amts.charAt(0) == '.') {
                                                    String am[] = amts.split(".", 2);
                                                    amts = am[1];
                                                }
                                                try {
                                                    totalcred = totalcred + Double.parseDouble(amts);
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                        String trans_type = "bank";
                                        if (bodylowercase.contains("upi")) {
                                            trans_type = "upi";
                                        } else if(bodylowercase.contains("imps")){
                                            trans_type="imps";
                                        }else if(bodylowercase.contains("debit card")){
                                            trans_type="atm";
                                        }
                                        MessageModelClass messageModelClass = new MessageModelClass(body, dateString, avail, type, amts, "Account nnumber-" + accno,trans_type);
                                        Message.add(messageModelClass);
                                        Messageadapter.notifyDataSetChanged();
                                        s = s + dateString + number + body;
                                    }


                                    //debit wale transactions
                                    if (body.contains("debited") || body.contains(" debited ")) {
                                        count++;
                                        lst1.add("debited " + count);
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

            }
            c.close();

        } else {
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
        }
//        totalcredtv.setText(totalcred + "Total cred");
//        totaldebtv.setText(totaldeb + "total deb");

        totalcredtv.setText(totalcred + "");
        totaldebtv.setText(totaldeb + "");

        return s;
    }

    private String getaccountno(String body) {
        String accNo = "xx";
        //starting me 2 x hona must
        if (body.contains("xx")) {
            String amt[] = body.split("xx", 2);
            for (int i = 0; i < amt[1].length(); i++) {
                //index from 3rd element
                if (amt[1].charAt(i) == ' ') {
                    break;
                } else if (amt[1].charAt(i) >= 48 && amt[1].charAt(i) <= 57 || amt[1].charAt(i) == 120) {
                    accNo = accNo + amt[1].charAt(i);
                }
            }
        }
        return accNo;
    }

    private void setAdapter() {
        mgr = new LinearLayoutManager(MainActivity.this);
        rcv.setLayoutManager(mgr);
        Messageadapter = new CustomAdapter(this, Message);
        rcv.setAdapter(Messageadapter);
    }

    private String getavail(String body) {
        String availAmtfordisplay = "";
        String availAmt = "";
        String[] avail = {};
        String amt[] = {};
        boolean toggle = false;
        if (body.contains("inr") && body.contains("bal")) {
            amt = body.split("inr", 2);
            if (amt[1].contains("inr")) {
                avail = amt[1].split("inr", 2);
                toggle = true;
            }
        } else if (body.contains("rs") && body.contains("bal")) {
            amt = body.split("rs", 2);
            if (amt[1].contains("rs")) {
                avail = amt[1].split("rs", 2);
                toggle = true;
            }
        }
        if (toggle) {
            for (int i = 0; i < avail[1].length(); i++) {
                if (avail[1].charAt(i) >= 48 && avail[1].charAt(i) <= 57 || avail[1].charAt(i) == ' ' || avail[1].charAt(i) == ',' || avail[1].charAt(i) == '.') {
                    if (avail[1].charAt(i) == ',') {
                        availAmtfordisplay = availAmtfordisplay + avail[1].charAt(i);
                    } else {
                        availAmt = availAmt + avail[1].charAt(i);
                        availAmtfordisplay = availAmtfordisplay + avail[1].charAt(i);
                    }
                } else {
                    break;
                }
            }
            //ye akhri wala dot htane k liye
            //availAmt = availAmt.substring(0, availAmt.length() - 2);
            availAmt = "\navailable amount" + availAmt;
            return availAmt;
        }//ye available amount print krne k liye
        return "null";

    }


    private String getamts(String bodylowercase, String creordeb) {
        String amt[] = {};
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.trip) {
            Toast.makeText(this, "trip", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,SplitScreen.class));
        }
        else if (item.getItemId() == R.id.chart) {
            Toast.makeText(this, "stats", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,stats.class));

        }
        else if (item.getItemId() == R.id.privacy) {
            Toast.makeText(this, "privacy", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,Privacy_Policy.class));
        }
        else if (item.getItemId() == R.id.connect_us) {
            Toast.makeText(this, "connect us", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_SENDTO)
                    .setData(new Uri.Builder().scheme("mailto").build())
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{ "ritvikdubeyrk@gmail.com" });
            startActivity(intent);

        }
        else if(item.getItemId()==R.id.prediction){
            Toast.makeText(this, "Predictions", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,FutureActivity.class));
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
}