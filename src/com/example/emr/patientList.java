package com.example.emr;

import java.io.*;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class patientList extends Activity{

	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb=null;
	EditText etSearch;
	ImageButton searchbtn;
	ListView patientlistview;
    JSONObject jObj = null;
    String json = "";
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.patient_list);
	        
	        etSearch = (EditText)findViewById(R.id.search);
	        searchbtn = (ImageButton)findViewById(R.id.searchbtn);
	        search("");
	        searchbtn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	String keyword = etSearch.getText().toString();
	            	etSearch.setText("searching...");
	            	search(keyword);
	            	
	            	etSearch.setText("");
	            }
	
	            });
	 }
	   @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.emr__main, menu);
	        return true;
	    }
	    
	    
	    public void search(String searchKeyword){
	    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        	LinearLayout linearList = (LinearLayout) findViewById(R.id.linearList);
        	linearList.removeAllViews();
	        try{

	            nameValuePairs.add(new BasicNameValuePair("search", searchKeyword));
	            	      	  
	             HttpClient httpclient = new DefaultHttpClient();
	             HttpPost httppost = new HttpPost("http://10.0.2.2/EMR/patientlist.php");
	             httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	             HttpResponse response = httpclient.execute(httppost);
	             HttpEntity entity = response.getEntity();
	             is = entity.getContent();
	             }catch(Exception e){
	                 Log.e("log_tag", "Error in http connection"+e.toString());
	            }

	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            is.close();
	            json = sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }
	 
	        //parse string>>> JSON object
	        try {
	            jObj = new JSONObject(json);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
	        
	        try {
                
                int success = jObj.getInt("success");
 
                if (success == 1) {
                    
                    //patientlist
                	jArray = jObj.getJSONArray("patient");
 
                    
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject patient = jArray.getJSONObject(i);
            	    	LinearLayout linearLayout = new LinearLayout(this);
            	        linearLayout.setOrientation(LinearLayout.VERTICAL);
            	        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
            	            LinearLayout.LayoutParams.FILL_PARENT,
            	            LinearLayout.LayoutParams.FILL_PARENT);
            	    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
            	    	        (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	TextView x = new TextView(this);
                    	TextView y = new TextView(this);
                    	x.setText("Patient ID: " + patient.getString("ID"));
                    	y.setText("Patient Name: " + patient.getString("Name"));
                    	x.setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
                    	y.setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
                    	
            			linearLayout.addView(x, layoutParams);
            			linearLayout.addView(y, layoutParams);

            			linearList.addView(linearLayout, llp);
                    }
                } else {
                	TextView e = new TextView(this);
                	e.setText("No record found.");
                	((LinearLayout)findViewById(R.id.linearList)).addView(e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
	    }
	}