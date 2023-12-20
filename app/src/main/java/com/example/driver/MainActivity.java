package com.example.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewResult;
    private TextView mEditTextName;
    int number;
    int driver;
    private Button button1;
    private EditText edittext1;
    private static String IP_ADDRESS = "ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com/driver/index.php?stop_id";
    private static String TAG = "phptest";
    private TextView txtResult;
    List<next_stop> next_stopList = new ArrayList<next_stop>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //next_stopList.add(new next_stop());
        button1 = (Button) findViewById(R.id.button);
        mTextViewResult = (TextView) findViewById(R.id.showInfo);
        edittext1 = (EditText) findViewById(R.id.text);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());
        String line = null;
        String readStr = "";
        File idFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/userid.txt");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                InsertData task = new InsertData();
                task.execute("http://ec2-52-193-213-157.ap-northeast-1.compute.amazonaws.com/driver/index.php?stop_id="+edittext1.getText()); //여기 인원수 받아오
//                Toast.makeText(getApplicationContext(), "ㅎㅇ", Toast.LENGTH_SHORT).show();

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            show(); //여기에 딜레이 후 시작할 작업들을 입력
//                        }
//                    }, 100);


            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray next_stopArray = jsonObject.getJSONArray("next_stop");

                JSONObject stopObject = next_stopArray.getJSONObject(0);


                next_stop stop = new next_stop();
                stop.setStop_id(stopObject.getString("stop_id"));
                stop.setPeople(stopObject.getString("people"));
                stop.setWheel(stopObject.getString("wheel"));

                next_stopList.clear();
                next_stopList.add(stop);
                Log.d(TAG, "POST response  1- " + stop.getPeople());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            next_stop bus = next_stopList.get(0);
            Log.d(TAG, "POST response  0- " + next_stopList.size());

            progressDialog.dismiss();
            if(bus.getWheel().equals("stop_id 오류")){
                mTextViewResult.setText("정류소 아이디 오류");

            }
            else{
                mTextViewResult.setText("총 승차객  : " + bus.getPeople() + "\n승차객중 휠체어 : " + bus.getWheel());
            }
            Log.d(TAG, "POST response  1- " + bus.getPeople());

        }


        @Override
        protected String doInBackground(String... params) {

            //String name = (String)params[1];
            //String country = (String)params[2];

            String serverURL = (String) params[0];

            //String postParameters = "name=" + name + "&country=" + country;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.connect();


                /*
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();*/


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK || responseStatusCode == HttpURLConnection.HTTP_CREATED) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    public class next_stop {
        private String stop_id = "네트워크가 원활하지 않습니다.";
        private String people = "네트워크가 원활하지 않습니다.";
        private String wheel = "네트워크가 원활하지 않습니다.";

        public String getStop_id() {
            return stop_id;
        }

        public String getPeople() {
            return people;
        }
        public String getWheel() {
            return wheel;
        }

        public void setStop_id(String stop_id) {
            this.stop_id = stop_id;
        }

        public void setPeople(String people) {
            this.people = people;
        }
        public void setWheel(String wheel) {
            this.wheel = wheel;
        }

    }

    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("다음정류장");
        //final CharSequence[] items = {driver.get(1).getStop_id()};

        AlertDialog alertDialog = builder.create();
        builder.show();
    }

}
