package tony.com.goodreporter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    //    CallbackManager callbackManager;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final List<String> permissions = Arrays.asList("public_profile", "email", "user_posts", "user_friends");
                        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (user == null) {
                                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    Log.d(TAG, "User signed up and logged in through Facebook!");
                                } else {
                                    Log.d(TAG, "User logged in through Facebook!");
                                }
                                if(user != null) {
                                    gotoMain();
                                }
                            }
                        });
                    }
                }
        );
        if(ParseUser.getCurrentUser() != null)
            gotoMain();
    }

    private void gotoMain() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
