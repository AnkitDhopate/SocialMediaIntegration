package com.example.socialmediaintegration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class UserDetailsActivity extends AppCompatActivity {
    private ImageView personPhoto;
    private TextView personName, personEmail, personGivenName, personFamilyName, personId;
    private LoginButton logoutButton;
    private Button googleSignOutBtn;
    private CardView cardView;
    private String name, email, givenName, familyName, id;

    private GoogleSignInClient mGoogleClient;
    private AccessTokenTracker accessTokenTracker;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        personPhoto = findViewById(R.id.personPhoto);
        personName = findViewById(R.id.personName);
        personGivenName = findViewById(R.id.personGivenName);
        personFamilyName = findViewById(R.id.personFamilyName);
        personEmail = findViewById(R.id.personEmail);
        personId = findViewById(R.id.personId);
        googleSignOutBtn = findViewById(R.id.google_sign_out_btn);
        cardView = findViewById(R.id.card_view);
        logoutButton = findViewById(R.id.facebook_logout_button);

        name = getIntent().getExtras().getString("personName");
        email = getIntent().getExtras().getString("personEmail");
        givenName = getIntent().getExtras().getString("personGivenName");
        familyName = getIntent().getExtras().getString("personFamilyName");
        id = getIntent().getExtras().getString("personId");

        if(email == null)
        {
            personEmail.setVisibility(View.GONE);
        }
        if(givenName == null)
        {
            personGivenName.setVisibility(View.GONE);
        }
        if(familyName == null)
        {
            personFamilyName.setVisibility(View.GONE);
        }
        if(id == null)
        {
            personId.setVisibility(View.GONE);
        }
        if (!getIntent().getExtras().getString("personPhoto").equals("noPhoto")) {
            Picasso.get().load(getIntent().getExtras().getString("personPhoto")).into(personPhoto);
        } else {
            cardView.setVisibility(View.GONE);
        }

        personName.setText("Name : " + name);
        personEmail.setText("Email : " + email);
        personGivenName.setText("Given Name : " + givenName);
        personFamilyName.setText("Family Name : " + familyName);
        personId.setText("ID : " + id);


        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);

        if (acc != null) {
            logoutButton.setVisibility(View.GONE);
        } else {
            googleSignOutBtn.setVisibility(View.GONE);

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if (currentAccessToken == null) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(UserDetailsActivity.this, MainActivity.class));
                        finish();
                    }
                }
            };
        }

        googleSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserDetailsActivity.this, "Signed out successfully !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserDetailsActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}