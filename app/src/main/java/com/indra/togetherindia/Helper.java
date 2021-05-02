package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class Helper extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);


        listView = findViewById(R.id.helpers_list);

        Entry entry = new Entry();
        final firebase_update firebase_update_obj = new firebase_update(entry);
        firebase_update_obj.getDataFromFirebase("all","all","helper");

        firebase_update_obj.get_data_status.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == 2)
                {
                    HelperAdapter helperAdapter = new HelperAdapter(getApplicationContext(),R.layout.helper_list_item, (ArrayList<Entry>) firebase_update_obj.all_required_data);
                    listView.setAdapter(helperAdapter);
                }
            }
        });

    }
}