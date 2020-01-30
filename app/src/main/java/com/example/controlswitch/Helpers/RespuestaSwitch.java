package com.example.controlswitch.Helpers;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespuestaSwitch {

    @SerializedName("chargerstatus")
    @Expose
    private String chargerstatus;
    @SerializedName("fanstatus")
    @Expose
    private String fanstatus;
    @SerializedName("timer")
    @Expose
    private Integer timer;

    public boolean chargerisOn(){
        if(chargerstatus.equals("ON")){
            return true;
        }else{
            return false;
        }
        //return chargerstatus  == "ON" ? true : false;
    }
    public boolean fanisOn(){
        if(fanstatus.equals("ON")){
            return true;
        }else{return false;}
        //return fanstatus  == "ON" ? true : false;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
    public Integer getTimer() {
        return this.timer;
    }

}
