package com.example.s;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Month;
import java.util.ArrayList;

public class manual_expense_adapter extends RecyclerView.Adapter<manual_expense_adapter.MyHolder> {
    Context context;
    ArrayList<CourseModal> courseModals;

    public manual_expense_adapter(Context context, ArrayList<CourseModal> courseModals) {
        this.context = context;
        this.courseModals = courseModals;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.manual_expense_card_view, viewGroup, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        myHolder.date.setText(courseModals.get(i).getDay() + "/" + courseModals.get(i).getMonth() + "/" + courseModals.get(i).getYear());
        myHolder.title.setText(courseModals.get(i).getTitle());
        if (courseModals.get(i).getTransactionType().equals("Income")) {
            myHolder.deborcred.setText("₹ " + courseModals.get(i).getAmout());
            myHolder.deborcred.setTextColor(Color.GREEN);
        } else {
            myHolder.deborcred.setText("₹ " + courseModals.get(i).getAmout());
            myHolder.deborcred.setTextColor(Color.RED);
        }
        myHolder.id.setText(courseModals.get(i).getId() + "");
        myHolder.type.setText(courseModals.get(i).getTransactionType());


        if (courseModals.get(i).getCategory().trim().equals("Housing")) {
            myHolder.iv.setImageResource(R.drawable.ic_baseline_house_24);
        } else if (courseModals.get(i).getCategory().trim().equals("Transportation")) {
            myHolder.iv.setImageResource(R.drawable.ic_baseline_emoji_transportation_24);
        } else if (courseModals.get(i).getCategory().trim().equals("Food")) {
            myHolder.iv.setImageResource(R.drawable.ic_baseline_fastfood_24);
        } else if (courseModals.get(i).getCategory().trim().equals("Entertainment")) {
            myHolder.iv.setImageResource(R.drawable.entertainment_icon);
        } else if (courseModals.get(i).getCategory().trim().equals("Health Care")) {
            myHolder.iv.setImageResource(R.drawable.ic_baseline_local_hospital_24);
        }
    }

    @Override
    public int getItemCount() {
        return courseModals.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView date, deborcred, type, id, title;
        ImageView iv,delete;

        public MyHolder(@NonNull View v) {
            super(v);
            date = v.findViewById(R.id.date);
            deborcred = v.findViewById(R.id.deborcred);
            type = v.findViewById(R.id.type);
            id = v.findViewById(R.id.ex_id);
            title = v.findViewById(R.id.title);
            iv = v.findViewById(R.id.icon);
            delete=v.findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHandler dbHandler=new DBHandler(context);
                    dbHandler.delete(id.getText().toString());
                    if(context instanceof ManualExoenses_Activity){

                        ManualExoenses_Activity activity = (ManualExoenses_Activity)context;
                        activity.delete();
                    }
                }
            });
        }

    }
}

