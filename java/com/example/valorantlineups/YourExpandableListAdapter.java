package com.example.valorantlineups;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.squareup.picasso.Picasso;

public class YourExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expandableListView;
    public OnFavoriteButtonClickListener favoriteButtonClickListener;
    DataStorage dataStorage = ValApp.getInstance().local_data;
    favoriteData favStorage = ValApp.getInstance().favoriteData;

    private List<String> favoriteSections;

    // default constructor with no favorites
    public YourExpandableListAdapter(Context context, List<String> listDataHeader, ExpandableListView expandableListView) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.expandableListView = expandableListView;
        // Initialize the default child data
        initializeChildData();
    }

    //overloaded constructor for favorites
    public YourExpandableListAdapter(Context context, List<String> listDataHeader, ExpandableListView expandableListView, List<String> favoriteSections) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.expandableListView = expandableListView;
        this.favoriteSections = favoriteSections; // Initialize the list of favorited sections
        // Initialize the default child data
        initializeChildData();
    }

    private void initializeChildData() {
        // Initialize the HashMap for storing child data
        listDataChild = new HashMap<>();

        // Loop through each group and add default child data
        for (String header : listDataHeader) {
            List<String> defaultChildData = new ArrayList<>();
            // Add default child data placeholders
            defaultChildData.add(""); // Add empty strings as placeholders for image URLs and description
            // Add the default child data to the HashMap
            listDataChild.put(header, defaultChildData);
        }
    }

    // Interface for favorite button click listener
    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(int groupPosition);
    }

    // Method to set the listener
    public void setOnFavoriteButtonClickListener(OnFavoriteButtonClickListener listener) {
        this.favoriteButtonClickListener = listener;
    }

    public void updateFavoriteButtonImage(int groupPosition, String map, String agent) { // Update the image resource and favData for the favorite button at groupPosition
        String groupTitle = listDataHeader.get(groupPosition);
        List<String> theseFavs = favStorage.getFavorites(map, agent,  context);// returning array of 0
        boolean isNotFavorite = true;
        for(String strings : theseFavs){
            if (groupTitle.equals(strings)){ // if this group is a favorite
                //swap and remove from list
                isNotFavorite = false;
                favStorage.removeFavorite(map,agent,groupTitle,context);
               favStorage.saveFavoritesToSharedPreferences(context);
            }
        }
        if(isNotFavorite == true) { // if not in fav, add then update
            favStorage.addFavorite(map,agent,groupTitle,context);
            favStorage.saveFavoritesToSharedPreferences(context); // Save favorites to SharedPreferences
        }
        int imageResource = isNotFavorite ? R.drawable.star_full : R.drawable.star_empty; // may be backwards
        View groupView = expandableListView.getChildAt(groupPosition - expandableListView.getFirstVisiblePosition()); ///may be bug with position ==================
        if (groupView != null) {
            ImageButton groupButton = groupView.findViewById(R.id.favButton);
            groupButton.setImageResource(imageResource);
        }
    }


    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (listDataChild != null && listDataHeader.size() > groupPosition) {
            List<String> children = listDataChild.get(listDataHeader.get(groupPosition));
            return children != null ? children.size() : 0;
        } else {
            return 0;
        }
    }
    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        boolean isFavorite = favoriteSections.contains(headerTitle); // Check if the section is favorited
        //boolean isFavorite = Boolean.parseBoolean((String) getChild(groupPosition, 0)); // Parse favorite flag
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item_layout, null);
        }
        LinearLayout groupLayout = (LinearLayout) convertView.findViewById(R.id.groupItemFull);
        TextView lblListHeader = (TextView) groupLayout.findViewById(R.id.groupTitle);
        lblListHeader.setText(headerTitle);
        // Set up the ImageButton based on favorite flag
        ImageButton childButton = convertView.findViewById(R.id.favButton);
        int imageResource = isFavorite ? R.drawable.star_full : R.drawable.star_empty;
        childButton.setImageResource(imageResource);
        // Set the OnClickListener for the favorite button
        childButton.setOnClickListener(v -> {
            if (favoriteButtonClickListener != null) {
                favoriteButtonClickListener.onFavoriteButtonClick(groupPosition);
            }
        });

        return convertView;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item_layout, null);
        }
        // Bind data to the views in your child item layout -- removed with child title --
        //TextView childTitle = convertView.findViewById(R.id.childTitle);
        //childTitle.setText(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
        // Add more bindings ?
        return convertView;
    }


    public View getNewChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent, String map, String agent, String title) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item_layout, null);
        }
        // Get the title of the group item
        String groupHeader = listDataHeader.get(groupPosition);
        // Get the child item data using the group header as the key
        String childItemText = listDataChild.get(groupHeader).get(childPosition);
        // Assuming you have three additional texts for images
        List<List<String>> listOfLists = dataStorage.getItems(map, agent, title);
        String imageUrl1 = listOfLists.get(0).get(0); // Get the URL for the first image
        String imageUrl2 = listOfLists.get(0).get(1); // Get the URL for the second image
        String imageUrl3 = listOfLists.get(0).get(2); // Get the URL for the third image
        // Bind data to the views in your child item layout
        TextView childTextView = convertView.findViewById(R.id.description);
        ImageView imageView1 = convertView.findViewById(R.id.image1);
        ImageView imageView2 = convertView.findViewById(R.id.image2);
        ImageView imageView3 = convertView.findViewById(R.id.image3);
        // Set the text for the TextView
        childTextView.setText(childItemText);
        // Use Picasso to load images from URLs into ImageViews
        Picasso.get().load(imageUrl1).into(imageView1);
        Picasso.get().load(imageUrl2).into(imageView2);
        Picasso.get().load(imageUrl3).into(imageView3);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // Assuming you have a method to update your dataset with new data
    public void updateData(List<String> newDataHeader, HashMap<String,List<String>> newDataChild) {
        this.listDataHeader = newDataHeader;
        this.listDataChild = newDataChild;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    private void loadFavoritesForAgent(String agent) {
        // Load favorites for the specified agent from SharedPreferences
        favStorage.loadFavoritesFromSharedPreferences(context);
    }


}