package com.bacchoterra.letask.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    public static final String USERS_NOD = "Users";
    public static final String COUNTRY_NOD = "country";
    public static final String PIC_NOD = "userPicUrl";

    //Firebase components
    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabaseRef;
    private static StorageReference mStorageRef;




    public static FirebaseAuth getFBAuth(){
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    public static DatabaseReference getFBDatabase(){


        if (mDatabaseRef == null){
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        }

        return mDatabaseRef;
    }

    public static StorageReference getFBStorage(){

        if (mStorageRef == null){
            mStorageRef = FirebaseStorage.getInstance().getReference();
        }

        return mStorageRef;

    }


}
