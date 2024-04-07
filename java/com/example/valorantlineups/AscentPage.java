package com.example.valorantlineups;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class AscentPage extends AppCompatActivity {

    private dataBar collapsibleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ascent_page);

        // Set up custom toolbar with a title
        String pageTitle = "Ascent";
        ToolBarHelper.setCustomToolbar(this, pageTitle, true);

        addSpinner();

        // Initialize collapsible menu
        LinearLayout yourCollapsibleContent = findViewById(R.id.collapsibleContent);
        collapsibleMenu = new dataBar(yourCollapsibleContent);
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

    private void addSpinner() {
        // Get a reference to the Spinner
        Spinner spinner = findViewById(R.id.dropdown_menu);
        // Create an ArrayAdapter using a simple array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.dropdown_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                String selectedAgent = parentView.getItemAtPosition(position).toString();
                updateDataForAgent(selectedAgent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });
    }

    private void updateDataForAgent(String agent) {
        switch (agent) {
            case "Agent 1":
                // Update data for Agent 1
                // Example: String[] agent1Data = getResources().getStringArray(R.array.agent1_data);
                break;
            case "Agent 2":
                // Update data for Agent 2
                // Example: String[] agent2Data = getResources().getStringArray(R.array.agent2_data);
                break;
        }
    }

    // Called when the toggle button for collapsible menu is clicked
    public void toggleCollapsibleContent(View view) {
        collapsibleMenu.toggleCollapsibleContent();
    } //comes here after clicked
}