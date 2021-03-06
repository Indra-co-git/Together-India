package com.indra.togetherindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

import static android.icu.text.Normalizer.YES;

public class Info extends AppCompatActivity {


    TextView about_app_view;
    public String info;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        about_app_view=findViewById(R.id.about_app);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        String about_app="<p><strong>With the motive of &quot;If we stand together we can defeat covid and save live&quot;</strong></p>\n" +
                "<p>Due to this pandemic, many people are in a helpless situation inside/outside hospitals but also 99% of people of India are not sick now and trying to help others and we believe if we stand together we can save lives and defeat this Pandemic</p>\n" +
                "<p>Here we tried to make a platform to connect those to need help and those who want to help by amplifying the voice of the needy.</p>\n" +
                "<p><br></p>\n" +
                "<p><strong>Be a Helper</strong></p>\n" +
                "<p><em>First, ensure your safety and then help other</em></p>\n" +
                "<p><br></p>\n" +
                "<p>Anyone can be a helper with no prerequisites, Simple efforts can become a huge impact on a needy person and can contribute to saving a human life.</p>\n" +
                "<p><br></p>\n" +
                "<p>The helpers can contribute in the following ways:-&nbsp;</p>\n" +
                "<p><br></p>\n" +
                "<p>Sharing the Ones help request on social media so that anyone who can fulfil the requirement and help</p>\n" +
                "<p><br></p>\n" +
                "<p>Raise help request for those who are not aware of this platform&nbsp;</p>\n" +
                "<p><br></p>\n" +
                "<p>If you know any friend or family in the medical field can contact them to know possible providers of require medical support and share contact to the requesters</p>\n" +
                "<p><br></p>\n" +
                "<p>If you have any vehicle that can be used for the needy.</p>\n" +
                "<p><br></p>\n" +
                "<p>And many more and remember your as simple effort as sharing to social media can be a great help and all these efforts counts and can save a life.</p>\n" +
                "<p><br></p>\n" +
                "<p><br></p>\n" +
                "<p><strong>Request Help</strong></p>\n" +
                "<p><br></p>\n" +
                "<p>When you request help your request will be amplified and appear to all the helper in your city/district and we hope the needy get some help from the helpers.</p>\n" +
                "<p><br></p>\n" +
                "<p><br></p>\n" +
                "<p>Please share this with those who are willing to help where they can find all persons who need help at one place and also raise requests for others who need help.</p>";
        Log.d("TAG", "onCreate: "+about_app );

        info = about_app;

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);


        Map<String,Object> defaultMap = new HashMap<>();
        defaultMap.put(info,about_app);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultMap);

        fetchConfig();


    }

    public void fetchConfig() {
        long cacheExpiration = 60; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
//                            String value = mFirebaseRemoteConfig.getString("info");
//                            about_app_view.setText(Html.fromHtml(value));

                            Toast.makeText(Info.this, "Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Info.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        applyRetrievedLengthLimit();
                    }
                });

//        mFirebaseRemoteConfig.fetch(cacheExpiration)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Make the fetched config available
//                        // via FirebaseRemoteConfig get<type> calls, e.g., getLong, getString.
//                        mFirebaseRemoteConfig.fetch();
//                        //mFirebaseRemoteConfig.activateFetched();
//
//                        // Update the EditText length limit with
//                        // the newly retrieved values from Remote Config.
//                        applyRetrievedLengthLimit();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // An error occurred when fetching the config.
//                        //Log.w(TAG, "Error fetching config", e);
//
//                        // Update the EditText length limit with
//                        // the newly retrieved values from Remote Config.
//                        applyRetrievedLengthLimit();
//                    }
//                });
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {

        String value = mFirebaseRemoteConfig.getString("info");
        about_app_view.setText(Html.fromHtml(value));

    }

}