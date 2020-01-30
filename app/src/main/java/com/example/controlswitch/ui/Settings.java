package com.example.controlswitch.ui;

import android.content.Context;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;



import com.example.controlswitch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Settings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private int porcentaje=90;
    private int valor_spinner=1;
    private SharedPreferences sharedPref;
    private OnFragmentInteractionListener mListener;

    public Settings() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        //Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPref = getContext().getSharedPreferences(
                "control_switch", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        final TextView percentage=v.findViewById(R.id.txtvpercentage);


        SeekBar sb=v.findViewById(R.id.seekbarbattery);
        sb.setOnSeekBarChangeListener(null);
        sb.setProgress(sharedPref.getInt("percentage_alert", 90));
        percentage.setText(sharedPref.getInt("percentage_alert", 90)+"");


        Spinner spinner = v.findViewById(R.id.chargerspinner);
        spinner.setSelection(sharedPref.getInt("timer_value", 1)-1,true);//"timer_value"
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valor_spinner= Integer.valueOf((String)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentage.setText(progress+"");
                porcentaje=progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainmenu_saveconfig:
                saveValuesInShared(porcentaje,valor_spinner);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveValuesInShared(int porcentaje,int t_value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("percentage_alert", porcentaje);
        editor.putInt("timer_value", t_value);
        editor.apply();
    }

}
