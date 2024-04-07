package com.example.valorantlineups;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.valorantlineups.DataStorage;

public class BreezePage extends AppCompatActivity {
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter; // Change to your custom adapter class
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String agent = null;    //for referencing the db
    String pageTitle = "Breeze";   /////// 2222222 , then add to android manifest
    DataStorage dataStorage = ValApp.getInstance().local_data;
    favoriteData favStorage = ValApp.getInstance().favoriteData;
    List<String> currentTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeze_page);
        ToolBarHelper.setCustomToolbar(this, pageTitle, true);
        addSpinner();
        // Initialize favorite data by loading from SharedPreferences
        favStorage.loadFavoritesFromSharedPreferences(this);
        // Initialize ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        //prepareListData();
        // Instantiate custom ExpandableListAdapter and adapter
        expandableListAdapter = new YourExpandableListAdapter(this, listDataHeader, expandableListView);
        expandableListView.setAdapter(expandableListAdapter);
        // Set up EXPAND LISTENER for image loading
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            // Get the title of the expanded group item
            String groupHeader = listDataHeader.get(groupPosition);
            /// AFTER expanded update data
            expandableListView.post(() -> loadImagesForGroup(groupPosition));


        });
        // Set up GROUP CLICK LISTENERS
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.collapseGroup(groupPosition);
                Log.e("ExpandAbleList ", "Group" + groupPosition + " COLLAPSED ------- .");
            } else {
                expandableListView.expandGroup(groupPosition);
                Log.e("ExpandAbleList ", "Group" + groupPosition + " EXPANDED +++++++ .");
            }
            return true; // Return true if you want to prevent the default behavior (expanding/collapsing)
        });
        //Child click listener: may need to convert to image listener to full screen pictures
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            // Handle child item click
            return true;
        });
        // Set the FAVORITE BUTTON LISTENER
        YourExpandableListAdapter adapter = new YourExpandableListAdapter(this, listDataHeader,  expandableListView);
        expandableListView.setAdapter(adapter);
        adapter.setOnFavoriteButtonClickListener(groupPosition -> {
            // Handle the favorite button click for the group at groupPosition
            String groupItem = listDataHeader.get(groupPosition);
            adapter.updateFavoriteButtonImage(groupPosition, pageTitle, agent);
        });
    } // end of onCreate


    private void prepareListData() {    // Function to prepare data for the ExpandableListView--- for b4 db call
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // Add header data
        listDataHeader.add("Prepping");
        listDataHeader.add("Organizing Data Structure");
        // Add child data
        List<String> header1 = new ArrayList<>();
        header1.add("Prepping Data");
        List<String> header2 = new ArrayList<>();
        header2.add("Finding Lineups");
        listDataChild.put(listDataHeader.get(0), header1);
        listDataChild.put(listDataHeader.get(1), header2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Function for back button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Handle the back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSpinner() { //function to create and handle dropdown menu
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
                if (selectedAgent == "Select an agent") {
                    agent = null;
                } else {
                    agent = selectedAgent;
                    updateDataForAgent(selectedAgent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });
    }

    private void updateDataForAgent(String agent) { //function to update data after spinner change
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference thisCollection = db.collection("Maps").document(pageTitle).collection(agent);
        // Clear the old stuff
        listDataHeader.clear();
        currentTitles.clear();
        //gather Title
        Log.d("Firebase", "Data Being Updated");
        thisCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Grab Titles for groups
                Log.d("Firebase", "Task is successful");
                int i = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("Title");
                    currentTitles.add(title);
                }
                Log.d("Firebase", "Number of titles: " + currentTitles.size());
                //Grab all Image1's for found titles
                int j = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference image1Reference = (DocumentReference) document.get("Image1");
                    if (image1Reference != null) {
                        String imageUrlReference = image1Reference.getPath(); // Get the path of the reference
                        dataStorage.addItem(pageTitle, agent, currentTitles.get(j), imageUrlReference);
                    }
                    j++;
                }
                Log.e("Firebase", "Done Image 1 : ");
                //Grab all Image2's for found titles
                j = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference image1Reference = (DocumentReference) document.get("Image2");
                    if (image1Reference != null) {
                        String imageUrlReference = image1Reference.getPath(); // Get the path of the reference
                        dataStorage.addItem(pageTitle, agent, currentTitles.get(j), imageUrlReference);
                    }
                    j++;
                }
                Log.e("Firebase", "Done Image 2 : ");
                //Grab all Image3's for found titles
                j = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference image1Reference = (DocumentReference) document.get("Image3");
                    if (image1Reference != null) {
                        String imageUrlReference = image1Reference.getPath(); // Get the path of the reference
                        dataStorage.addItem(pageTitle, agent, currentTitles.get(j), imageUrlReference);
                    }
                    j++;
                }
                Log.e("Firebase", "Done Image 3 : ");
                //Grab all descriptions for found titles
                j = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String thisItem = document.getString("Description");
                    dataStorage.addItem(pageTitle, agent, currentTitles.get(j), thisItem);
                    j++;
                }
                Log.e("Firebase", "Done Description: ");
            } else {    //handle task not being successful
                Log.e("Firebase", "Task could not be completed, error getting documents: ", task.getException());
                }
            // Setup new data for expandable list
            HashMap<String, List<String>> newDataChild = new HashMap<>();
            for (String title: currentTitles) {    // For each item in titles
                // Add the title to listDataHeader
                listDataHeader.add(title);
                List<String> childItems = new ArrayList<>();
                childItems.add(title + "_child");
                // Add the child items to newDataChild
                newDataChild.put(title, childItems);
            }
            List<String> favorites = favStorage.getFavorites(pageTitle, agent, this);
            reorderData(favorites);
            // Create a new instance of YourExpandableListAdapter with the new data to set onto old one
            YourExpandableListAdapter newAdapter = new YourExpandableListAdapter(this, currentTitles, expandableListView,favorites);
            expandableListView.setAdapter(newAdapter);

            //favStorage.loadFavoritesForAgent(agent);
            //add listener for favorite button
            newAdapter.setOnFavoriteButtonClickListener(groupPosition -> {
                String groupItem = listDataHeader.get(groupPosition);
                newAdapter.updateFavoriteButtonImage(groupPosition, pageTitle, agent);
            });
        });
    }

    private void loadImagesForGroup(int groupPosition) { // Function to load images for the selected group using Picasso
        String groupHeader = listDataHeader.get(groupPosition);
        List<List<String>> listOfLists = dataStorage.getItems(pageTitle, agent, groupHeader);
        if (listOfLists != null) { // Ensure at not null
            String imageUrl1 =   listOfLists.get(0).get(0); // Get the URL for the first image
            String imageUrl2 =   listOfLists.get(1).get(0); // Get the URL for the second image
            String imageUrl3 =   listOfLists.get(2).get(0); // Get the URL for the third image
            String desc = listOfLists.get(3).get(0); // Get the description
                // Find the ImageViews and TextView
            ImageView imageView1 = findViewById(R.id.image1);
            ImageView imageView2 = findViewById(R.id.image2);
            ImageView imageView3 = findViewById(R.id.image3);
            TextView descriptionTextView = findViewById(R.id.description);
                // Ensure TextView reference is not null then setting description
            if (descriptionTextView != null) {
                descriptionTextView.setText(desc);
            } else {
                // Handle the case where TextView reference is null
                Log.e("TextView", "TextView reference is null");
            }
            String bucketName = "valorant-lineup-app.appspot.com";
            String pathToFile = "test1.png";
                //String fbUrl1 = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + pathToFile + "?alt=media";
            String fbUrl1 = formatString(imageUrl1);
            String fbUrl2 = formatString(imageUrl2);
            String fbUrl3 = formatString(imageUrl3);
                // Ensure ImageView references are not null then load images
            if (imageView1 != null && imageView2 != null && imageView3 != null) {
                // Load images using Picasso
                Picasso.get().load(fbUrl1).into(imageView1, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Image 1 Loaded Successfully");
                        imageView1.setOnClickListener(v -> openFullscreenDialog(fbUrl1));
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Error loading Image 1: " + e.getMessage());
                    }
                });
                Picasso.get().load(fbUrl2).into(imageView2, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Image 2 Loaded Successfully");
                        imageView2.setOnClickListener(v -> openFullscreenDialog(fbUrl2));
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Error loading Image 2: " + e.getMessage());
                    }
                });
                Picasso.get().load(fbUrl3).into(imageView3, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", "Image 3 Loaded Successfully");
                        imageView3.setOnClickListener(v -> openFullscreenDialog(fbUrl3));
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.e("Picasso", "Error loading Image 3: " + e.getMessage());
                    }
                });
        } else {
            // Handle the case where listOfLists is null or does not have enough elements
            Log.e("Data", "List of lists is null or insufficient elements");
            }
        }
    }

    public String formatString(String input){   //helper function to convert image reference to usable url
        String[] string_parts = input.split(".com");
        if (string_parts.length == 2){
            return "https://firebasestorage.googleapis.com/v0/b/" + string_parts[0] + ".com/o" + string_parts[1] + "?alt=media";
            }
        else{
            Log.e("Error", "Error Formatting the string for" + input);
            return input;
            }
    }


    private void reorderData(List<String> favoriteSections) {
        // Reorder listDataHeader and listDataChild based on favoriteSections
        List<String> reorderedHeader = new ArrayList<>();
        HashMap<String, List<String>> reorderedChild = new HashMap<>();
        List<String> reorderedTitles = new ArrayList<>(); // New list to hold reordered titles

        // Add favorited sections to the top
        for (String section : favoriteSections) {
            if (listDataHeader.contains(section)) {
                reorderedHeader.add(section);
                reorderedChild.put(section, listDataChild.get(section));
                // Add corresponding titles to reorderedTitles
                int index = listDataHeader.indexOf(section);
                if (index >= 0 && index < currentTitles.size()) {
                    reorderedTitles.add(currentTitles.get(index));
                }
            }
        }

        // Add non-favorited sections after favorited sections
        for (String section : listDataHeader) {
            if (!favoriteSections.contains(section)) {
                reorderedHeader.add(section);
                reorderedChild.put(section, listDataChild.get(section));
                // Add corresponding titles to reorderedTitles
                int index = listDataHeader.indexOf(section);
                if (index >= 0 && index < currentTitles.size()) {
                    reorderedTitles.add(currentTitles.get(index));
                }
            }
        }

        // Update listDataHeader and listDataChild with reordered data
        listDataHeader = reorderedHeader;
        listDataChild = reorderedChild;
        // Update currentTitles with reordered titles
        currentTitles = reorderedTitles;
    }



    private void openFullscreenDialog(String imageUrl) {
        // Create a dialog with transparent background and fullscreen appearance
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        // Inflate the dialog layout
        View view = getLayoutInflater().inflate(R.layout.dialog_fullscreen_image, null);
        dialog.setContentView(view);

        // Find the ImageView in the inflated layout
        ImageView fullscreenImageView = view.findViewById(R.id.fullscreenImageView);

        // Load the image into the ImageView using Picasso
        Picasso.get().load(imageUrl).into(fullscreenImageView);

        // Show the dialog
        dialog.show();
    }

}
