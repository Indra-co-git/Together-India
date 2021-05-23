package com.indra.togetherindia;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class vaccine_util {

    StringRequest stringRequest;
    RequestQueue queue2;
    Map<String,Integer> statemapping,districtmapping;
    Map<Integer,ArrayList<String>> distsstatewise;
    List<vaccine_info> all_centre_wise_vaccine_info=new ArrayList<vaccine_info>();
    MutableLiveData set_state_dist_status = new MutableLiveData<>();
    MutableLiveData vaccine_info_status = new MutableLiveData<>();
    String TAG="vaccine_util";
    Integer response_count = 0;

    int temp_total;
    public vaccine_util(Context main_context){
        set_state_dist_status.setValue(0);
        // Instantiate the RequestQueue.
        queue2 = Volley.newRequestQueue(main_context);
        statemapping = new HashMap<>();
        districtmapping = new HashMap<>();
        distsstatewise = new HashMap<>();
        getStates();
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
                            Log.d("getstate - ",  statesarray.length()+statesarray.toString());


                            for (int i=0 ; i< statesarray.length(); i++)
                            {
                                JSONObject jo = statesarray.getJSONObject(i);
                                String state = jo.getString("state_name");
                                int id = jo.getInt("state_id");
                                getdistsformstateid(id);
                                Log.d(TAG, "onResponse: fetched dist for "+id);
                                statemapping.put(state.toLowerCase(),id);
                                states.add(state);

                            }
                        } catch (Exception e) {
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
        queue2.add(stringRequest);
        return states;

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
                                Log.d("get_dists_id ",distarray.length()+String.valueOf(distarray));
                            for (int i=0 ; i< distarray.length(); i++)
                            {
                                JSONObject jo = distarray.getJSONObject(i);
                                String dist = jo.getString("district_name");
                                int id = jo.getInt("district_id");
//                                Log.d(TAG, "onResponse: "+dist+id);
                                districtmapping.put(dist.toLowerCase(),id);
                                dists.add(dist.toLowerCase());

                            }
                            distsstatewise.put(stateid,dists);
                            if(stateid==37){
                                set_state_dist_status.setValue(1);
                                Log.d(TAG, "onResponse: state size"+statemapping.size()+" dist size " + districtmapping.size() );
                            }
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
        queue2.add(stringRequest);
        return dists;

    }

    public void get_brief_info(String state,
                               String dist,
                               String date){

        Log.d(TAG, "get_brief_info: input state, dist, date" +state+dist+date);

        state=state.toLowerCase();
        dist=dist.toLowerCase();
        if(date==null || date.length()==0)
        {
            date=new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        }
        Log.d(TAG, "get_brief_info: date provide "+date);
        Integer state_id = null,dist_id=null;
        if(state.equals("select state")||state.equals("india"))
            state_id=-1;
        else
        {
            Log.d(TAG, "get_brief_info: select id from api for state  "+state);
            state_id=statemapping.get(state);
            if(dist=="Select District")
                dist_id=-1;
            else
                dist_id=districtmapping.get(dist);
        }

        if(state_id==null)
        {
            Log.d(TAG, "get_brief_info: ----------------------     Can not find state id  hence set -1");
            state_id=-1;
        }

        if(dist_id==null)
        {
            Log.d(TAG, "get_brief_info: ----------------------     Can not find dist id  hence set -1");
            dist_id=-1;
        }


        Log.d(TAG, "get_brief_info: state id"+state_id);
        vaccine_info_status.setValue(0);
        response_count=0;
        all_centre_wise_vaccine_info.clear();
        if(state_id==-1){
            int over = 0;
            temp_total = 0;
            ArrayList<Integer> dists = new ArrayList<>(districtmapping.values());
            Integer dists_size=dists.size();

            for(int i = 0 ; i < dists.size() ; i++) {
                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=" + String.valueOf(dists.get(i)) + "&date=" + date;

                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    response_count += 1;

//                                    Log.d(TAG, "onResponse: "+response);
                                    JSONObject all = new JSONObject(response);
                                    JSONArray cetrearray = all.getJSONArray("sessions");

                                    int tot = 0;

                                    Log.d(TAG, "onResponse: cetrearray.length() "+cetrearray.length()+" "+response_count);

                                    for (int i = 0; i < cetrearray.length(); i++) {

                                        Log.d(TAG, "onResponse: "+cetrearray.get(1));

                                    }
                                    temp_total = tot + temp_total;
                                    Log.d("AMA",String.valueOf(temp_total));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    vaccine_info_status.setValue(-1);
                                }
                                if(response_count==dists_size)
                                {
                                    vaccine_info_status.setValue(1);
                                    Log.d(TAG, "onResponse: vaccine_info_status  complete  -- "+all_centre_wise_vaccine_info.size());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "error");
                        vaccine_info_status.setValue(-1);
                    }
                });


                queue2.add(stringRequest);
                over++;
                Log.d(TAG, "get_brief_info: over - "+over+" "+dists.get(i));
            }


            while(over < dists.size() )
            {

            }
            Log.d("AMA",String.valueOf(temp_total));
        }

        else if (dist_id==-1){

            int over = 0;
            temp_total = 0;
            ArrayList<String> dists =  distsstatewise.get(state_id);
            Integer dists_size=dists.size();


            for(int i = 0 ; i < dists.size() ; i++) {
                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=" + String.valueOf(districtmapping.get(dists.get(i))) + "&date=" + date;

                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    response_count += 1;

//                                    Log.d(TAG, "onResponse: "+response);
                                    JSONObject all = new JSONObject(response);
                                    JSONArray cetrearray = all.getJSONArray("sessions");

                                    int tot = 0;

                                    Log.d(TAG, "onResponse: cetrearray.length() "+cetrearray.length()+" "+response_count);

                                    for (int i = 0; i < cetrearray.length(); i++) {


//                                        all_centre_wise_vaccine_info.add(new Gson().fromJson(cetrearray.get(i).toString(), vaccine_info.class));
                                    }
                                    temp_total = tot + temp_total;
                                    Log.d("AMA",String.valueOf(temp_total));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    vaccine_info_status.setValue(-1);
                                }
                                if(response_count==dists_size)
                                {
                                    vaccine_info_status.setValue(1);
                                    Log.d(TAG, "onResponse: vaccine_info_status  complete  -- "+all_centre_wise_vaccine_info.size());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "error");
                        vaccine_info_status.setValue(-1);
                    }
                });


                queue2.add(stringRequest);
                over++;
                Log.d(TAG, "get_brief_info: over - "+over+" "+dists.get(i)+""+districtmapping.get(dists.get(i)));
            }


            while(over < dists.size() )
            {

            }
            Log.d("AMA",String.valueOf(temp_total));

        }
    }

}
