package com.example.controlswitch.Helpers;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConectionHelper extends AsyncTask<String, Void, RespuestaSwitch> {

    OkHttpClient client = new OkHttpClient();
    public static final String IP="http://192.168.5.171";
    @Override
    protected void onPostExecute(RespuestaSwitch respuestaSwitch) {
        super.onPostExecute(respuestaSwitch);
    }

    @Override
    protected RespuestaSwitch doInBackground(String... strings) {
        RespuestaSwitch status;
        String rta;
        Gson gson;
        String _url=IP+strings[0];

        if(strings.length>1){
            _url+="?seconds="+strings[1];
        }

        try {
            rta= makeRequest(_url);
            gson = new Gson();
            status=gson.fromJson(rta, RespuestaSwitch.class);
            //se cambian los valores de los switch
            return status;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String makeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


}