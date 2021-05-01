package com.indra.togetherindia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class EntryAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private int mResource;

    public EntryAdapter(Context context, int resource, ArrayList<Entry> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

     String name = getItem(position).getName();
     String age = getItem(position).getAge();
     String mobileNo = getItem(position).getMobileNo();
     String stage = getItem(position).getSeverity();
     String requirement = getItem(position).getRequirement();
     String state = getItem(position).getState();
     String city = getItem(position).getCity();
     String date = getItem(position).getDateTime();
     
     Entry entry = new Entry(name,age,mobileNo,stage,requirement,state , city ,date);

     LayoutInflater inflater = LayoutInflater.from(mContext);
     convertView = inflater.inflate(mResource,parent,false);

    TextView nametv = (TextView) convertView.findViewById(R.id.namei);
    TextView agetv = (TextView) convertView.findViewById(R.id.agei);
    TextView mobiletv = (TextView) convertView.findViewById(R.id.contacti);
    TextView stagetv = (TextView) convertView.findViewById(R.id.stagei);
    TextView reqtv = (TextView) convertView.findViewById(R.id.requirementi);
    TextView placetv = (TextView) convertView.findViewById(R.id.placei);
    TextView datetv = (TextView) convertView.findViewById(R.id.datei);

    nametv.setText(name);
    agetv.setText(" Age : "+ age);
    mobiletv.setText("Contact : "+ mobileNo);
    stagetv.setText(stage);
    if(stage.equals("MILD"))
    {
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.yello));
    }
    else if(stage.equals("SEVIOR")){
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.RED));
    }
    else {
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
    }
    reqtv.setText(requirement);
    placetv.setText(state + " ( " + city +" ) ");
    datetv.setText(date);

    return convertView;
    }
}
