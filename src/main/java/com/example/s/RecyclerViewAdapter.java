package com.example.s;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Myholder> {
    ArrayList<Modelclass> arrayList=new ArrayList<>();

    public RecyclerViewAdapter(ArrayList<Modelclass> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Myholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        Modelclass ob=arrayList.get(position);
        holder.tv.setText(ob.getImagename());
        holder.iv.setImageBitmap(ob.getImage());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Myholder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView iv;
        public Myholder(@NonNull View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.text);
            iv=itemView.findViewById(R.id.image);

        }
    }
}
