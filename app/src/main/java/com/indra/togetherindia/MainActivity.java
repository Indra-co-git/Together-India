package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    JSONObject state_district_json;
    String state_district_str = null;
    String selected_state,selected_city;
    Map<String,Integer> statemapping,districtmapping;
    Map<Integer,ArrayList<String>> distsstatewise;

    public static PackageManager packageManager;
    ProgressBar progressBar;


    TextView emptyview,av45,av18;
    Spinner state_spinner,district_spinner;
    SearchView searchView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button help,details;
    RequestQueue queue;
    StringRequest stringRequest;

    firebase_update firebase_update_obj;
    int status;

    final List<String> state_arr = new ArrayList<String>();
    final List<String> district_arr = new ArrayList<String>();
    ListView listView;
    int temp_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = 0;
        String TAG="Main activity ";
        listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.pb);
        emptyview = findViewById(R.id.empty_text);
        state_spinner = (Spinner)findViewById(R.id.state_spinner);
        district_spinner = (Spinner)findViewById(R.id.district_spinner);
        searchView=(SearchView)findViewById(R.id.search);
        emptyview.setVisibility(View.INVISIBLE);
        av18 = findViewById(R.id.a18);
        av45 = findViewById(R.id.a45);

        statemapping = new HashMap<>();
        districtmapping = new HashMap<>();
        distsstatewise = new HashMap<>();

        packageManager = getPackageManager();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        selected_state=sp.getString("selected_state","Select State");
        selected_city=sp.getString("selected_city","Select District");


        Log.d("state saved --------- ",selected_state.toString());
        Log.d("city saved ---------- ",selected_city.toString());
        state_arr.add("Select State");
        state_arr.add("India");
        district_arr.add("Select District");

        details = findViewById(R.id.details);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        //String url ="https://cdn-api.co-vin.in/api/v2/admin/location/states";

        getStates();
        //gettotal(-1,-1,"19-05-2021");

        Log.d("here","contrl is here");
        String len = String.valueOf(getStates().size());

        av45.setText(len);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Vaccine.class);
                startActivity(intent);
            }
        });
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


        Log.d(TAG, "onCreate: --- -------"+state_spinner.getSelectedItem().toString());
        select_state_spinner();
        Log.d(TAG, "onCreate: after select_state_spinner()");
        district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                emptyview.setVisibility(View.INVISIBLE);
                post_dist_select(district_arr.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d(TAG, "onCreate: after district_spinner");


        if(selected_state!=null)
        {
            state_spinner.setSelection(getIndex(state_spinner, selected_state));
            post_state_select(selected_state);
            if (selected_city!=null){
                district_spinner.setSelection(getIndex(district_spinner, selected_city));
                post_dist_select(selected_city);
            }

        }


        help = findViewById(R.id.help_request);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,Form.class);
                intent.putExtra("screen","register");
                startActivity(intent);
            }
        });


