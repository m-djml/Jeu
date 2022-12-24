package com.example.jeu;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class BugMove extends AsyncTask<String, String, String> {

    private String resp;
    private ProgressDialog progressDialog;
    private MainActivity main;

    public BugMove(MainActivity mainActivity) {
        this.main = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        publishProgress("Sleeping...");
        try {
            int time = Integer.parseInt(params[0])*1000;

            Thread.sleep(time);
            resp = "Slept for " + params[0] + " seconds";
        } catch (InterruptedException e) {
            e.printStackTrace();
            resp = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            resp = e.getMessage();
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
