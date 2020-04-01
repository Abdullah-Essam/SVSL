package com.example.svslsavemoneysavelife.activities.ui.rate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.adapters.RateAdapter;
import com.example.svslsavemoneysavelife.controller.UserController;
import com.example.svslsavemoneysavelife.utils.SharedData;

public class RateFragment extends Fragment {
    private RecyclerView recyclerView;
    private RateAdapter adapter;
    private UserController userController = new UserController();

    public RateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        init();
        return view;
    }

    private void init() {
        adapter = new RateAdapter(SharedData.currentUser.getMonths());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
