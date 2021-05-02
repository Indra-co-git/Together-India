package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Form extends AppCompatActivity {
    Entry entrydata=new Entry();
    JSONObject state_district_json;
    String state_district_str = null;


    final List<String> state_arr = new ArrayList<String>();
    final List<String> district_arr = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        final String TAG="inside help form -- ";

        final EditText name=findViewById(R.id.name);
        final EditText age=findViewById(R.id.age);
        final EditText mobile=findViewById(R.id.mobile);
        final Spinner state=findViewById(R.id.state);
        final EditText city=findViewById(R.id.city);
        final EditText severity=findViewById(R.id.sev);
        final EditText requirement=findViewById(R.id.requirement);
        Button submit_but=findViewById(R.id.submit);

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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.spinner_item_back,state_arr);

        state.setAdapter(arrayAdapter);

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "onItemSelected: "+state_arr.get(position));
                district_arr.clear();
                district_arr.add("Select District");
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.spinner_item_back,district_arr);
                    //arrayAdapter.setDropDownViewResource(R.layout.spinner_item_back);

                    //district_spinner.setAdapter(arrayAdapter);
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

        final firebase_update firebase_update_obj = new firebase_update(entrydata);
        firebase_update_obj.getDataFromFirebase("Jharkhand","garhwa","help");

        firebase_update_obj.uplaod_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged: ------------  "+integer.toString());
            }
        });
        submit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Date currentTime = Calendar.getInstance().getTime();

                    entrydata.setName(name.getText().toString());
                    entrydata.setAge(age.getText().toString());
                    entrydata.setState(String.valueOf(state.getSelectedItem()));
                    entrydata.setCity(city.getText().toString());
                    entrydata.setSeverity(severity.getText().toString());
                    entrydata.setRequirement(requirement.getText().toString());
                    entrydata.setMobileNo(mobile.getText().toString());
                    entrydata.setDateTime(currentTime.toString());
                    Log.d(TAG, "onClick: "+entrydata);


                    firebase_update_obj.setDataOnFirebase("help");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}