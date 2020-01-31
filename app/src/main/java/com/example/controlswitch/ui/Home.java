package com.example.controlswitch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.controlswitch.Helpers.ConectionHelper;
import com.example.controlswitch.Helpers.RespuestaSwitch;
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
    int valor_timer;
    private Handler handler;
    private SharedPreferences sharedPref;

    private RadioGroup rg_switch1;
    private RadioGroup rg_switch2;

    //RadioButtons
    private RadioButton rb_switch1on;
    private RadioButton rb_switch1off;

    private RadioButton rb_switch2on;
    private RadioButton rb_switch2off;

    private ProgressBar progressBar;

    private LinearLayout containerSwitchs;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        //this.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);

        progressBar=v.findViewById(R.id.progressBarHome);

        //RadioGroups
        rg_switch1= v.findViewById(R.id.switch1);
        rg_switch2= v.findViewById(R.id.switch2);

        //RadioButtons
        rb_switch1on= v.findViewById(R.id.switch1on);
        rb_switch1off= v.findViewById(R.id.switch1off);

        rb_switch2on= v.findViewById(R.id.switch2on);
        rb_switch2off= v.findViewById(R.id.switch2off);

        containerSwitchs=v.findViewById(R.id.linearLayoutContainerGroups);

        this.connect();

        rg_switch1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.switch1on){
                    //Toast.makeText(getContext(),"Prendido",Toast.LENGTH_SHORT).show();
                    new ConectionHelper().execute("/chargerOn");
                    Intent msgIntent = new Intent(getActivity(), BatteryDaemon.class);
                    getActivity().startService(msgIntent);
                }else{
                    //turn off
                    new ConectionHelper().execute("/chargerOff");
                    getActivity().stopService(new Intent(getActivity(),BatteryDaemon.class));
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

    public void connect(){
        HandlerThread thread = new HandlerThread("HandlerThread");
        thread.start();
        handler=new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run () {
                try{
                    state =new ConectionHelper().execute("/").get();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            containerSwitchs.setVisibility(View.VISIBLE);
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
                        }
                    });

                }
                catch(Exception e){

                }
            }
        });
    }
}

