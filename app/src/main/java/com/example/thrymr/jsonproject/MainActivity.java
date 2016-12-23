package com.example.thrymr.jsonproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;
    // JSON data url
    private static String Jsonurl = null;
    ArrayList<HashMap<String, String>> contactJsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactJsonList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);
        Intent i = getIntent();
        Jsonurl = (String) i.getSerializableExtra("url");
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();

            // request to json data url and getting response
            String jsonString = httpHandler.makeServiceCall(Jsonurl);
            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    //JSONObject jsonObject = new JSONObject(jsonString);
                    // Log.e(TAG, "Response object: " + jsonObject);
                    // Getting JSON Array node
                    JSONArray contacts = new JSONArray(jsonString);
                    System.out.print("contacts array" + contacts);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        //String id = c.getString("id");
                        String fname = c.getString("firstname");
                        String lname = c.getString("lastname");
                        int iage = c.getInt("age");
                        String age = "" + iage;

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("fname", fname);
                        contact.put("lname", lname);
                        contact.put("age", age);

                        // adding contact to contact list
                        contactJsonList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Could not get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Could not get json from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactJsonList,
                    R.layout.list_item, new String[]{"fname", "lname",
                    "age"}, new int[]{R.id.fname,
                    R.id.lname, R.id.age});

            listView.setAdapter(adapter);

        }

    }
}