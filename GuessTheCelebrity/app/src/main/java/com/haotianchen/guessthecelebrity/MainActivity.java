package com.haotianchen.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs = new ArrayList<>();
    ArrayList<String> celebnames = new ArrayList<>();
    int chosenCeleb = 0;
    ImageView imageView;
    Button button1;
    Button button2;

    Button button3;
    Button button4;



    int locationOfCorrentAnswer = 0;
    String[] answer = new String[4];

    public void celebChosen(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrentAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebnames.get(chosenCeleb), Toast.LENGTH_LONG).show();

        }
        helper();
    }


    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char cur = (char) data;
                    result += cur;
                    data = reader.read();

                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        button1 = (Button)findViewById(R.id.button0);
        button2 = (Button)findViewById(R.id.button1);
        button3 = (Button)findViewById(R.id.button2);
        button4 = (Button)findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();

        String result = null;

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\"");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");

            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebURLs.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");

            m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebnames.add(m.group(1));
            }

            helper();
            //Log.i("Content of URL:", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void helper() {

        try {
            Random random = new Random();
            chosenCeleb = random.nextInt(celebnames.size());


            DownloadImage imageTask = new DownloadImage();

            Bitmap celeImage;

            celeImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celeImage);

            int incorrect;

            locationOfCorrentAnswer = random.nextInt(4);

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrentAnswer) {
                    answer[i] = celebnames.get(chosenCeleb);
                } else {

                    incorrect = random.nextInt(celebnames.size());

                    while (incorrect == chosenCeleb) {
                        incorrect = random.nextInt(celebnames.size());
                    }
                    answer[i] = celebnames.get(incorrect);
                }

            }
            button1.setText(answer[0]);
            button2.setText(answer[1]);
            button3.setText(answer[2]);
            button4.setText(answer[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
