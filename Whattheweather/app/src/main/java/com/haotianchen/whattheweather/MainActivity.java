package com.haotianchen.whattheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText editText;
    TextView textView;

    public class FindWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;

            try {

                Log.i("city:", strings[0]);

                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                String res = "";

                while (data != -1) {

                    res += (char)data;

                    data = inputStreamReader.read();

                }

                return res;


            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.i("city:", s);

            super.onPostExecute(s);



            try {

                JSONObject jsonObject = new JSONObject(s);

                String weather = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weather);

                String res = "";

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    res += "City:" + jsonObject1.getString("main");

                    res += " Weather:" + jsonObject1.getString("description");

                }

                textView.setText(res);

            } catch (Exception e) {

            }
        }
    }
    public void findTheWeather(View v) {
        FindWeather findWeather = new FindWeather();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        String city = editText.getText().toString();
        String encoded = "";
        try {
            encoded = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String html = "http://api.openweathermap.org/data/2.5/weather?q=" +
                encoded +
                "&appid=fec1cf03a8f6bd5c57a8bff78dcf7c6a";
        findWeather.execute(html);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.textView4);
    }
}