//        if(selected_city!="Select District" && selected_state!="Select State")
//        {
//            updateList(selected_state,selected_city,"help");
//        }
//        else
//        {
//            updateList("all","all","help");
//        }
//        state_spinner.setSelection(getIndex(state_spinner,selected_state));


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchView.onActionViewExpanded();
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("search text change ", "onQueryTextChange: "+newText);
                List<Entry> after_search_list=firebase_update_obj.all_required_data;
                if (newText.length()>0)
                {
                    search search_obj=new search();
                    after_search_list=search_obj.search_text(newText,after_search_list);
                }

                EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(),R.layout.req_help_list_item, (ArrayList<Entry>) after_search_list);
                listView.setAdapter(entryAdapter);
                return false;
            }
        });


    }

    public void select_state_spinner(){

        Log.d("selected state", "select_state_spinner: ---- "+state_spinner.getSelectedItem().toString());
        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                emptyview.setVisibility(View.INVISIBLE);
                Log.d("selected state", "onItemSelected: ---- "+state_spinner.getSelectedItem().toString());
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
        Log.d("post state", "post_state_select: "+select_state);
        String t=selected_state;
        if(!select_state.equals("Select State") && !select_state.equals(selected_state))
        {
            Log.d("post editor", "post_state_select: "+select_state+" "+selected_state+selected_state.length());
            editor.putString("selected_state",select_state);
            editor.putString("selected_city","Select District");
            editor.commit();

            selected_state=sp.getString("selected_state","Select State");
            selected_city=sp.getString("selected_city","Select District");
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

            selected_city=sp.getString("selected_city","Select District");
        }

        if(district_spinner.getSelectedItem().toString()=="Select District")
            updateList(state_spinner.getSelectedItem().toString(),"all","help");
        else
            Log.d("selected state and dist", "post_dist_select: ");
        updateList(state_spinner.getSelectedItem().toString(),district_spinner.getSelectedItem().toString(),"help");
    }

    public void updateList(String state,String city,String type){

        Entry entry = new Entry("Aman Gupta","24","7737476484","MILD","Need a bed with ventilator in any hospitlal in patna. Condition is very critical please help willing to pay any amount of money needed please save him.","Bihar","Patna","19:17 May 01 2021");
        firebase_update_obj = new firebase_update(entry);
        firebase_update_obj.getDataFromFirebase(state,city,type);
        final ArrayList<Entry> emptylist= new ArrayList<>();
        EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(),R.layout.req_help_list_item, emptylist);
        listView.setAdapter(entryAdapter);
        firebase_update_obj.get_data_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 1)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    emptyview.setVisibility(View.VISIBLE);
                }
                if(integer == 2)
                {
//                    Log.d("Tag 1",firebase_update_obj.all_required_data.toString());
                    EntryAdapter entryAdapter = new EntryAdapter(getApplicationContext(),R.layout.req_help_list_item, (ArrayList<Entry>) firebase_update_obj.all_required_data);
                    listView.setAdapter(entryAdapter);
                    progressBar.setVisibility(View.INVISIBLE);
                    emptyview.setVisibility(View.INVISIBLE);

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

    public ArrayList<String> getStates(){

        ArrayList<String> states = new ArrayList<>();
        states.clear();
        String url = "https://cdn-api.co-vin.in/api/v2/admin/location/states";

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject all = new JSONObject(response);
                            JSONArray statesarray = all.getJSONArray("states");
                            Log.d("AMAN",  statesarray.toString());


                            for (int i=0 ; i< statesarray.length(); i++)
                            {
                                JSONObject jo = statesarray.getJSONObject(i);
                                String state = jo.getString("state_name");
                                int id = jo.getInt("state_id");
                                getdistsformstateid(id);
                                statemapping.put(state,id);
                                states.add(state);

                            }

                            status++;
                            av18.setText(String.valueOf(distsstatewise.size()));
                            av45.setText(String.valueOf(states.size()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error","error");
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return states;

    }

//    public void getrandom()
//    {
//        // Request a string response from the provided URL.
//        stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        textView.setText("Response is: "+ response.substring(0,500));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                textView.setText("That didn't work!");
//            }
//        });
//
//    }
    public int getstatedistslot(String state,String dist)
    {

        return 0;
    }
    public ArrayList<String> getdistsformstateid(int stateid) {

        ArrayList<String> dists = new ArrayList<>();
        dists.clear();
        String url = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/"+String.valueOf(stateid);

        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject all = new JSONObject(response);
                            JSONArray distarray = all.getJSONArray("districts");

                            if(stateid < 5)
                            Log.d("AMAN1",String.valueOf(distarray));
                            for (int i=0 ; i< distarray.length(); i++)
                            {
                                JSONObject jo = distarray.getJSONObject(i);
                                String dist = jo.getString("district_name");
                                int id = jo.getInt("district_id");
                                districtmapping.put(dist,id);
                                dists.add(dist);

                            }
                            distsstatewise.put(stateid,dists);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error","error");
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return dists;

    }

    public int gettotal(int dist_id,int state_id,String date)
    {
        if(dist_id== -1 && state_id ==-1)
        {
            int over = 0;
            temp_total = 0;
            ArrayList<Integer> dists = new ArrayList<>(districtmapping.values());
            for(int i = 0 ; i < dists.size() ; i++) {
                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=" + String.valueOf(dists.get(i)) + "&date=" + date;

                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject all = new JSONObject(response);
                                    JSONArray cetrearray = all.getJSONArray("sessions");


                                    int tot = 0;

                                    for (int i = 0; i < cetrearray.length(); i++) {
                                        JSONObject jo = cetrearray.getJSONObject(i);
                                        String vaccine = jo.getString("vaccine");
                                        int min_age_limit = jo.getInt("min_age_limit");
                                        int available_capacity = jo.getInt("available_capacity");
                                        tot = tot + available_capacity;

                                    }
                                    temp_total = tot + temp_total;
                                    Log.d("AMA",String.valueOf(temp_total));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "error");
                    }
                });
                over++;
            }

            queue.add(stringRequest);


            while(over < dists.size() )
            {

            }
            av45.setText(String.valueOf(temp_total));
            Log.d("AMA",String.valueOf(temp_total));
            return temp_total;
        }
        else if(dist_id == -1)
        {
            ArrayList<String> dists = distsstatewise.get(state_id);
            int over = 0;
            temp_total = 0;
            for(int i = 0 ; i < dists.size() ; i++) {
                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=" + String.valueOf(districtmapping.get(dists.get(i))) + "&date=" + date;

                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject all = new JSONObject(response);
                                    JSONArray cetrearray = all.getJSONArray("sessions");


                                    int tot = 0;

                                    for (int i = 0; i < cetrearray.length(); i++) {
                                        JSONObject jo = cetrearray.getJSONObject(i);
                                        String vaccine = jo.getString("vaccine");
                                        int min_age_limit = jo.getInt("min_age_limit");
                                        int available_capacity = jo.getInt("available_capacity");
                                        tot = tot + available_capacity;

                                    }
                                    temp_total = tot + temp_total;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "error");
                    }
                });
                over++;
            }

            // Add the request to the RequestQueue.
            queue.add(stringRequest);


            while(over < dists.size() )
            {

            }
            return temp_total;



        }
        if(state_id==-1)
        {
            int over = 0;
            temp_total = 0;
                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=" + String.valueOf(dist_id) + "&date=" + date;

                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject all = new JSONObject(response);
                                    JSONArray cetrearray = all.getJSONArray("sessions");


                                    int tot = 0;

                                    for (int i = 0; i < cetrearray.length(); i++) {
                                        JSONObject jo = cetrearray.getJSONObject(i);
                                        String vaccine = jo.getString("vaccine");
                                        int min_age_limit = jo.getInt("min_age_limit");
                                        int available_capacity = jo.getInt("available_capacity");
                                        tot = tot + available_capacity;

                                    }
                                    temp_total = tot + temp_total;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "error");
                    }
                });


            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return temp_total;
        }
        return 0;
    }

    public int getdistdata(int dist_id,String date)
    {

        return 0;

    }
}