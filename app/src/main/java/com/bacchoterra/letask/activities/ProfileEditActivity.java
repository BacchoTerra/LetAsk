package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.fragments.ProfileEditBottomSheetDialog;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.firebase.UsuarioFirebase;
import com.bacchoterra.letask.model.Usuario;
import com.bacchoterra.letask.firebase.UsuarioInformation;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener,ProfileEditBottomSheetDialog.OnFabChoiceListener{

    //Layout components
    private Toolbar toolbar;
    private ShapeableImageView imageUserPic;
    private FloatingActionButton fabChoosePic;
    private TextInputEditText editName;
    private TextInputEditText editDesc;


    //Model
    private Usuario currentUsuario;

    //Toolbar menu
    private Menu mMenu;

    //BottomSheet
    ProfileEditBottomSheetDialog profileEditBottomSheetDialog;

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
        fabChoosePic = findViewById(R.id.activity_profile_edit_fabChoosePic);
        editName = findViewById(R.id.activity_profile_edit_editName);
        editDesc = findViewById(R.id.activity_profile_edit_editDescription);

        fabChoosePic.setOnClickListener(this);
    }


    /**
     * Email must come from intent, avoiding creating another FirebaseUser..
     * The email will be used to get user information from database
     *
     * @see ProfileEditActivity#fetchUserInformation();
     */
    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String email = bundle.getString(MainActivity.KEY_FOR_USER_EMAIL);
            currentUsuario = new Usuario();
            currentUsuario.setEmail(email);
        }


    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void customizeShapeableImageView() {

        ShapeAppearanceModel.Builder builder = imageUserPic.getShapeAppearanceModel().toBuilder();

        builder.setAllCorners(CornerFamily.ROUNDED, 30);

        imageUserPic.setShapeAppearanceModel(builder.build());


    }

    private void fetchUserInformation() {

        UsuarioInformation.getUsuarioInformation(currentUsuario.getEmail(), new UsuarioInformation.OnInformationFetchCompleteListener() {
            @Override
            public void onInformationSuccess(Usuario information, String text) {
                currentUsuario = information;
                handleUserInformation(currentUsuario);
                Toast.makeText(ProfileEditActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInformationCancelled(String error) {
                Toast.makeText(ProfileEditActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handleUserInformation(Usuario mUser) {

        editName.setText(mUser.getName());

        if (mUser.getUserDescription() != null) {
            editDesc.setText(mUser.getUserDescription());
        }

        if (mUser.getUserPicUrl() != null) {
            Glide.with(this).load(mUser.getUserPicUrl()).placeholder(R.drawable.ic_person_24px).into(imageUserPic);
        } else {
            Glide.with(this).load(R.drawable.ic_person_24px).into(imageUserPic);
        }


    }

    private void updateUser() {

        final ProgressBar pb = createToolbarProgressBar();
        final MenuItem item = mMenu.findItem(R.id.simple_save_menu_save);
        item.setVisible(false);



        if (MyHelper.netConn(this)) {

            assert editName.getText() != null && editDesc.getText() != null;
            String name = editName.getText().toString();
            String desc = editDesc.getText().toString();

            if (name.length() >= 5 && isNameOnlyLetters(name)) {
                currentUsuario.setName(name);
                currentUsuario.setUserDescription(desc);


                UsuarioInformation.updateUsuarioOnDatabase(currentUsuario, new UsuarioInformation.OnInformationUpdatedCompleteListener() {
                    @Override
                    public void onUpdate(Usuario updatedUsuario) {
                        UsuarioFirebase.updateUserName(updatedUsuario.getName());
                        MainActivity.shouldRefreshUserInfo = true;
                        finish();
                    }

                    @Override
                    public void onErrorUpdating(String error) {
                        Toast.makeText(ProfileEditActivity.this, error, Toast.LENGTH_LONG).show();
                        pb.setVisibility(View.GONE);
                        item.setVisible(true);
                    }
                });


            } else if (name.length() < 5) {

                MyHelper.showSnackbarLong(R.string.invalid_user_name, toolbar);
                pb.setVisibility(View.GONE);
                item.setVisible(true);
            } else {
                MyHelper.showSnackbarLong(R.string.name_can_only_contain_letters, toolbar);
                pb.setVisibility(View.GONE);
                item.setVisible(true);
            }
        } else {
            MyHelper.showSnackbarLong(R.string.no_internet_connection, toolbar);
            pb.setVisibility(View.GONE);
            item.setVisible(true);
        }

    }

    private ProgressBar createToolbarProgressBar() {

        ProgressBar pb = new ProgressBar(this);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT, GravityCompat.END);



        pb.setLayoutParams(params);

        toolbar.addView(pb);

        return pb;

    }


    private boolean isNameOnlyLetters(String name) {

        char[] letters = name.toCharArray();

        for (char c : letters) {
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.simple_save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.simple_save_menu_save:

                updateUser();
                break;

            case android.R.id.home:
                finish();

        }

        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }


    @Override
    public void onClick(View view) {

        if (view == fabChoosePic){

            if (profileEditBottomSheetDialog == null){
                profileEditBottomSheetDialog = new ProfileEditBottomSheetDialog();
            }
            profileEditBottomSheetDialog.show(getSupportFragmentManager(),null);

        }

    }

    @Override
    public void onFabChose(int choice) {

        switch (choice){


            case ProfileEditBottomSheetDialog.FAB_CAMERA:
                Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show();
                break;

            case ProfileEditBottomSheetDialog.FAB_GALLERY:
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
                break;

            case ProfileEditBottomSheetDialog.FAB_REMOVE_PIC:
                Toast.makeText(this, "Remove pic", Toast.LENGTH_SHORT).show();
                break;

        }


    }
}