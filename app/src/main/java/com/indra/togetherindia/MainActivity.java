package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
    String selected_state,selected_city;

    ProgressBar progressBar;


    TextView emptyview;
    Spinner state_spinner,district_spinner;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button help;

    final List<String> state_arr = new ArrayList<String>();
    final List<String> district_arr = new ArrayList<String>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String TAG="Main activity ";
        listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.pb);
        emptyview = findViewById(R.id.empty_text);
        state_spinner = (Spinner)findViewById(R.id.state_spinner);
        district_spinner = (Spinner)findViewById(R.id.district_spinner);

        emptyview.setVisibility(View.INVISIBLE);


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        selected_state=sp.getString("selected_state","Select State");
        selected_city=sp.getString("selected_city","Select District");


        Log.d("state saved",selected_state.toString());
        Log.d("city saved",selected_city.toString());
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


//        if(selected_state!=null)
//        {
//            state_spinner.setSelection(getIndex(state_spinner, selected_state));
//            post_state_select(selected_state);
//
//        }


        help = findViewById(R.id.help_request);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,Form.class);
                intent.putExtra("screen","register");
                startActivity(intent);
            }
        });


        if(selected_city!="Select District" && selected_state!="Select State")
        {
            updateList(selected_state,selected_city,"help");
        }
        else
        {
            updateList("all","all","help");
        }
        state_spinner.setSelection(getIndex(state_spinner,selected_state));
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
        if(select_state!="Select State")
        {
            editor.putString("selected_state",select_state);
            editor.commit();

        }
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
            district_spinner.setSelection(getIndex(district_spinner,selected_city));

        }
        catch ( JSONException e) {
            e.printStackTrace();
            Log.e("TAG", "onCreate: "+e );
        }
        if(state_spinner.getSelectedItem().toString()=="Select State"||state_spinner.getSelectedItem().toString()=="India")
            updateList("all","all","help");
        else
            updateList(state_spinner.getSelectedItem().toString(),"all","help");


    }

    private void post_dist_select(String select_dist){
        if(select_dist!="Select District")
        {
            editor.putString("selected_city",select_dist);
            Log.d("TAG", "post_dist_select: --------- "+select_dist);
            editor.commit();
        }

        if(district_spinner.getSelectedItem().toString()=="Select District")
            updateList(state_spinner.getSelectedItem().toString(),"all","help");
        else
            updateList(state_spinner.getSelectedItem().toString(),district_spinner.getSelectedItem().toString(),"help");
    }

    public void updateList(String state,String city,String type){


        Entry entry = new Entry("Aman Gupta","24","7737476484","MILD","Need a bed with ventilator in any hospitlal in patna. Condition is very critical please help willing to pay any amount of money needed please save him.","Bihar","Patna","19:17 May 01 2021");
        final firebase_update firebase_update_obj = new firebase_update(entry);
        firebase_update_obj.getDataFromFirebase(state,city,type);
        final ArrayList<Entry> emptylist= new ArrayList<>();
        EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(),R.layout.req_help_list_item, emptylist);
        listView.setAdapter(entryAdapter);
        firebase_update_obj.get_data_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 2)
                {
//                    Log.d("Tag 1",firebase_update_obj.all_required_data.toString());
                    EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(),R.layout.req_help_list_item, (ArrayList<Entry>) firebase_update_obj.all_required_data);
                    listView.setAdapter(entryAdapter);
                    progressBar.setVisibility(View.INVISIBLE);
                    if(firebase_update_obj.all_required_data.size()==0)
                    {
                     emptyview.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.helper:
                Intent intent =new Intent(MainActivity.this,Helper.class);
                startActivity(intent);
                return true;
            case R.id.info:
                Intent intent1 =new Intent(MainActivity.this,Info.class);
                startActivity(intent1);
                return true;
            case R.id.register:
                Intent intent2 =new Intent(MainActivity.this,Form.class);
                intent2.putExtra("screen","screen");
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}