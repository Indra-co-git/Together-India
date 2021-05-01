package com.indra.togetherindia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;

public class Form extends AppCompatActivity {
    Entry entrydata=new Entry();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        final String TAG="inside help form -- ";

        final EditText name=findViewById(R.id.name);
        final EditText age=findViewById(R.id.age);
        final EditText mobile=findViewById(R.id.mobile);
        final EditText state=findViewById(R.id.state);
        final EditText city=findViewById(R.id.city);
        final EditText severity=findViewById(R.id.sev);
        final EditText requirement=findViewById(R.id.requirement);
        Button submit_but=findViewById(R.id.submit);


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
                    entrydata.setState(state.getText().toString());
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