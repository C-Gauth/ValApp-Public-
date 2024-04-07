package com.example.valorantlineups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MapScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        ToolBarHelper.setCustomToolbar(this, "Map Select");

        // buttons here
        setupButtonClick(R.id.ascent_button, AscentPage.class);
        setupButtonClick(R.id.breeze_button, BreezePage.class);
        setupButtonClick(R.id.bind_button, BindPage.class);
        /*

        setupButtonClick(R.id.fracture_button, FracturePage.class);

        setupButtonClick(R.id.haven_button,HavenPage.class);
        setupButtonClick(R.id.icebox_button, IceboxPage.class);
        setupButtonClick(R.id.lotus_button, LotusPage.class);
        setupButtonClick(R.id.pearl_button, PearlPage.class);
        setupButtonClick(R.id.split_button, SplitPage.class);
        setupButtonClick(R.id.sunset_button, SunsetPage.class);
         */
    }

    private void setupButtonClick(int buttonId, final Class<?> destinationClass) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(view -> navigateToPage(destinationClass));
    }

    public void navigateToPage(Class<?> destinationClass) {
        Intent intent = new Intent(this, destinationClass);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();  // Handle the back button press
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}