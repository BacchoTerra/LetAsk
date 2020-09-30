package com.bacchoterra.letask.model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public abstract class UsuarioInformation {

    private static Usuario usuario;


    public static Usuario getUsuarioInformation(String email, final OnInformationFetchCompleteListener listener) {

        if (usuario == null) {

            DatabaseReference mRef = FirebaseConfig.getFBDatabase();
            mRef.child(FirebaseConfig.USERS_NOD)
                    .child(Base64Custom.toBase64(email))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){

                                usuario = new Usuario();
                                usuario = snapshot.getValue(Usuario.class);
                                listener.onInformationSuccess(usuario,"From database");
                            }else {
                                listener.onInformationCancelled("User not found in database");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            listener.onInformationCancelled(error.getMessage());
                        }
                    });

        }else {

            listener.onInformationSuccess(usuario,"from class");
        }

        return usuario;


    }


    public interface OnInformationFetchCompleteListener {

        void onInformationSuccess(Usuario information,String text);

        void onInformationCancelled(String error);


    }


}
