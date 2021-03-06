package com.example.svslsavemoneysavelife.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.models.Month;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.ViewHolder> {
    private ArrayList<Month> mData = new ArrayList<>();
    private Context context;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat df = new SimpleDateFormat("MMMM");

    public RateAdapter(ArrayList<Month> data) {
        this.mData.addAll(data);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_rate, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(df.format(mData.get(position).getMonthStart()) + context.getString(R.string.month));
        double total = mData.get(position).getTotalExpanse();
        double limit = mData.get(position).getMonthLimit();
        holder.price.setText(String.format("%.2f", total) + context.getString(R.string.sr));
        holder.limit.setText(String.format("%.2f", limit) + context.getString(R.string.sr));

        if (total / limit <= 1.0) {
            holder.comment.setText(context.getText(R.string.five));
            holder.ratingBar.setRating(5);
        } else if (total / limit <= 1.15) {
            holder.comment.setText(context.getText(R.string.four));
            holder.ratingBar.setRating(4);
        } else if (total / limit <= 1.4) {
            holder.comment.setText(context.getText(R.string.three));
            holder.ratingBar.setRating(3);
        } else if (total / limit <= 1.8) {
            holder.comment.setText(context.getText(R.string.two));
            holder.ratingBar.setRating(2);
        } else if (total / limit > 1.8) {
            holder.comment.setText(context.getText(R.string.one));
            holder.ratingBar.setRating(1);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(ArrayList<Month> newData) {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        RatingBar ratingBar;
        TextView title, price, limit, comment;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            limit = itemView.findViewById(R.id.limit);
            comment = itemView.findViewById(R.id.comment);
            ratingBar = itemView.findViewById(R.id.rate);

        }
    }
}
