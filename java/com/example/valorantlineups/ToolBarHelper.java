package com.example.valorantlineups;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ToolBarHelper {
    public static void setCustomToolbar(AppCompatActivity activity, String title) {
        setCustomToolbar(activity, title, true);
    }

    public static void setCustomToolbar(AppCompatActivity activity, String title, boolean setupToolbar) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (setupToolbar && actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);  // Set this to true
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_toolbar_layout);
            View customView = actionBar.getCustomView();
            TextView titleTextView = customView.findViewById(R.id.titleTextView);
            titleTextView.setText(title);
        }
    }

    public static void hideCustomToolbar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static void showCustomToolbar(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }
}