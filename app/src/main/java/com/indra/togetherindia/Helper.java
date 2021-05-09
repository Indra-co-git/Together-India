package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Helper extends AppCompatActivity {

    JSONObject state_district_json;
    String state_district_str = null;

    ProgressBar progressBar;

    ListView listView;
    Spinner state_spinner,district_spinner;
    final List<String> state_arr = new ArrayList<String>();
    final java.util.List<String> district_arr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        String TAG="Helper activity ";
        listView = findViewById(R.id.helpers_list);

        progressBar = findViewById(R.id.pbh);
        //emptyview = findViewById(R.id.empty_text);
        state_spinner = (Spinner)findViewById(R.id.state_spinnerh);
        district_spinner = (Spinner)findViewById(R.id.district_spinnerh);

        //emptyview.setVisibility(View.INVISIBLE);

        updateList("all","all","helper");

        state_arr.add("Select State");
        state_arr.add("India");
        district_arr.add("Select District");

        // gets states
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
//                Log.d(TAG, "onCreate: "+state_district_json.getJSONArray("states").getJSONObject(i).getString("state"));
                state_arr.add(state_district_json.getJSONArray("states").getJSONObject(i).getString("state"));
            }
//            Log.d("TAG", "onCreate: "+state_district_json.getJSONArray("states").get(0));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate: "+e );
        }



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.spinner_item_back,state_arr);
        state_spinner.setAdapter(arrayAdapter);

        select_state_spinner();
        district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                post_dist_select(district_arr.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void select_state_spinner(){
        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                post_state_select(state_spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });
    }

    private void post_state_select(String select_state){

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

            district_spinner.setAdapter(arrayAdapter);

        }
        catch ( JSONException e) {
            e.printStackTrace();
            Log.e("TAG", "onCreate: "+e );
        }
        if(state_spinner.getSelectedItem().toString()=="Select State"||state_spinner.getSelectedItem().toString()=="India")
            updateList("all","all","helper");
        else
            updateList(state_spinner.getSelectedItem().toString(),"all","helper");


    }

    private void post_dist_select(String select_dist){

        if(district_spinner.getSelectedItem().toString()=="Select District")
            updateList(state_spinner.getSelectedItem().toString(),"all","helper");
        else
            updateList(state_spinner.getSelectedItem().toString(),select_dist,"helper");
    }

    public void updateList(String state,String city,String type){


        Entry entry = new Entry();
        final firebase_update firebase_update_obj = new firebase_update(entry);
        firebase_update_obj.getDataFromFirebase(state,city,type);
        final ArrayList<Entry> emptylist= new ArrayList<>();
        HelperAdapter helperAdapter = new HelperAdapter(Helper.this,R.layout.helper_list_item, emptylist);
        listView.setAdapter(helperAdapter);
        firebase_update_obj.get_data_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d("Inte",integer.toString());
                if(integer == 2)
                {
//                    Log.d("Tag 1",firebase_update_obj.all_required_data.toString());
                    HelperAdapter helperAdapter = new HelperAdapter(getApplicationContext(),R.layout.helper_list_item, (ArrayList<Entry>) firebase_update_obj.all_required_data);
                    listView.setAdapter(helperAdapter);
                    progressBar.setVisibility(View.INVISIBLE);

                }
                if(integer == -1)
                {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });
    }
}