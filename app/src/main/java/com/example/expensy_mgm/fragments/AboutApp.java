package com.example.expensy_mgm.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensy_mgm.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutApp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutApp extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutApp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutApp.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutApp newInstance(String param1, String param2) {
        AboutApp fragment = new AboutApp();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);

        // LINKIFY
        TextView textGithub_url = view.findViewById(R.id.textGithub_url);
        textGithub_url.setText("https://github.com/micah3252625/Expensy-MGM");
        Linkify.addLinks(textGithub_url, Linkify.WEB_URLS);

        TextView textUI_url = view.findViewById(R.id.textUI_url);
        textUI_url.setText("https://dribbble.com/shots/11123324-Notes-App");
        Linkify.addLinks(textUI_url, Linkify.WEB_URLS);

        TextView textFonts_url = view.findViewById(R.id.textFonts_url);
        textFonts_url.setText("https://fonts.google.com/specimen/Ubuntu");
        Linkify.addLinks(textFonts_url, Linkify.WEB_URLS);

        // Inflate the layout for this fragment
        return view;
    }
}