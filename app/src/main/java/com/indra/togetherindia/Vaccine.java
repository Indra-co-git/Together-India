package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Vaccine extends AppCompatActivity {


    public static final String TAG = "MyTag";

    TextView textView;
    StringRequest stringRequest;
    RequestQueue queue;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine);

        textView = findViewById(R.id.text);
        button = findViewById(R.id.button);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url ="https://cdn-api.co-vin.in/api/v2/admin/location/states";
        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG);
                        Log.d(TAG,response.toString());
                        textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_LONG);
                Log.d(TAG,error.toString());
                textView.setText("That didn't work!");

            }
        });

        // Set the tag on the request.
        stringRequest.setTag(TAG);



        // Add the request to the RequestQueue.
        queue.add(stringRequest);




    }
}