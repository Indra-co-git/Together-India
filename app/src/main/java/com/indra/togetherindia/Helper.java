package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class Helper extends AppCompatActivity {

    ListView listView;
    SearchView searchView;

    List<Entry> helperlist=new ArrayList<Entry>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);


        listView = findViewById(R.id.helpers_list);
        searchView=findViewById(R.id.searchView);

        Entry entry = new Entry();
        final firebase_update firebase_update_obj = new firebase_update(entry);
        firebase_update_obj.getDataFromFirebase("all","all","helper");

        firebase_update_obj.get_data_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 2)
                {
                    helperlist=firebase_update_obj.all_required_data;
                    HelperAdapter helperAdapter = new HelperAdapter(getApplicationContext(),R.layout.helper_list_item, (ArrayList<Entry>) firebase_update_obj.all_required_data);
                    listView.setAdapter(helperAdapter);
                }
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
                List<Entry> after_search_list=helperlist;
                if (newText.length()>0)
                {
                    search search_obj=new search();
                    after_search_list=search_obj.search_text(newText,after_search_list);
                }


                HelperAdapter helperAdapter = new HelperAdapter(getApplicationContext(),R.layout.helper_list_item, (ArrayList<Entry>) after_search_list);
                listView.setAdapter(helperAdapter);
                return false;
            }
        });

    }
}