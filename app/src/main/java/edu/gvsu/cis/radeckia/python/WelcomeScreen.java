package edu.gvsu.cis.radeckia.python;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener {

    private SignInButton signIn;
    private Button signOut;
    private Button playGame;
    private Button achievements;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private int REQUEST_ACHIEVEMENTS = 123;
    private final int GAMES_WON = 121;
    private static int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        signIn = (SignInButton) findViewById(R.id.signIn);
        signOut = (Button) findViewById(R.id.signOut);
        achievements = (Button) findViewById(R.id.achievements);
        playGame = (Button) findViewById(R.id.playGame);
        setSupportActionBar(toolbar);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)

                .build();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchGame = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivityForResult(launchGame, GAMES_WON);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignInClicked = false;
                if(mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                }
                mGoogleApiClient.disconnect();

                signIn.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.GONE);
                achievements.setVisibility(View.GONE);
            }
        });

        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        signIn.setVisibility(View.GONE);
        signOut.setVisibility(View.VISIBLE);
        achievements.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.playGame:
                Intent launchGame = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivityForResult(launchGame, GAMES_WON);
                break;

            case R.id.signIn:
                mSignInClicked = true;
                mGoogleApiClient.connect();
                break;

            case R.id.signOut:
                mSignInClicked = false;
                if(mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                }
                mGoogleApiClient.disconnect();

                signIn.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.GONE);
                achievements.setVisibility(View.GONE);
                break;
            case R.id.achievements:
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(mResolvingConnectionFailure) {
            return;
        }

        if(mSignInClicked) {
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if(connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mResolvingConnectionFailure = true;
                }
                catch(IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    mResolvingConnectionFailure = false;
                    mGoogleApiClient.connect();
                }
            }
            else {
                Snackbar.make(signOut, connectionResult.getErrorMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            if(mResolvingConnectionFailure) {
                mResolvingConnectionFailure = false;
                mGoogleApiClient.connect();
            }
        }
        else if(requestCode == GAMES_WON) {
            if(data != null) {
                int wins = data.getIntExtra("Wins", 0);
                unlockAchievement(wins);
            }
        }


    }

    public void unlockAchievement(int counter) {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() && counter > 0) {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.beginner), counter);
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.better), counter);
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.good), counter);
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.unstoppable), counter);
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.godlike), counter);
        }
    }
}
