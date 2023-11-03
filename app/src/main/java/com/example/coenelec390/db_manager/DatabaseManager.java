package com.example.coenelec390.db_manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private DatabaseReference mDatabase;

    // Constructor
    public DatabaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Adds a component to the database.
     * @param type The type of component (active/passive).
     * @param category The category of the component (resistors, transistors, etc.).
     * @param model The model of the component.
     * @param component The component data.
     */
    public void addComponent(String type, String category, String model, Component component) {
        mDatabase.child("components").child(type).child(category).child(model).setValue(component);
    }
    public void deleteComponent(String type, String category, String model) {
        mDatabase.child("components").child(type).child(category).child(model).removeValue();
        //if you want to remove a model from the database
    }
    public void updateComponentFields(String type, String category, String model, Map<String, Object> updates) {
        mDatabase.child("components").child(type).child(category).child(model).updateChildren(updates);
        //the user only inputs Map<String, Object> updates, the rest stays the same
    }
    public interface DataCallback {
        void onDataReceived(List<String> data);
        void onError(String error);
    }


    //this isnt 100% stable fetchCategories
    /*public void fetchCategories(String type, DataCallback callback) {
        DatabaseReference ref = mDatabase.child("components").child(type);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    categories.add(categorySnapshot.getKey());
                }
                callback.onDataReceived(categories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    public void fetchModels(String type, String category, DataCallback callback) {
        DatabaseReference ref = mDatabase.child("components").child(type).child(category);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> models = new ArrayList<>();
                for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                    models.add(modelSnapshot.getKey());
                }
                callback.onDataReceived(models);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    public void fetchModelDetails(String type, String category, String model, ValueEventListener listener) {
        DatabaseReference ref = mDatabase.child("components").child(type).child(category).child(model);
        ref.addListenerForSingleValueEvent(listener);
    }*/
}
