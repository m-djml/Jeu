package com.example.jeu;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BugMove extends AsyncTask<String, String, String> {

    private String resp;
    private ProgressDialog progressDialog;
    private MainActivity main;
    private ImageView[] bugs_left;
    private ImageView[] bugs_right;
    private int score_value;

    public BugMove(MainActivity mainActivity, ImageView[] bugs_right, ImageView[] bugs_left, int score_value) {
        this.main = mainActivity;
        this.bugs_right = bugs_right;
        this.bugs_left = bugs_left;
        this.score_value = score_value;
    }

    @Override
    protected String doInBackground(String... params) {
        for (int i = 0; i < 5; i++){
            bugs_right[i].setY(bugs_right[i].getY()+10);
            bugs_left[i].setY(bugs_left[i].getY()+10);
            Log.d("asd", "I AM DOING MY ASYNC TASK");
        }
        return resp;
    }


    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation
    }


    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onProgressUpdate(String... text) {
    }
}
