package com.colin.go4lunch.utils;

import com.colin.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class UserHelper {
    private static final String COLLECTION_NAME = "Users";

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --------------------
    // CREATE
    // --------------------

    public static Task<Void> createUser(String id, String name, String email, String photo) {
        User userToCreate = new User(id, name, email, photo);
        return UserHelper.getUserCollection().document(id).set(userToCreate, SetOptions.merge());
    }

    public static Task<Void> createUser(FirebaseUser user) {
        String photoUrl = (user.getPhotoUrl() == null) ? null : user.getPhotoUrl().toString();
        return createUser(user.getUid(), user.getDisplayName(), user.getEmail(), photoUrl);
    }

    // -------------------
    // REFERENCE
    // -------------------

    public static Task<DocumentSnapshot> getUser(String id) {
        return getUserCollection().document(id).get();
    }

    public static Query getUsersInterestedByPlaceQuery(String placeId) {
        return UserHelper.getUserCollection().whereEqualTo("selectedPlaceId", placeId);
    }

    public static Task<QuerySnapshot> getUsersInterestedByPlace(String placeId) {
        return UserHelper.getUserCollection().whereEqualTo("selectedPlaceId", placeId).get();
    }

    // -----------------
    // UPDATE
    // -----------------

    public static Task<Void> updateString(String field, String value, String userId){
        return UserHelper.getUserCollection().document(userId).update(field, value);
    }

    public static Task<Void> addLikedPlace(String placeId, String userId) {
        return getUserCollection().document(userId).update("likedPlaces", FieldValue.arrayUnion(placeId));
    }

    public static Task<Void> deleteLikedPlace(String placeId, String userId) {
        return getUserCollection().document(userId).update("likedPlaces", FieldValue.arrayRemove(placeId));
    }
}