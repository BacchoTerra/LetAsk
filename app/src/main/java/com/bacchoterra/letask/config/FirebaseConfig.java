package com.bacchoterra.letask.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {

    //Firebase components
    private static FirebaseAuth mAuth;
    private static DatabaseReference mRef;




    public static FirebaseAuth getFBAuth(){
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    public static DatabaseReference getFBDatabase(){


        if (mRef == null){
            mRef = FirebaseDatabase.getInstance().getReference();
        }

        return mRef;
    }


}
