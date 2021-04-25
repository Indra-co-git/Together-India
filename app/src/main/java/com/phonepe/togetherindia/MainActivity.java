package com.phonepe.togetherindia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    JSONObject state_district_json;
    String state_district_str = null;


    Spinner state_spinner,district_spinner;

    final List<String> state_arr = new ArrayList<String>();
    final List<String> district_arr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String TAG="Main activity ";


        state_arr.add("Select State");
        district_arr.add("select district");

        Log.d("TAG", "onCreate: created");
        try {
            InputStream is = getApplicationContext().getAssets().open("state_district.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            state_district_str = new String(buffer, "UTF-8");
            state_district_json= new JSONObject(state_district_str);
            for(int i=0;i<state_district_json.getJSONArray("states").length();i++)
            {
                Log.d(TAG, "onCreate: "+state_district_json.getJSONArray("states").getJSONObject(i).getString("state"));
                state_arr.add(state_district_json.getJSONArray("states").getJSONObject(i).getString("state"));
            }
            Log.d("TAG", "onCreate: "+state_district_json.getJSONArray("states").get(0));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate: "+e );
        }

        state_spinner = (Spinner)findViewById(R.id.state_spinner);

         district_spinner = (Spinner)findViewById(R.id.district_spinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.test,state_arr);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_back);

        state_spinner.setAdapter(arrayAdapter);

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "onItemSelected: "+state_arr.get(position));
                try {
                    JSONArray states=state_district_json.getJSONArray("states");
                    for(int i=0;i<states.length();i++)
                    {
                        if(states.getJSONObject(i).getString("state")==state_arr.get(position)){
                            JSONArray districts=states.getJSONObject(i).getJSONArray("districts");
                            for(int j=0;j<districts.length();j++)
                                district_arr.add(districts.getString(j));
                            break;
                        }

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.test,district_arr);
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_item_back);

                    district_spinner.setAdapter(arrayAdapter);
                }
                catch ( JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", "onCreate: "+e );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}