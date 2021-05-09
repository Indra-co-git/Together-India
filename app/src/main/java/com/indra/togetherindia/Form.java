package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Form extends AppCompatActivity {
    Entry entrydata=new Entry();
    JSONObject state_district_json;
    String state_district_str = null;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String selected_state,selected_city;


    final List<String> state_arr = new ArrayList<String>();
    final List<String> district_arr = new ArrayList<String>();
    final List<String> sev_arr = new ArrayList<String>();
    Spinner city_spinner,state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        final String TAG="inside help form -- ";

        final EditText name=findViewById(R.id.name);
        final EditText age=findViewById(R.id.age);
        final EditText mobile=findViewById(R.id.mobile);
        state=findViewById(R.id.state);
        city_spinner = findViewById(R.id.city);
        final Spinner severity=findViewById(R.id.sev);
        final EditText requirement=findViewById(R.id.requirement);
        Button submit_but=findViewById(R.id.submit);
        final TextView heading = findViewById(R.id.heading_cond);
        final TextInputLayout reqq = findViewById(R.id.f1);

        Intent intent = getIntent();
        if(intent.getStringExtra("screen").equals("screen"))
        {
             severity.setVisibility(View.INVISIBLE);
             //requirement.setHint("What can you provide ?");
             heading.setVisibility(View.INVISIBLE);
             //reqq.setVisibility(View.GONE);
        }



        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        selected_state=sp.getString("selected_state","Select State");
        selected_city=sp.getString("selected_city","Select District");

        state_arr.add("Select State");
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
                post_state_selected(state_arr.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });

        state.setSelection(getIndex(state, selected_state));
        post_state_selected(selected_state);

        final firebase_update firebase_update_obj = new firebase_update(entrydata);

        sev_arr.add("ASYMPTOMATIC");
        sev_arr.add("MILD");
        sev_arr.add("SEVIOR");
        ArrayAdapter<String> sevArraryadapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,sev_arr);
        severity.setAdapter(sevArraryadapter);

        firebase_update_obj.uplaod_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged: ------------  "+integer.toString());
                if(integer == 1)
                {
                    Toast.makeText(getApplicationContext(),"Request Submitted Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        submit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date currentTime = Calendar.getInstance().getTime();

                    entrydata.setName(name.getText().toString().trim());
                    entrydata.setAge(age.getText().toString().trim());
                    entrydata.setState(String.valueOf(state.getSelectedItem()).trim());
                    entrydata.setCity(String.valueOf(city_spinner.getSelectedItem()).trim());
                    entrydata.setSeverity(String.valueOf(severity.getSelectedItem()).trim());
                    entrydata.setRequirement(requirement.getText().toString().trim());
                    entrydata.setMobileNo(mobile.getText().toString().trim());
                    entrydata.setDateTime(currentTime.toString().trim());
                    Log.d(TAG, "onClick: "+entrydata);
                    
                    if(entrydata.getName().isEmpty() || entrydata.getAge().isEmpty() || entrydata.getAge().isEmpty() || entrydata.getState().equalsIgnoreCase("select state") || entrydata.getCity().equalsIgnoreCase("select district"))
                    {
                        Toast.makeText(getApplicationContext(),"Field can't be left Blank",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(entrydata.getMobileNo().length()<10)
                    {
                        Toast.makeText(getApplicationContext(),"Mobile no. should be of 10 digits",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(getIntent().getStringExtra("screen").equals("screen"))
                    {
                        firebase_update_obj.setDataOnFirebase("helper");
                    }
                    else {
                        firebase_update_obj.setDataOnFirebase("help");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void post_state_selected(String select_state) {
        district_arr.clear();
        district_arr.add("Select District");
        try {
            JSONArray states=state_district_json.getJSONArray("states");
            for(int i=0;i<states.length();i++)
            {
                if(states.getJSONObject(i).getString("state")==select_state){
                    JSONArray districts=states.getJSONObject(i).getJSONArray("districts");
                    for(int j=0;j<districts.length();j++)
                        district_arr.add(districts.getString(j));
                    break;
                }

            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.spinner_item_back,district_arr);
            //arrayAdapter.setDropDownViewResource(R.layout.spinner_item_back);

            city_spinner.setAdapter(arrayAdapter);
//            Log.d("TAG", "post_state_selected:   city spinner set"+district_arr);

            city_spinner.setSelection(getIndex(city_spinner, selected_city));
        }
        catch ( JSONException e) {
            e.printStackTrace();
            Log.e("TAG", "onCreate: "+e );
        }
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
//            Log.d("TAG", "getIndex: "+spinner.getItemAtPosition(i));
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

}