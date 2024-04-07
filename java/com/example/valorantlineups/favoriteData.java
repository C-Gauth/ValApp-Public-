package com.example.valorantlineups;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class favoriteData {
    private Map<String, Map<String, List<String>>> favoriteMap;
    private final String FAVORITES_KEY = "favorites";

    public favoriteData() {
        favoriteMap = new HashMap<>();
    }

    public void addFavorite(String map, String agent, String title, Context context) {
        loadFavoritesFromSharedPreferences(context);
        Map<String, List<String>> mapData = favoriteMap.computeIfAbsent(map, k -> new HashMap<>());
        List<String> agentData = mapData.computeIfAbsent(agent, k -> new ArrayList<>());
        if (!agentData.contains(title)) {
            agentData.add(title);
            saveFavoritesToSharedPreferences(context);
        }
    }

    public void removeFavorite(String map, String agent, String title, Context context) {
        loadFavoritesFromSharedPreferences(context);
        Map<String, List<String>> mapData = favoriteMap.get(map);
        if (mapData != null) {
            List<String> agentData = mapData.get(agent);
            if (agentData != null) {
                agentData.remove(title);
                if (agentData.isEmpty()) {
                    mapData.remove(agent);
                }
                if (mapData.isEmpty()) {
                    favoriteMap.remove(map);
                }
                saveFavoritesToSharedPreferences(context);
            }
        }
    }

    public List<String> getFavorites(String map, String agent, Context context) {
        if (favoriteMap == null) {
            // Load favorites from SharedPreferences if not already loaded
            loadFavoritesFromSharedPreferences(context);
        }
        // Log the contents of favoriteMap for debugging
        if (favoriteMap != null) {
            Log.d("getFavorites", "favoriteMap contents: " + favoriteMap.toString());
        } else {
            Log.d("getFavorites", "favoriteMap is null");
        }
        // Now, proceed with accessing the favoriteMap
        Map<String, List<String>> agentMap = favoriteMap.get(map);
        if (agentMap != null) {
            return agentMap.getOrDefault(agent, new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(favoriteMap);
    }

    public static favoriteData fromJson(String json) {
        Gson gson = new Gson();
        favoriteData favoriteData = new favoriteData();
        favoriteData.favoriteMap = gson.fromJson(json, new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType());
        return favoriteData;
    }

    public void saveFavoritesToSharedPreferences(Context context) {
        // Convert favoriteMap to JSON string
        Gson gson = new Gson();
        String serializedFavorites = gson.toJson(favoriteMap);

        // Save JSON string to SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FAVORITES_KEY, serializedFavorites);
        editor.apply();
    }

    public void loadFavoritesFromSharedPreferences(Context context) { //loads full favorite map
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String serializedFavorites = sharedPreferences.getString(FAVORITES_KEY, "");
        // Convert JSON string back to favoriteMap
        if (!serializedFavorites.isEmpty()) {
            Gson gson = new Gson();
            favoriteMap = gson.fromJson(serializedFavorites, new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType());
        }
    }

    public void clearFavorites(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // This will clear all the data from SharedPreferences
        editor.apply(); // Apply the changes
        favoriteMap.clear();


    }

}