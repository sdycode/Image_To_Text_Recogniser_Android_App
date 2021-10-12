package com.example.businesscardstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesscardstore.Model.model;
import com.example.businesscardstore.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context context;
    ArrayList<model> models;

    public Adapter(Context context, ArrayList<model> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.text_card_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( Adapter.ViewHolder holder, int position) {
        model m = models.get(position);
        holder.cardText.setText(m.getLineTextOnCard());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardText;
        public ViewHolder( View itemView) {
            super(itemView);
            cardText = itemView.findViewById(R.id.textOnCardId);
        }
    }
}
