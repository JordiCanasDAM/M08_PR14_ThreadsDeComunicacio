package com.example.m08_pr14_threadsdecomunicacio;

//URL -> https://api.myip.com

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialitzem model
        ArrayList<String> lines = new ArrayList<String>();
        ArrayAdapter<String> adapter;

        // Inicialitzem l'ArrayAdapter amb el layout pertinent
        adapter = new ArrayAdapter<String>( this, R.layout.activity_list_item, lines )
        {
            @Override
            public View getView(int pos, View convertView, ViewGroup container)
            {
                if( convertView==null ) {
                    convertView = getLayoutInflater().inflate(R.layout.activity_list_item, container, false);
                }
                ((TextView) convertView.findViewById(R.id.textView)).setText(getItem(pos));
                return convertView;
            }

        };

        // busquem la ListView i li endollem el ArrayAdapter
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        Button button = findViewById(R.id.button);
        ListView text = findViewById(R.id.listView);

        button.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String data = getDataFromUrl("https://api.myip.com");
                        Log.i("INFO:", data);
                        lines.add(data);
                        adapter.notifyDataSetChanged();
                        handler.post(new Runnable() {
                            @Override public void run() {

                            }
                        });
                    }
                });
            }
        });
    }



    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        String error = "";
        try {

            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}