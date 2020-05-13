package com.colin.go4lunch.utils;

import androidx.annotation.NonNull;

import com.colin.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class UserHelper {
    public static final String COLLECTION_NAME = "Users";

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // ------------------------------------------ CREATE ------------------------------------------

    public static Task<Void> createUser(String id, String name, String email, String photo) {
        User userToCreate = new User(id, name, email, photo);
        return UserHelper.getUserCollection().document(id).set(userToCreate, SetOptions.merge());
    }

    public static Task<Void> createUser(FirebaseUser user) {
        String photoUrl = (user.getPhotoUrl() == null) ? null : user.getPhotoUrl().toString();
        return createUser(user.getUid(), user.getDisplayName(), user.getEmail(), photoUrl);
    }

    // ---------------------------------------- REFERENCE -----------------------------------------

    public static Task<DocumentSnapshot> getUser(String id) {
        return getUserCollection().document(id).get();
    }

    // ------------------------------------------ UPDATE ------------------------------------------
    public static Task<Void> updateName(String name, String id) {
        return UserHelper.getUserCollection().document(id).update("name", name);
    }

    public static Task<Void> updateEmail(String email, String id) {
        return UserHelper.getUserCollection().document(id).update("email", email);
    }

    public static Task<Void> updateProfilePicture(String profilePicture, String id) {
        return UserHelper.getUserCollection().document(id).update("profilePicture", profilePicture);
    }

    public static Task<Void> updateString(String field, String value, String userId){
        return UserHelper.getUserCollection().document(userId).update(field, value);

    }

    // ------------------------------------------ DELETE ------------------------------------------

    public static Task<Void> deleteUser(String id) {
        return getUserCollection().document(id).delete();
    }
}