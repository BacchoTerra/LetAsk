package com.bacchoterra.letask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bacchoterra.letask.R;
import com.bacchoterra.letask.config.FirebaseConfig;
import com.bacchoterra.letask.helper.Base64Custom;
import com.bacchoterra.letask.helper.MyHelper;
import com.bacchoterra.letask.helper.SharedPrefsUtil;
import com.bacchoterra.letask.model.Usuario;
import com.bacchoterra.letask.firebase.UsuarioInformation;
import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Layout components
    private Toolbar toolbar;
    private TextView txtAddQuestion;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private FrameLayout containerLayout;

    //Header layout components
    private CircleImageView imageUserPic;
    private CircleImageView imageUserCountry;
    private TextView txtUserName;
    private ImageView imageExit;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser firebaseUser;

    //Strings
    private String userCountry;
    private String userProvider;

    //Extras
    private Button btnRefreshUserInfo;
    public static final int BTN_REFRESH_ID = 45;
    private Usuario usuario = new Usuario();
    public static final String KEY_FOR_USER_EMAIL = "user_email_key";
    public static boolean shouldRefreshUserInfo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {

        World.init(this);
        mAuth = FirebaseConfig.getFBAuth();
        rootRef = FirebaseConfig.getFBDatabase();

        initViews();
        initToolbarAndDrawer();
        initBottomNavigation();
        configContainerLayoutBounds();
        bindUserInfoInDrawer();
        getUserProvider();

    }

    private void initViews() {

        toolbar = findViewById(R.id.activity_main_toolbar);
        txtAddQuestion = findViewById(R.id.activity_main_toolbar_txtAddQuestion);
        drawerLayout = findViewById(R.id.activity_main_drawerLayout);
        navView = findViewById(R.id.activity_main_navView);
        bottomNavigationViewEx = findViewById(R.id.activity_main_bottomNavView);
        containerLayout = findViewById(R.id.activity_main_containerLayout);

        //Header components
        View headerLayout = navView.getHeaderView(0);
        txtUserName = headerLayout.findViewById(R.id.header_layout_txtUserName);
        imageUserPic = headerLayout.findViewById(R.id.header_layout_imageUserPic);
        imageUserCountry = headerLayout.findViewById(R.id.header_layout_imageUserCountry);
        imageExit = headerLayout.findViewById(R.id.header_layout_imageExit);


        imageExit.setOnClickListener(this);
        txtUserName.setOnClickListener(this);
        txtAddQuestion.setOnClickListener(this);

    }

    private void initToolbarAndDrawer() {

        setSupportActionBar(toolbar);
        createToolbarRefreshButton();
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(abdt);
        abdt.syncState();

    }

    private void createToolbarRefreshButton() {

        btnRefreshUserInfo = new Button(this);
        btnRefreshUserInfo.setText(R.string.refresh);
        btnRefreshUserInfo.setVisibility(View.GONE);
        btnRefreshUserInfo.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_button_all_round_primary_color, null));
        btnRefreshUserInfo.setTextColor(getResources().getColor(R.color.white));
        btnRefreshUserInfo.setId(BTN_REFRESH_ID);
        btnRefreshUserInfo.setOnClickListener(this);


        toolbar.addView(btnRefreshUserInfo);


    }

    private void bindUserInfoInDrawer() {

        btnRefreshUserInfo.setVisibility(View.GONE);


        if (mAuth.getCurrentUser() != null) {


            mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseUser = mAuth.getCurrentUser();

                        if (!firebaseUser.isAnonymous()) {
                            usuario.setName(firebaseUser.getDisplayName());
                            usuario.setEmail(firebaseUser.getEmail());
                            txtUserName.setText(usuario.getName());
                            txtUserName.setEnabled(true);
                            handleUserCountry(usuario.getEmail());
                            handleUserProfilePicture();
                        } else {
                            updateUiForAnonymousUser();
                        }


                    } else {
                        txtUserName.setText(R.string.error_fetching_user_info);
                        btnRefreshUserInfo.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, R.string.error_fetching_user_information, Toast.LENGTH_SHORT).show();


                    }
                }
            });


        }

    }

    private void updateUiForAnonymousUser() {

        txtUserName.setText(R.string.guest_account);
        imageUserCountry.setImageResource(World.getFlagOf(R.drawable.globe));


    }

    private void handleUserProfilePicture() {

        if (firebaseUser.getPhotoUrl() != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).placeholder(R.drawable.ic_person_24px).into(imageUserPic);
            usuario.setUserPicUrl(firebaseUser.getPhotoUrl().toString());
        } else {
            Glide.with(this).load(R.drawable.ic_person_24px).into(imageUserPic);
        }


    }

    private void handleUserCountry(final String email) {

        userCountry = SharedPrefsUtil.getUserCountry(this, email);

        if (userCountry != null) {

            final int flag = World.getFlagOf(userCountry);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), flag, null);
            Glide.with(MainActivity.this).load(drawable).into(imageUserCountry);
        } else {

            rootRef.child(FirebaseConfig.USERS_NOD)
                    .child(Base64Custom.toBase64(email))
                    .child(FirebaseConfig.COUNTRY_NOD)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            userCountry = snapshot.getValue(String.class);
                            assert userCountry != null;
                            final int flag = World.getFlagOf(userCountry);
                            Drawable drawable = ResourcesCompat.getDrawable(getResources(), flag, null);
                            Glide.with(MainActivity.this).load(drawable).into(imageUserCountry);

                            SharedPrefsUtil.saveUserCountry(MainActivity.this, userCountry, email);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        usuario.setCountry(userCountry);

    }

    private void logout() {

        if (MyHelper.netConn(this) && firebaseUser != null) {


            if (getUserProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();


                GoogleSignInClient mSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

                mSignInClient.signOut();

            }

            if (UsuarioInformation.usuario != null) {
                UsuarioInformation.removeInstance();
            }

            if (firebaseUser.isAnonymous()) {
                firebaseUser.delete();

            } else {
                mAuth.signOut();
            }
            startActivity(new Intent(this, AuthActivity.class));
            finish();

        } else if (!MyHelper.netConn(this)) {
            MyHelper.showSnackbarLong(R.string.no_internet_connection, drawerLayout);
        } else {
            MyHelper.showSnackbarLong(R.string.please_refresh_user_info, drawerLayout);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }


    }

    private String getUserProvider() {


        if (firebaseUser != null) {

            for (UserInfo userInfo : firebaseUser.getProviderData()) {

                userProvider = userInfo.getProviderId();

            }

        }

        return userProvider;

    }

    private void initBottomNavigation() {

        bottomNavigationViewEx.setIconSize(28);


    }

    private void configContainerLayoutBounds() {

        bottomNavigationViewEx.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomNavigationViewEx.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int min_bottom_marg = bottomNavigationViewEx.getHeight();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) containerLayout.getLayoutParams();

                params.setMargins(0, 0, 0, min_bottom_marg);
                containerLayout.setLayoutParams(params);

            }
        });

    }

    private void createExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldRefreshUserInfo) {
            bindUserInfoInDrawer();
            shouldRefreshUserInfo = false;
            Toast.makeText(this, "Information Updated", Toast.LENGTH_SHORT).show();
        }

    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.header_layout_imageExit:

                createExitDialog();
                break;
            case BTN_REFRESH_ID:
                bindUserInfoInDrawer();
                break;

            case R.id.header_layout_txtUserName:


                if (MyHelper.netConn(this) && firebaseUser != null) {

                    if (!firebaseUser.isAnonymous()) {
                        Intent intent = new Intent(this, ProfileEditActivity.class);
                        intent.putExtra(KEY_FOR_USER_EMAIL, usuario.getEmail());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(this, R.string.create_an_account_to_edit_your_profile, Toast.LENGTH_SHORT).show();
                    }
                } else if (!MyHelper.netConn(this)) {
                    MyHelper.showSnackbarLong(R.string.no_internet_connection, drawerLayout);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    MyHelper.showSnackbarLong(R.string.please_refresh_user_info, drawerLayout);
                }

                break;
            case R.id.activity_main_toolbar_txtAddQuestion:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}