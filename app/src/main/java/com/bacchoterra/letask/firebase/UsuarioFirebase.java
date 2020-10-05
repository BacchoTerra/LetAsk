package com.bacchoterra.letask.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static String getUserIdentifier (){

        FirebaseAuth mAuth = FirebaseConfig.getFBAuth();

        FirebaseAuth auth = FirebaseConfig.getFBAuth();

        String email = auth.getCurrentUser().getEmail();
        String id = Base64Custom.toBase64(email);

        return id;

    }


    public static FirebaseUser getCurrentUser(){

        FirebaseAuth auth = FirebaseConfig.getFBAuth();

        return auth.getCurrentUser();

    }

    public static void updateUserName(final String name, final OnUpdateSuccesListener listener){

       try {

           FirebaseUser user = getCurrentUser();

           UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

           user.updateProfile(upcr).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {

                   if (!task.isSuccessful()){
                       Log.d("TAG: name update", "onComplete: Failure updating name");
                       listener.updateFailure();
                   }else {
                       Log.d("TAG: name update", "onComplete: success updating name");
                       listener.updateSuccess(name);
                   }

               }
           });


       }catch (Exception e){
           e.printStackTrace();
       }


    }

    public interface OnUpdateSuccesListener{

        void updateSuccess(String name);

        void updateFailure();


    }

}
