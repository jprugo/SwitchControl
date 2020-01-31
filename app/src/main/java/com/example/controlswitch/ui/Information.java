package com.example.controlswitch.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.controlswitch.Helpers.ConectionHelper;
import com.example.controlswitch.Helpers.RespuestaSwitch;
import com.example.controlswitch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Information.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Information#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Information extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RespuestaSwitch state;
    private OnFragmentInteractionListener mListener;
    private Animation rotateAnimation;
    private ImageView fanView;
    private TextView textViewInfo;
    private TextView textViewNumber;
    private Handler handler;
    private ProgressBar progressBar;
    public Information() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Information.
     */
    // TODO: Rename and change types and number of parameters
    public static Information newInstance(String param1, String param2) {
        Information fragment = new Information();
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
    public void onResume() {
        super.onResume();
        //this.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_information, container, false);

        progressBar=v.findViewById(R.id.progressBarInfo);

        fanView=v.findViewById(R.id.imageViewFan);
        textViewInfo=v.findViewById(R.id.textView_fan_legend);
        textViewNumber=v.findViewById(R.id.textView_timer);

        this.connect();

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
    private void StartAnimation(){
        rotateAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        fanView.startAnimation(rotateAnimation);
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
                            textViewInfo.setVisibility(View.VISIBLE);
                            fanView.setVisibility(View.VISIBLE);
                            if(state.fanisOn()){
                                StartAnimation();
                                textViewInfo.setText(R.string.fan_on);
                                textViewNumber.setText(" "+(state.getTimer()/60)+" minutes");
                            }else{
                                fanView.setImageResource(R.drawable.fan_off);
                                textViewInfo.setText(R.string.fan_off);
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
