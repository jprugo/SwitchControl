package com.example.controlswitch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.controlswitch.Helpers.ConectionHelper;
import com.example.controlswitch.Helpers.RespuestaSwitch;
import com.example.controlswitch.MainActivity;
import com.example.controlswitch.R;
import com.example.controlswitch.Services.BatteryDaemon;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private RespuestaSwitch state;
    private OnFragmentInteractionListener mListener;
    int valor_timer;

    SharedPreferences sharedPref;
    public Home() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getContext().getSharedPreferences(
                "control_switch", Context.MODE_PRIVATE);

        valor_timer=sharedPref.getInt("timer_value",1);
        try{
            state =new ConectionHelper().execute("/").get();
            Log.i("prueba",state.toString());
        }
        catch(Exception e){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);

        RadioGroup rg_switch1= v.findViewById(R.id.switch1);
        RadioGroup rg_switch2= v.findViewById(R.id.switch2);

        RadioButton rb_switch1on= v.findViewById(R.id.switch1on);
        RadioButton rb_switch1off= v.findViewById(R.id.switch1off);

        RadioButton rb_switch2on= v.findViewById(R.id.switch2on);
        RadioButton rb_switch2off= v.findViewById(R.id.switch1off);

        rg_switch1.setOnCheckedChangeListener(null);
        rg_switch2.setOnCheckedChangeListener(null);

        if(state.chargerisOn()){
            rb_switch1on.setChecked(true);
        }else{
            rb_switch1off.setChecked(true);
        }

        if(state.fanisOn()){
            rb_switch2on.setChecked(true);
        }else{
            rb_switch2off.setChecked(true);
        }

        rg_switch1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.switch1on){
                    //turn on
                    new ConectionHelper().execute("/chargerOn");
                    Intent msgIntent = new Intent(getContext(), BatteryDaemon.class);
                    getActivity().sendBroadcast(msgIntent);
                }else{
                    //turn off
                    new ConectionHelper().execute("/chargerOff");
                    getActivity().stopService(new Intent(getContext(),BatteryDaemon.class));
                }
            }
        });


        rg_switch2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.switch2on){
                    String secs=(valor_timer*3600)+"";
                    new ConectionHelper().execute("/fanOn",secs);
                }else{
                    //turn off
                    new ConectionHelper().execute("/fanOff");
                }
            }
        });

        return v;
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

}
