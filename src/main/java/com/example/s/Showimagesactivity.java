package com.example.s;

import android.content.Context;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

public class Showimagesactivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Db d;
    Context con;
    RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimagesactivity);
        recyclerView=findViewById(R.id.rcv);
        d=new Db(this);


    }
    public void getdata(View view){
        adapter=new RecyclerViewAdapter(d.getall());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}