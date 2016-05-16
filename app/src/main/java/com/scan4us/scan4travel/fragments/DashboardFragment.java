package com.scan4us.scan4travel.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scan4us.scan4travel.R;

/**
 * Created by RanKey on 15/05/2016.
 */
public class DashboardFragment extends Fragment {

    private TextView tvLabelName;
    private String titleText ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //titleText =  getArguments().getString("Dashboard");
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //tvLabelName = (TextView) getActivity().findViewById(R.id.txtlabel);
        //tvLabelName.setText(titleText);
    }

}
