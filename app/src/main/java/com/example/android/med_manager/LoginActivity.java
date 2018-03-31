package com.example.android.med_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

/**
 * Login activity that is a full screen
 */
public class LoginActivity extends Activity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = RESULT_OK;
    private ImageButton mSignUpButton;
    GoogleSignInClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets up the layout to the correct view
        setContentView(R.layout.activity_login);
        // finds the image view
        ImageView appLogo = findViewById(R.id.app_logo);
        // finds the button
        mSignUpButton = findViewById(R.id.sign_up);
        // Sets up the animation
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim.reset();
        // Starts the animation
        appLogo.startAnimation(fadeInAnim);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null)
        {

            Log.d(LOG_TAG,"User have not signed in previously");
        } else
        {
            // Start next activity
            Intent intent = new Intent(this,HomePageActivity.class);
            startActivity(intent);
            Log.d(LOG_TAG,"User have previously signed in");
        }

        // Sets up action for sign up
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
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
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String displayName = account.getDisplayName();
            // Signed in successfully, show authenticated UI.
            Log.d(LOG_TAG, displayName + " signed in successfully");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.d(LOG_TAG,"failed to sign in");
        }
    }
}
