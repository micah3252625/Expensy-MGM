package com.example.expensy_mgm.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.expensy_mgm.R;

public class AnalyticsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        ImageView imageBack = view.findViewById(R.id.imageBack);
        imageBack.setOnClickListener(this::onClick);
        return view;
    }

    private void onClick(View view) {
        getFragmentManager().beginTransaction().remove(AnalyticsFragment.this).commit();
    }
}