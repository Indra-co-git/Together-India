package com.indra.togetherindia;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class firebase_update {
Entry entrydata;
String TAG;

//Integer uplaod_status=-1;
MutableLiveData uplaod_status = new MutableLiveData<>();
MutableLiveData get_data_status = new MutableLiveData<>();
FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference dataRef = database.getReference();

List<Entry> all_required_data = new ArrayList<Entry>();


public firebase_update(Entry entrydata){
    TAG="firebase update class";
    this.entrydata=entrydata;
}

public void setDataOnFirebase(String type) throws JSONException {
    Log.d(TAG, "setDataOnFirebase: ");
    uplaod_status.setValue(0);
    checkAndSetData(entrydata,type);
}

public void getDataFromFirebase(String state,String city,String type){
    Log.d(TAG, "getDataFromFirebase"+state+city);
    if(state=="Select State"||state=="India")
        state="all";
    else if(city=="Select District")
        city="all";
    get_data_status.setValue(0);
    all_required_data.clear();
    if(state=="all"){
        try {
            dataRef.child(type).child("all_data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    get_data_status.setValue(1);
//                    Log.d(TAG, "onDataChange: ------- "+snapshot.toString());
                    if(!snapshot.exists()){
                        get_data_status.setValue(-1);
                        return;
                    }
                    for (DataSnapshot snap : snapshot.getChildren()){
//                        Log.d(TAG, "onDataChange: ------- "+snap.getValue().toString());
                        all_required_data.add(snap.getValue(Entry.class));
                    }
                    get_data_status.setValue(2);
                    sort_list_data();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            get_data_status.setValue(-1);
            Log.e(TAG, "getDataFromFirebase: "+e.getMessage());
        }
    }
    else if(city=="all"){

        try {

            dataRef.child(type).child(state).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    get_data_status.setValue(1);
//                    Log.d(TAG, "onDataChange: ------- "+snapshot.toString());
                    if(!snapshot.exists()){
                        get_data_status.setValue(-1);
                        return;
                    }
                    for (DataSnapshot snap : snapshot.getChildren()){
//                        Log.d(TAG, "onDataChange: ------- "+snap.getValue().toString());
                        for (DataSnapshot snap_city : snap.getChildren()){
                            all_required_data.add(snap_city.getValue(Entry.class));
                        }
                    }
                    get_data_status.setValue(2);
                    sort_list_data();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            get_data_status.setValue(-1);
            Log.e(TAG, "getDataFromFirebase: "+e.getMessage());
        }

    }
    else{

        try {
            dataRef.child(type).child(state).child(city).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    get_data_status.setValue(1);
//                    Log.d(TAG, "onDataChange: ------- "+snapshot.toString());
                    if(!snapshot.exists()){
                        get_data_status.setValue(-1);
                        return;
                    }
                    for (DataSnapshot snap : snapshot.getChildren()){
//                        Log.d(TAG, "onDataChange: ------- "+snap.getValue().toString());
                        all_required_data.add(snap.getValue(Entry.class));
                    }
                    get_data_status.setValue(2);
                    sort_list_data();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            get_data_status.setValue(-1);
            Log.e(TAG, "getDataFromFirebase: "+e.getMessage());
        }

    }
}

private void sort_list_data(){

    Collections.sort(all_required_data, new Comparator<Entry>(){
        public int compare(Entry obj1, Entry obj2) {
            // ## Ascending order

            DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.ENGLISH);
            Date date1 = null,date2=null;
            try {
                date1 = format.parse(obj1.getDateTime());
                date2 = format.parse(obj2.getDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

//            Log.d(TAG, "compare: "+obj1.getDateTime()+date2);
            return date2.compareTo(date1);
        }
    });

}

// if successfully set the data return 1
    private boolean setData(Entry entrydata,String type) throws JSONException {
        Log.d(TAG, "setData: "+type);
        try {
            dataRef.child(type).child(entrydata.getState().toString()).child(entrydata.getCity().toString()).child(entrydata.getMobileNo().toString()).setValue(entrydata);
            dataRef.child(type).child("all_data").child(entrydata.getMobileNo().toString()).setValue(entrydata);
            uplaod_status.setValue(2);
        }
        catch (Exception e){
            uplaod_status.setValue(-2);
        }
    return false;
    }

//    check if mobile number already exist if yes than return 0 request not allowed else return 1
    private void checkAndSetData(final Entry entrydata, final String type) {
    uplaod_status.setValue(0);
    dataRef.child(type).child(entrydata.getState().toString()).child(entrydata.getCity().toString()).child(entrydata.getMobileNo().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue() == null) {
                try {
                    uplaod_status.setValue(1);
                    setData(entrydata,type);
                } catch (JSONException e) {
                    e.printStackTrace();
                    uplaod_status.setValue(-2);
                }
            }
            else
                uplaod_status.setValue(-1);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }


}
