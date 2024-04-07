package com.example.valorantlineups;

import android.app.Application;
import com.example.valorantlineups.DataStorage;


public class ValApp extends Application {
    private static ValApp instance;
    public DataStorage local_data;
    public favoriteData favoriteData;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        local_data = new DataStorage();
        favoriteData = new favoriteData();
    }

    public static ValApp getInstance() {
        return instance;
    }
}


/*

Picaso images:
// Assuming you have an ImageView named "imageView" in your layout
String imageUrl = "https://example.com/image.jpg"; // URL of the image to load

// Load image using Picasso
Picasso.get().load(imageUrl).into(imageView);


get x from Firebase: (x=titles)
FirebaseFirestore db = FirebaseFirestore.getInstance();
CollectionReference cypherCollection = db.collection("Maps").document("Ascent").collection("Cypher");

cypherCollection.get().addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            String title = document.getString("title");
            // Handle the title
        }
    } else {
        // Handle errors
    }
});


 */













