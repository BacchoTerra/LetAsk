package com.bacchoterra.letask.firebase;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bacchoterra.letask.activities.MainActivity;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.bacchoterra.letask.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class UsuarioInformation {

    public static Usuario usuario;


    public static void getUsuarioInformation(String email, final OnInformationFetchCompleteListener listener) {

        if (usuario == null) {

            DatabaseReference mRef = FirebaseConfig.getFBDatabase();
            mRef.child(FirebaseConfig.USERS_NOD)
                    .child(Base64Custom.toBase64(email))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                usuario = new Usuario();
                                usuario = snapshot.getValue(Usuario.class);
                                listener.onInformationSuccess(usuario, "From database");
                            } else {
                                listener.onInformationCancelled("User not found in database");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            listener.onInformationCancelled(error.getMessage());
                        }
                    });

        } else {

            listener.onInformationSuccess(usuario, "from class");
        }

    }

    //TODO: Melhorar este metodo para verificar se houve alteraçao de valores
    public static void updateUsuarioOnDatabase(final Usuario updatedUsuario, final OnInformationUpdatedCompleteListener listener) {


        DatabaseReference mRef = FirebaseConfig.getFBDatabase();

        Map<String, Object> map = new HashMap<>();

        map.put("name", updatedUsuario.getName());
        map.put("userDescription", updatedUsuario.getUserDescription());
        map.put("userPicUrl", updatedUsuario.getUserPicUrl());


        mRef.child(FirebaseConfig.USERS_NOD)
                .child(Base64Custom.toBase64(updatedUsuario.getEmail()))
                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    listener.onUpdate(updatedUsuario);
                    usuario = updatedUsuario;


                } else {

                    listener.onErrorUpdating(task.getException().getMessage());

                }
            }
        });


    }

    public static void updateUserProfilePic(Bitmap bitmap, final OnPictureUpdateListener listener) {

        StorageReference mStorage = FirebaseConfig.getFBStorage();

        if (bitmap != null) {


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            final StorageReference userStorage = mStorage.child("images")
                    .child("profile")
                    .child(Base64Custom.toBase64(usuario.getEmail()) + ".jpeg");

            UploadTask uTask = userStorage.putBytes(imageData);

            uTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Task<Uri> uri = task.getResult().getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        final Uri url = uri.getResult();
                        UsuarioFirebase.updateUserProfilePic(url, new UsuarioFirebase.OnProfilePicUpdateListener() {
                            @Override
                            public void onUpdateSuccess() {
                                DatabaseReference mRef = FirebaseConfig.getFBDatabase();
                                mRef.child(FirebaseConfig.USERS_NOD)
                                        .child(Base64Custom.toBase64(usuario.getEmail()))
                                        .child(FirebaseConfig.PIC_NOD).setValue(url.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            usuario.setUserPicUrl(url.toString());
                                            listener.onUpdateSuccess();
                                        } else {
                                            userStorage.delete();
                                            listener.onUpdateFailure();

                                        }
                                    }
                                });

                            }

                            @Override
                            public void onUpdateFailure() {

                            }
                        });


                    } else {
                        listener.onUpdateFailure();
                    }
                }
            });

        } else {
            final StorageReference userStorage = mStorage.child("images")
                    .child("profile")
                    .child(Base64Custom.toBase64(usuario.getEmail()) + ".jpeg");

            userStorage.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    UsuarioFirebase.updateUserProfilePic(null, new UsuarioFirebase.OnProfilePicUpdateListener() {
                        @Override
                        public void onUpdateSuccess() {
                            DatabaseReference mRef = FirebaseConfig.getFBDatabase();
                            mRef.child(FirebaseConfig.USERS_NOD)
                                    .child(Base64Custom.toBase64(usuario.getEmail()))
                                    .child(FirebaseConfig.PIC_NOD).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        listener.onUpdateSuccess();
                                        usuario.setUserPicUrl(null);
                                    }else {
                                        listener.onUpdateFailure();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onUpdateFailure() {
                            listener.onUpdateFailure();

                        }
                    });

                }
            });
        }


    }


    public static void removeInstance() {

        usuario = null;

    }


    public interface OnInformationFetchCompleteListener {

        void onInformationSuccess(Usuario information, String text);

        void onInformationCancelled(String error);


    }

    public interface OnInformationUpdatedCompleteListener {

        void onUpdate(Usuario updatedUsurario);

        void onErrorUpdating(String error);

    }

    public interface OnPictureUpdateListener {


        void onUpdateSuccess();

        void onUpdateFailure();

    }


}
