package projects.chirolhill.juliette.finalprojectchirolhilljuliette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

//import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.User;
import projects.chirolhill.juliette.finalprojectchirolhilljuliette.model.UserSingleton;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivityTag";
    private static final int RC_SIGN_IN = 0;

    private Button btnSignIn;
    private TextView textMoment;
    private TextView textDescr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signin);

        btnSignIn = findViewById(R.id.btnSignIn);
        textMoment = findViewById(R.id.textMoment);
        textDescr = findViewById(R.id.textDescr);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignIn.setVisibility(View.GONE);
                textMoment.setVisibility(View.VISIBLE);
                textDescr.setVisibility(View.GONE);

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "in onactivyresult");
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "in request code of onactivityresult");
//            Log.d(TAG, data.getExtras().toString());
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, user.getUid());

                // save to shared preferences
                SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = prefs.edit();
                prefEditor.putString(UserSingleton.PREF_USER_ID, user.getUid());
                prefEditor.commit();

                // launch main activity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                i.putExtra(UserSingleton.EXTRA_USER_ID, user.getUid());
                startActivity(i);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
//                Log.d(TAG, Integer.toString(response.getError().getErrorCode()));
                Log.d(TAG, "in else of onactivityresult");
            }
        }
    }

}
