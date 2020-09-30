package com.bacchoterra.letask.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.model.Usuario;
import com.bacchoterra.letask.model.UsuarioInformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.CornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileEditActivity extends AppCompatActivity {

    //Layout components
    private Toolbar toolbar;
    private ShapeableImageView imageUserPic;
    private TextInputEditText editName;
    private TextInputEditText editDesc;


    //Model
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        init();

    }

    private void init() {
        initViews();
        customizeShapeableImageView();
        getBundle();
        initToolbar();
        fetchUserInformation();

    }

    private void initViews() {

        toolbar = findViewById(R.id.activity_profile_edit_toolbar);
        imageUserPic = findViewById(R.id.activity_profile_edit_imageUserPic);
        editName = findViewById(R.id.activity_profile_edit_editName);
        editDesc = findViewById(R.id.activity_profile_edit_editDescription);
    }

    private void customizeShapeableImageView(){

        ShapeAppearanceModel.Builder builder = imageUserPic.getShapeAppearanceModel().toBuilder();

        builder.setAllCorners(CornerFamily.ROUNDED,30);

        imageUserPic.setShapeAppearanceModel(builder.build());

    }

    private void getBundle (){

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String email = bundle.getString(MainActivity.KEY_FOR_USER_EMAIL);
            usuario = new Usuario();
            usuario.setEmail(email);
        }


    }

    private void initToolbar() {

        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void fetchUserInformation(){

        UsuarioInformation.getUsuarioInformation(usuario.getEmail(), new UsuarioInformation.OnInformationFetchCompleteListener() {
            @Override
            public void onInformationSuccess(Usuario information, String text) {
                usuario = information;
                handleUserInformation(usuario);
                Toast.makeText(ProfileEditActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInformationCancelled(String error) {
                Toast.makeText(ProfileEditActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handleUserInformation(Usuario mUser){

        editName.setText(mUser.getName());

        if (mUser.getUserPicUrl() != null){
            Glide.with(this).load(mUser.getUserPicUrl()).placeholder(R.drawable.ic_person_24px).into(imageUserPic);
        }else {
            Glide.with(this).load(R.drawable.ic_person_24px).into(imageUserPic);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.simple_save_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}