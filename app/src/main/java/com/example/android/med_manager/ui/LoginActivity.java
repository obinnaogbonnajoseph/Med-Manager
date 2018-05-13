package com.example.android.med_manager.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.med_manager.R;
import com.example.android.med_manager.services.InternetService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.example.android.med_manager.services.InternetService.MyBinder;

/**
 * Login activity that is a full screen
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener {
    // Log tag
    private static final String LOGIN_LOG = LoginActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;
    // Declare authentication
    private GoogleSignInClient mClient;
    private FirebaseAuth mAuth;
    // Sign up buttons
    private EditText mEmailSignUp, mEmailSignIn, mPasswordSignUp, mPasswordSignIn, mNameText;
    // View buttons
    private LinearLayout mSignInLayout, mSignUpLayout;
    private TextView mSignUp;
    // Loading indicator
    private ProgressBar mLoadingIndicator;
    // Service object
    InternetService mService;
    // Checks if internet service is bound to the activity
    private boolean mIsBound;
    // User details


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets up the layout to the correct view
        setContentView(R.layout.activity_login);
        // finds the image view
        ImageView appLogo = findViewById(R.id.app_logo);
        // Sets up the animation
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim.reset();
        // Starts the animation
        appLogo.startAnimation(fadeInAnim);
        // Set up the views
        mEmailSignUp = findViewById(R.id.sign_up_email);
        mEmailSignIn = findViewById(R.id.login_username);
        mPasswordSignUp = findViewById(R.id.sign_up_password);
        mPasswordSignIn = findViewById(R.id.login_password);
        mNameText = findViewById(R.id.sign_up_name);
        mSignInLayout = findViewById(R.id.sign_in_details);
        mSignUpLayout = findViewById(R.id.sign_up_details);
        // Set up loading indicator
        mLoadingIndicator = findViewById(R.id.loading_indicator);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // First check network connectivity
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mClient = GoogleSignIn.getClient(this, gso);

        //Start to initialize authentication
        mAuth = FirebaseAuth.getInstance();
        // Be sure that sign in layout is visible
        mSignInLayout.setVisibility(View.VISIBLE);
        mSignUpLayout.setVisibility(View.GONE);

        // Set up the views for sign in and sign up
        // Sign in text
        TextView signIn = findViewById(R.id.text_login);
        signIn.setOnClickListener(this);
        // Sign up text
        TextView signUpSubmit = findViewById(R.id.text_submit);
        signUpSubmit.setOnClickListener(this);
        // Google sign in button
        Button googleSignIn = findViewById(R.id.btn_google_sign_in);
        googleSignIn.setOnClickListener(this);
        // Sign up text
        mSignUp = findViewById(R.id.text_sign_up);
        mSignUp.setOnClickListener(this);
        // Bind my service to this activity
        Intent internetIntent = new Intent(this, InternetService.class);
        bindService(internetIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        // Add an authentication state listener
        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            Log.e(LOGIN_LOG,"Wrong request code " + requestCode);
            Toast.makeText(this, "Sign in failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        // Verify that user is not null
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(null != currentUser) {
            // Sign in
            updateUI(currentUser);
        } else updateUI(null);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        String emailSignUp = mEmailSignUp.getText().toString();
        String passwordSignUp = mPasswordSignUp.getText().toString();
        String emailSignIn = mEmailSignIn.getText().toString();
        String passwordSignIn = mPasswordSignIn.getText().toString();
        switch(viewId){
            case R.id.btn_google_sign_in:
                if(checkNetworkConnectivity()) {
                    googleSignIn();
                } else {
                    Toast.makeText(this, "No Network Connection.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_login:
                // Get the email and password
                if(checkNetworkConnectivity()) {
                    emailSignIn(emailSignIn, passwordSignIn);
                } else {
                    Toast.makeText(this, "No Network Connection.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_sign_up:
                // Check to see what layout is visible and change it
                if(mSignInLayout.isShown()) {
                    mSignUpLayout.setVisibility(View.VISIBLE);
                    mSignInLayout.setVisibility(View.GONE);
                    mSignUp.setText(R.string.text_sign_in_with_google);
                } else {
                    mSignInLayout.setVisibility(View.VISIBLE);
                    mSignUpLayout.setVisibility(View.GONE);
                    mSignUp.setText(R.string.text_sign_up);
                }
                break;
            case R.id.text_submit:
                if(checkNetworkConnectivity()) {
                    createAccount(emailSignUp, passwordSignUp);
                } else {
                    Toast.makeText(this, "No Network Connection.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove authentication state listener
        mAuth.removeAuthStateListener(this);
    }

    private void createAccount(String email, String password) {
        Log.d(LOGIN_LOG, "createAccount:" + email);
        if(!validateForm(mEmailSignUp,mPasswordSignUp,mNameText)) {
            return;
        }
        //showProgressDialog();
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // create user with email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOGIN_LOG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOGIN_LOG, "createUserWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                        //hideProgressDialog();
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this,"google sign in account successful",
                    Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LOGIN_LOG, "signInResult:failed code=" + e.getStatusCode());
            Log.d(LOGIN_LOG,"failed to sign in");
            Toast.makeText(this,"Sign in with google failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOGIN_LOG, "firebaseAuthWithGoogle:" + acct.getId());
        //showProgressDialog()
        mLoadingIndicator.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Sign in success update UI with the signed-in user's information
                            Log.d(LOGIN_LOG,"signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // if sign in fails, display a message to the user.
                            Log.w(LOGIN_LOG,"signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        //hideProgressDialog();
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void emailSignIn(String email, String password) {
        Log.d(LOGIN_LOG, "signIn:" + email);
        if(!validateForm(mEmailSignIn,mPasswordSignIn,null)) {
            return;
        }
        // showProgressDialog();
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Start sign_in_with_email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOGIN_LOG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOGIN_LOG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                        // hideProgressDialog();
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = mClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private boolean validateForm(EditText emailText, EditText passwordText, EditText nameText) {
        boolean valid = true;

        if(nameText!= null) {
            String givenName = nameText.getText().toString();
            if (TextUtils.isEmpty(givenName)) {
                nameText.setError("Required.");
                valid = false;
            } else {
                nameText.setError(null);
            }
        }


        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Required. Enter Valid email");
            valid = false;
        } else {
            emailText.setError(null);
        }

        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordText.setError("Required.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private Boolean checkNetworkConnectivity() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, return true, else return false
        return networkInfo != null && networkInfo.isConnected();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder binder = (MyBinder) service;
            mService = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(user != null) {
            // start the next activity
            Intent intent = new Intent(LoginActivity.this,HomePageActivity.class);
            Toast.makeText(this, "Sign in Successful",
                    Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else {
            // Do the opposite
            Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
