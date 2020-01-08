package org.com.example.dogal_sepeti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton signUp_with_Google_btn;
    private com.facebook.login.widget.LoginButton facebookSignUpButton;
    private ImageButton emailSignUpButton;
    private Button signInButton;
    private HashMap<String, Object> profileMap;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 786;
    private CallbackManager mCallbackManager;
    private ImageButton backArrow_image_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ProgressDialog progressDialog;
    private GoogleSignInAccount googleSignInAccount;
    private FirebaseUser currentUser;
    private StorageReference userProfileImagesRef;
    private LoginResult loginResult_In_facebook;

    private ConstraintLayout logOut_consLayout;
    private LinearLayout signUp_linearLayout;
    private Button logOut_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        initializeFields();

        configureGoogleSignUp();

        configureFacebookSignUp();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        logOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserStatus("offline");
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                signUp_linearLayout.setVisibility(View.VISIBLE);
                logOut_consLayout.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("userState")
                .updateChildren(onlineStateMap);

    }


    private void activateBackArrowButton() {
        backArrow_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    public void facebookButtonClicked(View view) {

        facebookSignUpButton.performClick();
    }

    private void configureFacebookSignUp() {

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookSignUpButton = new LoginButton(this);
        facebookSignUpButton.setReadPermissions("email", "public_profile");
        facebookSignUpButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                loginResult_In_facebook = loginResult;
            }

            @Override
            public void onCancel() {
                Log.d("", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("", "facebook:onError", error);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            saveFacebookCredentialsInFirebase();
                        } else {
                            // If sign in fails, display a message to the user.
                            String e = task.getException().toString();
                            Log.w("", "signInWithCredential:failure" + e);
                            Toast.makeText(getApplicationContext(), "Authentication failed." + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                googleSignInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle() {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            saveGoogleCredentialsInFirebase();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void saveGoogleCredentialsInFirebase() {
        progressDialog.setTitle("Signing to your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        profileMap.put("email", googleSignInAccount.getEmail());
        profileMap.put("name", googleSignInAccount.getGivenName());
        profileMap.put("surname", googleSignInAccount.getFamilyName());
        profileMap.put("image", googleSignInAccount.getPhotoUrl().toString());

        rootRef.child("Users").child(currentUser.getUid()).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUserStatus("Online");
                    changeVisibility();
                    progressDialog.dismiss();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });


    }

    private void changeVisibility() {
        if(mAuth.getUid() != null){
            logOut_consLayout.setVisibility(View.VISIBLE);
            signUp_linearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureGoogleSignUp() {
        //***     CODES FOR GOOGLE *** //
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signUp_with_Google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_signUp_btn:
                        signIn();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeVisibility();
    }

    private void initializeFields() {
        logOut_btn = findViewById(R.id.signOut);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        backArrow_image_btn = findViewById(R.id.arrow);
        signUp_with_Google_btn = findViewById(R.id.google_signUp_btn);
        emailSignUpButton = findViewById(R.id.signUp_with_email_btn);
        signInButton = findViewById(R.id.signInButton);
        progressDialog = new ProgressDialog(this);
        profileMap = new HashMap<>();

        emailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpWithEmailActivity.class));
            }
        });
        logOut_consLayout = findViewById(R.id.userLogOut);
        signUp_linearLayout = findViewById(R.id.MainViewToLogin);

        changeVisibility();

        activateBackArrowButton();
    }

    private void saveFacebookCredentialsInFirebase() {
        progressDialog.setTitle("Signing to your Account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        profileMap.put("name", currentUser.getDisplayName());
        profileMap.put("email", currentUser.getEmail());
        profileMap.put("phone_num", currentUser.getPhoneNumber());
        profileMap.put("image", currentUser.getPhotoUrl().toString() + "?height=500");

        rootRef.child("Users").child(currentUser.getUid()).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            changeVisibility();
                            progressDialog.dismiss();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });


    }


}
