package com.indra.togetherindia;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

     final String name = getItem(position).getName();
     String age = getItem(position).getAge();
     final String mobileNo = getItem(position).getMobileNo();
     String stage = getItem(position).getSeverity();
     String requirement = getItem(position).getRequirement();
     String state = getItem(position).getState();
     String city = getItem(position).getCity();
     String date = getItem(position).getDateTime();
     
     Entry entry = new Entry(name,age,mobileNo,stage,requirement,state , city ,date);

     LayoutInflater inflater = LayoutInflater.from(mContext);
     convertView = inflater.inflate(mResource,parent,false);

    TextView nametv = (TextView) convertView.findViewById(R.id.namei);
    TextView stagetv = (TextView) convertView.findViewById(R.id.stagei);
    TextView reqtv = (TextView) convertView.findViewById(R.id.requirementi);
    TextView placetv = (TextView) convertView.findViewById(R.id.placei);
    TextView datetv = (TextView) convertView.findViewById(R.id.datei);
    ImageView call = (ImageView) convertView.findViewById(R.id.call);
    ImageView wa = (ImageView) convertView.findViewById(R.id.wa);


    call.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String str="tel:"+ mobileNo;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(str));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }
    });
    wa.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String msg = "Hi " + name + " ,";


            boolean app_present;
            try{
                MainActivity.packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
                app_present = true;
            }catch (PackageManager.NameNotFoundException e)
            {
                app_present = false;
            }
            Log.d("TAG WA", app_present + "");

            if(app_present)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + mobileNo +"&text=" + msg));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
            else {
                Toast.makeText(getContext(),"App is not installed in this phone ", Toast.LENGTH_SHORT);
            }


        }
    });
    nametv.setText(name + " ( " + age + " ) ");
    stagetv.setText(stage);
    if(stage.equals("MILD"))
    {
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.yello));
    }
    else if(stage.equals("SEVERE")){
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.RED));
    }
    else {
        stagetv.setTextColor(ContextCompat.getColor(getContext(),R.color.green));
    }
    reqtv.setText(requirement);
    placetv.setText(state + " ( " + city +" ) ");
    datetv.setText(date.substring(0,16));
    return convertView;

    }
}
