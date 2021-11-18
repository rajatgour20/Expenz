package com.example.s;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyHolder> {
    Context context;
    ArrayList<MessageModelClass> msz;

    public CustomAdapter(Context context, ArrayList<MessageModelClass> msz) {
        this.context = context;
        this.msz = msz;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.custom_list_item, viewGroup, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        String amtdeborcred=msz.get(i).getDeborcredamt();
        if(msz.get(i).getDeborcredamt().length()>0)
        if(msz.get(i).getDeborcredamt().charAt(0)=='.'){
                String am[]=amtdeborcred.split(".",2);
                amtdeborcred=am[1];
        }
        myHolder.body.setText(msz.get(i).getBody());
        myHolder.date.setText(msz.get(i).getDate());
        myHolder.avail.setText(msz.get(i).getAvail());
        myHolder.type.setText(msz.get(i).getType());
        myHolder.acc.setText(msz.get(i).getAcc());
        if(msz.get(i).getType().equals("debit")){
            myHolder.deborcredamt.setText("₹ -"+amtdeborcred);
            myHolder.deborcredamt.setTextColor(Color.RED);
        }
        else{
            myHolder.deborcredamt.setText("₹ "+amtdeborcred);
            myHolder.deborcredamt.setTextColor(Color.GREEN);
        }
        if(msz.get(i).getTransaction_tye().trim().equals("upi"))
            myHolder.iv.setImageResource(R.drawable.upi_icon);
        else if(msz.get(i).getTransaction_tye().trim().equals("atm")){
            myHolder.iv.setImageResource(R.drawable.ic_baseline_atm_24);
        }else if(msz.get(i).getTransaction_tye().trim().equals("imps")){
            myHolder.iv.setImageResource(R.drawable.imps_icon);
        }
    }

    @Override
    public int getItemCount() {
        return msz.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView body;
        TextView date;
        TextView avail;
        TextView type;
        TextView deborcredamt;
        TextView acc;

        public MyHolder(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.icon);
            body = v.findViewById(R.id.msz);
            date= v.findViewById(R.id.date);
            avail=v.findViewById(R.id.avail);
            type=v.findViewById(R.id.type);
            deborcredamt=v.findViewById(R.id.deborcred);
            acc=v.findViewById(R.id.acc);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, ""+body.getText().toString(), Toast.LENGTH_SHORT).show();
                    final Dialog d=new Dialog(context);
                    d.setContentView(R.layout.pop_up_msz);
                    TextView msz=d.findViewById(R.id.msz);
                    msz.setText(body.getText().toString());
                     d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    d.show();
                }
            });
        }
    }
}
