package com.example.petr.simplweather;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView teplota, vlhkost, vitr;
    String pocasiTeplota, pocasiVlhkost, pocasiVitr;

    public static Handler handler;
    Message message;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // zakazat menit orientaci obrazovky
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        teplota = (TextView) findViewById(R.id.text_teplota);
        vlhkost = (TextView) findViewById(R.id.text_vlhkost);
        vitr = (TextView) findViewById(R.id.text_vitr);

        setTitle("Dolní Benešov");


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = new Bundle();
                bundle = msg.getData();
                String data = bundle.getString("pocasi");

                if (data.equals("nastav"))
                {
                    teplota.setText("Teplota: " + pocasiTeplota);
                    vlhkost.setText("Vlhkost: " + pocasiVlhkost);
                    vitr.setText("Vítr: " + pocasiVitr);
                }
            }
        };
    }


    @Override
    public void onStart(){
        super.onStart();

        //getWeather();
        vlakno();
    }


    public void vlakno(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getWeather();
            }
        });
        thread.start();
    }


    public void getWeather(){


        BufferedReader br = null;

        try {

            URL url = new URL("https://www.in-pocasi.cz/predpoved-pocasi/cz/moravskoslezsky/dolni-benesov-70/");
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            //StringBuilder sb = new StringBuilder();

            while((line = br.readLine()) != null)
            {
                if(line.contains("Teplota:"))
                {
                    line = line.replace("</span>", "");
                    line = line.replace("<br />", "");
                    line = line.substring(line.indexOf(">")+1, line.indexOf("("));

                    pocasiTeplota = line;
                }

                if(line.contains("Vlhkost:"))
                {
                    line = line.replace("</span>", "");
                    line = line.replace("<br />", "");
                    line = line.substring(line.indexOf(">")+1);

                    pocasiVlhkost = line;
                }

                if(line.contains("Vítr:"))
                {
                    line = line.replace("</span>", "");
                    line = line.substring(line.indexOf(">")+1);

                    pocasiVitr = line;
                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally
        {
            if(br != null)
            {
                try {
                    br.close();
                } catch (IOException ex) {
                    //Logger.getLogger(ReadWebPage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        sendCommand("nastav");
    }


    public void sendCommand(String command){

        try{
            message = new Message();
            bundle = new Bundle();
            bundle.putString("pocasi", command);
            message.setData(bundle);
            MainActivity.handler.sendMessage(message);
        } catch (Exception e){
        }
    }
}
