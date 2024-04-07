package com.example.valorantlineups;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;


import com.example.valorantlineups.R;
import com.example.valorantlineups.ToolBarHelper;
import com.example.valorantlineups.YourExpandableListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BindPage extends AppCompatActivity {
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter; // Change to your custom adapter class
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String agent = null;    //for referencing the db
    String pageTitle = "Bind";   /////// 2222222 , then add to android manifest


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_page);        /////////// 111111
        ToolBarHelper.setCustomToolbar(this, pageTitle, true);
        addSpinner();
        // Initialize ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        // Prepare data for the ExpandableListView
        prepareListData();
        // Instantiate custom ExpandableListAdapter and adapter
        expandableListAdapter = new YourExpandableListAdapter(this, listDataHeader, expandableListView);
        expandableListView.setAdapter(expandableListAdapter);
        //imageView.setImageResource(R.drawable.new_image); can use to set the image, FIND OUT HOW --------------------------
        // Set up item click listeners or any additional configurations as needed
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            // Handle group item click
            if (expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.collapseGroup(groupPosition);
            } else {
                expandableListView.expandGroup(groupPosition);
            }
            return true; // Return true if you want to prevent the default behavior (expanding/collapsing)
        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            // Handle child item click
            return true;
        });

        // Set the listener for favorite button clicks
        YourExpandableListAdapter adapter = new YourExpandableListAdapter(this, listDataHeader, expandableListView);
        expandableListView.setAdapter(adapter);
        adapter.setOnFavoriteButtonClickListener(groupPosition -> {
            // Handle the favorite button click for the group at groupPosition
            String groupItem = listDataHeader.get(groupPosition);
            adapter.updateFavoriteButtonImage(groupPosition, pageTitle, agent);
        });
    }

    // Function to prepare data for the ExpandableListView
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // Add header data
        listDataHeader.add("Preparing Data");
        // Add child data
        List<String> header1 = new ArrayList<>();
        header1.add("Data info will go here");
        listDataChild.put(listDataHeader.get(0), header1);
    }

    private void updateListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        //get data ref
        CollectionReference dataSetCollectionRef = db.collection("maps").document(pageTitle)
                .collection("agents").document(agent)
                .collection("data_sets");
        //get data from ref collection
        // Retrieve all documents within the collection
        dataSetCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Retrieve the fields from each document
                    String body = document.getString("Body");
                    String title = document.getString("Title");
                    String image1 = document.getString("image1");
                    String image2 = document.getString("image2");
                    String image3 = document.getString("image3");
                    boolean isFavorite = document.getBoolean("isFavorite");

                    // CHANGE THIS TO USE THE DATA and maybe add a bucket that supports favoites
                    Log.d(TAG, "Body: " + body);
                    Log.d(TAG, "Title: " + title);
                    Log.d(TAG, "Image1: " + image1);
                    Log.d(TAG, "Image2: " + image2);
                    Log.d(TAG, "Image3: " + image3);
                    Log.d(TAG, "isFavorite: " + isFavorite);
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        // Add header data
        listDataHeader.add("Preparing Data");
        // Add child data
        List<String> header1 = new ArrayList<>();
        header1.add("Data info will go here");
        listDataChild.put(listDataHeader.get(0), header1);
    };

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
                if (selectedAgent == "Select an agent"){
                    agent = null;
                }
                else {
                    agent = selectedAgent;
                    updateListData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });
    }

}


/* Code Example: getting data from FB

FirebaseFirestore db = FirebaseFirestore.getInstance();

// Assuming you know the map ID and agent ID
String mapId = "your_map_id";
String agentId = "your_agent_id";

// Reference to the collection of data sets for the agent on the map
CollectionReference dataSetCollectionRef = db.collection("maps").document(mapId)
        .collection("agents").document(agentId)
        .collection("data_sets");

// Retrieve all documents within the collection
dataSetCollectionRef.get().addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            // Retrieve the fields from each document
            String body = document.getString("Body");
            String title = document.getString("Title");
            String image1 = document.getString("image1");
            String image2 = document.getString("image2");
            String image3 = document.getString("image3");
            boolean isFavorite = document.getBoolean("isFavorite");

            // Use the retrieved data
            Log.d(TAG, "Body: " + body);
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Image1: " + image1);
            Log.d(TAG, "Image2: " + image2);
            Log.d(TAG, "Image3: " + image3);
            Log.d(TAG, "isFavorite: " + isFavorite);
        }
    } else {
        Log.d(TAG, "Error getting documents: ", task.getException());
    }
});


 */