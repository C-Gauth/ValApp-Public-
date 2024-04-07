package com.example.valorantlineups;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    favoriteData favStorage = ValApp.getInstance().favoriteData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToolBarHelper.hideCustomToolbar(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Find your button by its ID
        Button navigateButton = findViewById(R.id.FindLineupsButton);

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setBackgroundColor(ContextCompat.getColor(this, R.color.val_red));

        clearButton.setOnClickListener(v -> {
            showConfirmationDialog(this );
        });

        // Set OnClickListener for the button
        navigateButton.setOnClickListener(v -> {
            // Create an Intent to navigate to the TargetActivity
            Intent intent = new Intent(MainActivity.this, MapScreen.class);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Find Lineups Button");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main Button");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            // Start the new activity
            startActivity(intent);
        });
            /*  ====================ADS
        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
            */
    }

    private void showConfirmationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete your favorites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call the method to delete favorites here
                favStorage.clearFavorites(context);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
