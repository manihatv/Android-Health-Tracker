package com.viswanathanmb.tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Viswanathan M B on 13/07/2016.
 */
public class OthersHealthData extends ActionBarActivity implements AsyncResponse{

    String myJSON;
    String distance, shim, uname;

    ArrayList<HashMap<String, String>> usernamelist;
    ListView list;
    TextView textView_ohd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.othershealthdata);

        Button btn_username = (Button) findViewById(R.id.button_username);
        list = (ListView) findViewById(R.id.listView);
        usernamelist = new ArrayList<HashMap<String,String>>();
        textView_ohd = (TextView) findViewById(R.id.textView_ohd);

        btn_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new JSONtask().execute("http://192.168.78.1:8082/showusernames");
                new JSONtask().execute("http://10.100.22.90:8082/showusernames");
            }
        });

        Button button_healthdata = (Button) findViewById(R.id.button_healthdata);
        button_healthdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new JSONtask().execute("http://192.168.78.1:8082/showusernames");
                Log.d("ResultData",distance);
                Log.d("ResultData",shim);

                new loadothersdata().execute("http://10.100.22.90:8081/addhealthdata",distance,shim,uname);
            }
        });

    }

    @Override
    public void processFinish(String output) {
        Log.d("******Viswa Output", output);
    }


    public class JSONtask extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection1 = null;
            BufferedReader reader = null;
            BufferedReader reader1=null;
            String finaljson=null;
            try
            {
                Log.d("Inside try","inside try");
                URL url = new URL(params[0]);
                connection1 = (HttpURLConnection) url.openConnection();
                connection1.connect();
                Log.d("after connection","after connection");
                InputStream in  = connection1.getInputStream();

                reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer buffer = new StringBuffer();
                Log.d("after stringbuffer","after stringbuffer");
                String line = "";

                while((line = reader.readLine())!=null)
                {
                    buffer.append(line);
                }

                finaljson = buffer.toString();
                Log.d("Viswa Response", finaljson);

            }catch(Exception e)
            {
                e.printStackTrace();
            }finally{
                if(connection1 != null)
                {
                    connection1.disconnect();
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
            return finaljson;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            myJSON = result;
            showList();
        }

    }

    protected void showList()
    {
        try {
            JSONObject parentobject = new JSONObject(myJSON);
            JSONArray parentArray = parentobject.getJSONArray("message");

            for(int i = 0 ; i < parentArray.length() ; i++) {
                JSONObject object1 = (JSONObject) parentArray.get(i);
                uname = object1.getString("username");
                Log.d("ViswaJSON", uname);

                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put("username",uname);
                usernamelist.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    OthersHealthData.this, usernamelist, R.layout.list_item,
                    new String[]{"username"},
                    new int[]{R.id.username}
            );

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    //String item = ((TextView)view).getText().toString();
                    //String value = (String)parent.getItemAtPosition(position);
                    HashMap<String,String> map =(HashMap<String,String>)list.getItemAtPosition(position);
                    String value = map.get("username");
                    Log.d("Selected Item",value);
                    //new JSONtask().execute("http://10.100.22.90:8083/authorize/" + "runkeeper" + "?username=" + value, value, finalstartdate,finalenddate);
                    new JSONtask1().execute("http://10.100.22.90:8083/authorize/" + "runkeeper" + "?username=" + value, value,"2016-05-31","2016-07-18" );
                    //new JSONtask1().execute("http://192.168.78.1:8083/authorize/" + "runkeeper" + "?username=" + value, value,"2016-05-31","2016-07-15" );
                    //new loadothersdata().execute("http://10.100.22.90:8081/addusernames",distance,shim,uname);

                }
            });
        } catch(JSONException e)
            {
                e.printStackTrace();
            }
    }


    public class JSONtask1 extends AsyncTask<String, String, String>
    {

        public AsyncResponse delegate = null;
        /*public JSONtask1(AsyncResponse delegate){
            this.delegate = delegate;
        }*/

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
                distance = "";
                String unit = "";

                while((line = reader.readLine())!=null)
                {
                    buffer.append(line);
                }

                String finaljson = buffer.toString();
                Log.d("Viswa New Response", finaljson);

                JSONObject parentobject = new JSONObject(finaljson);
                String authorization_url = parentobject.getString("authorizationUrl");
                Log.d("NewAuthorizationURL", authorization_url);

                if(authorization_url!= "null")
                {
                    Log.d("Viswa browser1","Browser Block");
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
                URL url_authorization1 = new URL("http://10.100.22.90:8083/data/"+"runkeeper"+"/activity?username="+ params[1] +"&dateStart="+ params[2] +"&dateEnd="+ params[3]+"&normalize=true");
                //URL url_authorization1 = new URL("http://192.168.78.1:8083/data/"+"runkeeper"+"/activity?username="+ params[1] +"&dateStart="+ params[2] +"&dateEnd="+ params[3]+"&normalize=true");
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
                shim = parentobject1.getString("shim");
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
            textView_ohd.setText(result);
            Log.d("ResultData",result);
            Log.d("ResultData",distance);
            Log.d("ResultData",shim);

            //new loadothersdata().execute("http://10.100.22.90:8081/addusernames",distance,shim,uname);
        }
    }

    public class loadothersdata extends AsyncTask<String, String, String>
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
                return PostData(params[0],params[1],params[2],params[3]);
            } catch (IOException e)
            {
                return "Network Error";
            }
            catch(JSONException e1)
            {
                return "Data Invalid";
            }
        }


        private String PostData(String urlpath,String distance, String shim, String username) throws  IOException, JSONException
        {
            JSONObject datatosend = new JSONObject();
            //datatosend.put("distance=",distance+"shim="+shim+",name=",username);
            datatosend.put("distancetravelled",distance);
            datatosend.put("shimmer",shim);
            datatosend.put("name",username);
            Log.d("After postdata json","After postdata json");

            try {
                URL url = new URL(urlpath);
                HttpURLConnection urlConnectionnew = (HttpURLConnection) url.openConnection();
                urlConnectionnew.setDoOutput(true);
                urlConnectionnew.setRequestMethod("POST");
                urlConnectionnew.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                try
                {
                    Log.d("Viswa Urlpath",urlpath);
                    urlConnectionnew.connect();
                    //Log.d("Response Message",urlConnectionnew.getResponseMessage());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
/*
                String requestbody1= "distancetravelled=" +distance+ ",shimmer="+shim+",name=" + username;
                OutputStreamWriter outputStreamWriter1 = new OutputStreamWriter(urlConnectionnew.getOutputStream());
                try {
                    Log.d("Viswa RequestBody",requestbody1);
                    outputStreamWriter1.write(requestbody1);
                    outputStreamWriter1.flush();
                } catch (Exception e) {

                    e.printStackTrace();
                }
  *              // Get the response
                try {
                    urlConnectionnew.getResponseCode();
                    outputStreamWriter1.close();

                } catch (Exception e) {

                    e.printStackTrace();
                }
*/

                OutputStream outputStreamnew = urlConnectionnew.getOutputStream();
                BufferedWriter bufferedWriternew = new BufferedWriter(new OutputStreamWriter(outputStreamnew));
                bufferedWriternew.write(datatosend.toString());
                Log.d("DataToSend",datatosend.toString());
                bufferedWriternew.flush();

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
            //Log.d("PostDataServerResult",result);
            //txt.setText(result);
        }

    }


}


