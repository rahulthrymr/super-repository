package com.example.thrymr.jsonproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by thrymr on 21/12/16.
 */
public class DashboardActivity extends Activity {

    private ProgressDialog progressDialog;
    String[] fname = {"rahul", "vikesh", "suresh"};
    String[] lname = {"kumar", "kumar", "kumar"};
    int[] age = {25, 44, 39};

    private String currntUri="";
    SendDataToServer sendDataToServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

    }


    public void getdatatoserver(View v) {
        if (!currntUri.equalsIgnoreCase("")) {
            Toast.makeText(this, currntUri, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("url", currntUri);
            startActivity(i);

        }

    }

    public void senddatatoserver(View v) {
        //creating JSONObject[] Array to store JSONObjects
        JSONObject[] jsonObject = new JSONObject[3];

        //loop for storing each JSONObject into JSONObject[]
        for (int x = 0; x < fname.length; x++) {
            //creating JSONObject and atoring data into JSONObject by using put(-,-) method
            JSONObject post_dict = new JSONObject();

            try {
                post_dict.put("firstname", fname[x]);
                post_dict.put("lastname", lname[x]);
                post_dict.put("age", age[x]);

                jsonObject[x] = post_dict;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.length > 0) {
            //executing inner class
             new SendDataToServer().execute(Arrays.toString(jsonObject));
        }

    }

    class SendDataToServer extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(DashboardActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://api.myjson.com/bins");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                // json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                //input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();


                JSONObject jsonObject = new JSONObject(JsonResponse);

                currntUri = jsonObject.getString("uri");
//response data
                Log.i("TAG", JsonResponse);
                try {
//send to post execute
                    return JsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }
}
