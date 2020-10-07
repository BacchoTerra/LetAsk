package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener,ProfileEditBottomSheetDialog.OnFabChoiceListener,EasyPermissions.PermissionCallbacks{

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
    private int clickLock = 0; //Lock the menu click before information is fetched;

    //BottomSheet
    private ProfileEditBottomSheetDialog profileEditBottomSheetDialog;

    //Extras
    private Snackbar loadingSnackBar;

    //Permissions and intents
    private static final int CAMERA_PERMISSION = 100;
    private static final int CAMERA_SELECTION = 101;

    private static final int STORAGE_PERMISSION = 200;
    private static final int GALLERY_SELECTION = 201;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void customizeShapeableImageView() {

        ShapeAppearanceModel.Builder builder = imageUserPic.getShapeAppearanceModel().toBuilder();

        builder.setAllCorners(CornerFamily.ROUNDED, 30);

        imageUserPic.setShapeAppearanceModel(builder.build());


    }

    private void fetchUserInformation() {

        loadingSnackBar = Snackbar.make(toolbar,"Loading information..",Snackbar.LENGTH_INDEFINITE);
        loadingSnackBar.show();

        UsuarioInformation.getUsuarioInformation(currentUsuario.getEmail(), new UsuarioInformation.OnInformationFetchCompleteListener() {
            @Override
            public void onInformationSuccess(Usuario information, String text) {
                currentUsuario = information;
                handleUserInformation(currentUsuario);
                Toast.makeText(ProfileEditActivity.this, text, Toast.LENGTH_SHORT).show();
                loadingSnackBar.dismiss();
                clickLock = 1;
            }

            @Override
            public void onInformationCancelled(String error) {
                Toast.makeText(ProfileEditActivity.this, error, Toast.LENGTH_SHORT).show();
                loadingSnackBar.dismiss();
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

                if (clickLock != 0){
                    updateUser();
                }

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
                cameraPermission();
                break;

            case ProfileEditBottomSheetDialog.FAB_GALLERY:
                externalStoragePermission();
                break;

            case ProfileEditBottomSheetDialog.FAB_REMOVE_PIC:
                Toast.makeText(this, "Remove pic", Toast.LENGTH_SHORT).show();
                break;

        }


    }

    //Permissions methods----------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult( requestCode,permissions,grantResults,this);

    }

    @AfterPermissionGranted(CAMERA_PERMISSION)
    private void cameraPermission(){

        String [] perm = {Manifest.permission.CAMERA};

        if (EasyPermissions.hasPermissions(this,perm)){

            Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (camIntent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(camIntent,CAMERA_SELECTION);
            }


        }else {
            EasyPermissions.requestPermissions(this,getString(R.string.camera_permission),CAMERA_PERMISSION,perm);
        }

    }

    @AfterPermissionGranted(STORAGE_PERMISSION)
    private void externalStoragePermission(){

        String [] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this,perm)){

            Intent galIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (galIntent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(galIntent,GALLERY_SELECTION);
            }

        }else {
            EasyPermissions.requestPermissions(this,getString(R.string.external_storage_permission),GALLERY_SELECTION,perm);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }

    }
}