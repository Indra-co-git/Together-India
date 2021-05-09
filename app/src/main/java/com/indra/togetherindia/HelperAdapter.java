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


public class HelperAdapter extends ArrayAdapter<Entry> {

    private final Context mContext;
    private final int mResource;

    public HelperAdapter(Context context, int resource, ArrayList<Entry> objects) {
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

    String requirement = getItem(position).getRequirement();
    String state = getItem(position).getState();
    String city = getItem(position).getCity();
    String date = getItem(position).getDateTime();

    LayoutInflater inflater = LayoutInflater.from(mContext);
    convertView = inflater.inflate(mResource,parent,false);
    TextView nametv = convertView.findViewById(R.id.nameo);
    TextView mobiletv = convertView.findViewById(R.id.contacto);
    TextView reqtv = convertView.findViewById(R.id.requiremento);
    TextView placetv = convertView.findViewById(R.id.placeo);
    TextView datetv = convertView.findViewById(R.id.dateo);
    ImageView call = (ImageView) convertView.findViewById(R.id.callo);
    ImageView wa = (ImageView) convertView.findViewById(R.id.wao);

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
                MainActivity.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
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

    nametv.setText(name + " (" + age + ")");
    mobiletv.setText("Contact : "+ mobileNo);
    reqtv.setText(requirement);
    placetv.setText(state + " ( " + city +" ) ");
    datetv.setText(date.substring(0,16));
    return convertView;

    }
}
