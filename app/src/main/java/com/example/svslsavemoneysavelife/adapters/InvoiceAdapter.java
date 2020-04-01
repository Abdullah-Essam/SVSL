package com.example.svslsavemoneysavelife.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.models.Invoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private ArrayList<Invoice> mData = new ArrayList<>();
    private Context context;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public InvoiceAdapter(ArrayList<Invoice> data) {
        this.mData.addAll(data);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_invoice, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText("Invoice " + (position + 1));
        holder.price.setText(String.format("%.2f SR", mData.get(position).getAmount()));
        holder.date.setText(df.format(mData.get(position).getDate()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(ArrayList<Invoice> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView title, price, date;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
        }
    }
}
