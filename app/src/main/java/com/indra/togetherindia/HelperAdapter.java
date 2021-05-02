package com.indra.togetherindia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class HelperAdapter extends ArrayAdapter<Entry> {

    private Context mContext;
    private int mResource;

    public HelperAdapter(Context context, int resource, ArrayList<Entry> objects) {
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

     String requirement = getItem(position).getRequirement();
     String state = getItem(position).getState();
     String city = getItem(position).getCity();
     String date = getItem(position).getDateTime();

     LayoutInflater inflater = LayoutInflater.from(mContext);
     convertView = inflater.inflate(mResource,parent,false);

    TextView nametv = (TextView) convertView.findViewById(R.id.nameo);

    TextView mobiletv = (TextView) convertView.findViewById(R.id.contacto);

    TextView reqtv = (TextView) convertView.findViewById(R.id.requiremento);
    TextView placetv = (TextView) convertView.findViewById(R.id.placeo);
    TextView datetv = (TextView) convertView.findViewById(R.id.dateo);

    nametv.setText(name + " (" + age + ")");
    mobiletv.setText("Contact : "+ mobileNo);
    reqtv.setText(requirement);
    placetv.setText(state + " ( " + city +" ) ");
    datetv.setText(date.substring(0,16));

    return convertView;
    }
}
