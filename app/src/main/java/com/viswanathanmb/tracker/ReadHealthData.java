package com.viswanathanmb.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ReadHealthData extends ActionBarActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private TextView txt;
    private Calendar startdatecal, enddatecal;
    private TextView startdate, enddate;
    private TextView activeDateDisplay;
    private Calendar activeDate;

    Button startdatebtn, enddatebtn;

    String item;
    String finalstartdate, finalenddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readhdata);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button btn = (Button) findViewById(R.id.button);
        Button btn_uname = (Button) findViewById(R.id.button_user);
        txt = (TextView) findViewById(R.id.textView1);


        startdate = (TextView) findViewById(R.id.textView2);
        startdatebtn = (Button) findViewById(R.id.button2);

        /* get the current date */
        startdatecal = Calendar.getInstance();

        /* add a click listener to the button   */
        startdatebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(startdate, startdatecal);
            }
        });

        /* capture our View elements for the end date function */
        enddate = (TextView) findViewById(R.id.textView3);
        enddatebtn = (Button) findViewById(R.id.button3);

        /* get the current date */
        enddatecal = Calendar.getInstance();

        /* add a click listener to the button   */
        enddatebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDateDialog(enddate, enddatecal);
            }
        });

        updateDisplay(startdate, startdatecal);
        updateDisplay(enddate, enddatecal);


        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("runkeeper");
        categories.add("fitbit");
        categories.add("googlefit");
        categories.add("ihealth");
        categories.add("withings");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(dataAdapter);


        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                //new JSONtask().execute("http://192.168.78.1:8083/authorize/" + item + "?username=" + username, username, finalstartdate,finalenddate);
                new JSONtask().execute("http://10.100.22.90:8083/authorize/" + item + "?username=" + username, username, finalstartdate,finalenddate);
                Log.d("Viswa Username", username);
                Log.d("Viswa Shim", item);
            }
        });


        btn_uname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                //new loadusername().execute("http://192.168.78.1:8082/addusernames", username);
                new loadusername().execute("http://10.100.22.90:8082/addusernames", username);
                Log.d("Viswa Username", username);
            }
        });
    }

    private void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.YEAR)).append("-")
                        .append((date.get(Calendar.MONTH) + 1) < 10 ? ("0"+ (date.get(Calendar.MONTH) + 1)):(date.get(Calendar.MONTH) + 1)).append("-")
                        //.append(date.get(Calendar.MONTH) + 1)
                        .append(date.get(Calendar.DAY_OF_MONTH) < 10 ? ("0"+ (date.get(Calendar.DAY_OF_MONTH))):(date.get(Calendar.DAY_OF_MONTH)))
                        //.append(date.get(Calendar.DAY_OF_MONTH)).append("")
                        );
        finalstartdate = startdate.getText().toString();
        finalenddate = enddate.getText().toString();
    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(0);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, monthOfYear);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(activeDateDisplay, activeDate);
            unregisterDateDisplay();
        }
    };

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case 0:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }


    public class JSONtask extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            BufferedReader reader1=null;

            try
            {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream in  = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                String source_name = "";
                String distance = "";
                String unit = "";

                while((line = reader.readLine())!=null)
                {
                    buffer.append(line);
                }

                String finaljson = buffer.toString();
                Log.d("Viswa Response", finaljson);

                JSONObject parentobject = new JSONObject(finaljson);
                String authorization_url = parentobject.getString("authorizationUrl");
                Log.d("Viswa AuthorizationURL", authorization_url);

                if(authorization_url!= "null")
                {
                    Log.d("Viswa browser","Browser Block");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorization_url));
                    startActivity(browserIntent);
                }

                else
                {
                    Log.d("Viswa Toast","Toast Block");
                    /*Context context = getApplicationContext();
                    CharSequence text = "User Already Authorized!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    */
                }

                String line1 = "";
                //URL url_authorization1 = new URL("http://192.168.78.1:8083/data/"+item+"/activity?username="+ params[1] +"&dateStart="+ params[2] +"&dateEnd="+ params[3]+"&normalize=true");
                URL url_authorization1 = new URL("http://10.100.22.90:8083/data/"+item+"/activity?username="+ params[1] +"&dateStart="+ params[2] +"&dateEnd="+ params[3]+"&normalize=true");
                Log.d("Viswa URL Auth", url_authorization1.toString());
                HttpURLConnection connection3 = (HttpURLConnection) url_authorization1.openConnection();
                connection3.connect();

                InputStream in1  = connection3.getInputStream();

                reader1 = new BufferedReader(new InputStreamReader(in1));
                StringBuffer buffer2 = new StringBuffer();

                while((line1 = reader1.readLine())!=null)
                {
                    Log.d("Line","+++++++++++++++++++++++++++++++++++");
                    buffer2.append(line1);

                }
                Log.d("Viswa Response", "****************************");
                String finaljson1 = buffer2.toString();
                Log.d("Viswa Response", finaljson1);

                JSONObject parentobject1 = new JSONObject(finaljson1);
                String shim = parentobject1.getString("shim");
                JSONArray parentArray = parentobject1.getJSONArray("body");

                for (int i = 0; i < parentArray.length(); i++)
                {
                    JSONObject childJSONObject = parentArray.getJSONObject(i);
                    JSONObject headerObject = childJSONObject.getJSONObject("header");
                    Log.d("ViswaJSON",headerObject.getString("creation_date_time"));
                    JSONObject acquisitionObject = headerObject.getJSONObject("acquisition_provenance");
                    source_name = acquisitionObject.getString("source_name");
                    Log.d("ViswaJSON",source_name);

                    JSONObject bodyObject = childJSONObject.getJSONObject("body");
                    JSONObject distanceObject = bodyObject.getJSONObject("distance");
                    distance = distanceObject.getString("value");
                    Log.d("ViswaJSON",distance);
                    unit = distanceObject.getString("unit");
                    Log.d("ViswaJSON",unit);
                }


                return "\n\n\n\nShim : " + shim + "\nData Provider : " + source_name +
                        "\nDistance Travelled : " + distance + " " + unit;

            }catch(Exception e)
            {
                e.printStackTrace();
            }finally{
                if(connection != null)
                {
                    connection.disconnect();
                }
                try {
                    if(reader != null)
                    {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            txt.setText(result);
        }

    }



    public class loadusername extends AsyncTask<String, String, String>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

           /* progressDialog = new ProgressDialog(ReadHealthData.this);
            progressDialog.setMessage("Inserting Usernames");
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return PostData(params[0],params[1]);
            } catch (IOException e)
            {
                return "Network Error";
            }
            catch(JSONException e1)
            {
                return "Data Invalid";
            }
        }


        private String PostData(String urlpath,String username) throws  IOException, JSONException
        {
            JSONObject datatosend = new JSONObject();
            datatosend.put("name=",username);
            Log.d("After postdata json","After postdata json");

            try {
                    URL url = new URL(urlpath);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    try
                    {
                        urlConnection.connect();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    String requestbody= "name=" + username;
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                    try {
                        outputStreamWriter.write(requestbody);
                        outputStreamWriter.flush();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    // Get the response
                    try {
                        urlConnection.getResponseCode();
                        outputStreamWriter.close();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                return null;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            txt.setText(result);
        }

    }




}