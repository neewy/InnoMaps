package com.innopolis.maps.innomaps.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;

public class SplashScreenActivity extends Activity {

    private ProgressBar mProgressBar;
    private TextView text_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadViewTask().execute();
    }

    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute()
        {

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.splash_screen);
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            text_loading = (TextView) findViewById(R.id.text_loading);

            mProgressBar.setMax(100);
            mProgressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                synchronized (this)
                {
                    int counter = 0;
                    while(counter < 20)
                    {
                        this.wait(200);
                        counter++;
                        publishProgress(counter*5);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            mProgressBar.setProgress(values[0]);
            text_loading.setText(String.format(getString(R.string.loadingplus), values[0]) + "%");

        }

        @Override
        protected void onPostExecute(Void result)
        {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }
}