package edu.gvsu.cis.radeckia.python;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.Date;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomEntityCreator;
import com.google.android.gms.games.multiplayer.realtime.RoomRef;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import edu.gvsu.cis.radeckia.python.Main;
import edu.gvsu.cis.radeckia.python.Disks;
import edu.gvsu.cis.radeckia.python.Player;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private int gameCols = 7;
    private int gameRows = 6;
    private GameLogic gameLogic;
    private long reset = 0;
    private GoogleApiClient mClient;
    private String mRoomId = "";
    private RoomUpdateListener mListener;
    private RealTimeMessageReceivedListener realTimeListener;
    private RoomStatusUpdateListener mUpdateListener;
    private static int RC_SIGN_IN = 9001;
    private final static int RC_WAITING_ROOM = 10002;
    private boolean mResolvingConnectionFailure = false;
    //private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private int cols = 7;
    private int rows = 6;
    private Disks disks[][] = new Disks[cols][rows];
    private Player player1;
    private Player player2;
    private boolean gameDone = false;

    /*private void reset() {
        Date increment = new Date();
        reset = increment.getTime();
        if(reset == 100) {
            //updateBoard();
            increment.setTime(0);
            reset = 0;
        }
    }*/

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        RoomConfig.Builder builder = RoomConfig.builder(mListener);
        builder.setMessageReceivedListener(realTimeListener);
        builder.setRoomStatusUpdateListener(mUpdateListener);

        return builder;
    }

    private void startQuickGame() {
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
    }

    public void placePiece(String Player, int col){
        int i = col;

        if(legalMove(i)){
            for (int k = disks[i].length - 1; k > 0; k++) {
                if (disks[k][i] == null){
                    Disks d = new Disks (Player, k, i);
                    disks[k][i] = d;

                    if(hasWon(k, i)){
                        gameDone =  true;
                        endGame();
                    }
                    //TODO add more work to update the UI
                }
            }
        }

    }

    //checks if the move is possible
    public boolean legalMove(int i){
        if (gameDone == false) {
            if (disks[0][i] != null) {
                return false;
            }
        }
        return true;
    }

    public void endGame(){
        int GAMES_WON = 0;
        Intent launchme = new Intent(MainActivity.this, ResultsScreen.class);
        launchme.putExtra("winner", GAMES_WON);
        startActivityForResult(launchme, GAMES_WON);
    }

    //returns how many pieces there are in a row.
    public boolean hasWon(int r, int c){

        int i = 1;
        int t = 1;
        String s = disks[r][c].getPlayer();

        //checks horizontal options
        if (disks[r+1][c].getPlayer().equals(s)&& r+1 < disks.length){
            i++;
            if(disks[r+2][c].getPlayer().equals(s) && r+2 < disks.length){
                i++;
                if(disks[r+3][c].getPlayer().equals(s)&& r+3 < disks.length){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
            if(disks[r-1][c].getPlayer().equals(s) && r-1 >=0){
                i++;
                if (i == 4){
                    return true;
                }
                if(disks[r-2][c].getPlayer().equals(s) && r-2 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }

                }

            }
        }
        if(disks[r-1][c].getPlayer().equals(s) && r-1 >=0){
            i++;
            if(disks[r-2][c].getPlayer().equals(s) && r-2 >= 0){
                i++;
                if(disks[r-3][c].getPlayer().equals(s)&& r-3 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
        }

        //checks for diaginal left
        if (disks[r+1][c+1].getPlayer().equals(s)&& r+1 < disks.length && c+1 < disks[r].length){
            i++;
            if (disks[r+2][c+2].getPlayer().equals(s)&& r+2 < disks.length && c+2 < disks[r].length){
                i++;
                if (disks[r+3][c+3].getPlayer().equals(s)&& r+3 < disks.length && c+3 < disks[r].length){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
            if(disks[r-1][c-i].getPlayer().equals(s) && r-1 >=0){
                i++;
                if (i == 4){
                    return true;
                }
                if(disks[r-2][c-2].getPlayer().equals(s) && r-2 >=0 && c-i >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }

                }

            }
        }
        if(disks[r-1][c-1].getPlayer().equals(s) && r-1 >=0 && c-1 >=0){
            i++;
            if(disks[r-2][c-2].getPlayer().equals(s) && r-2 >=0 && c-2 >=0){
                i++;
                if(disks[r-3][c-3].getPlayer().equals(s) && r-3 >=0 && c-3 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
        }

        //checks for diaginal right
        if (disks[r+1][c-1].getPlayer().equals(s)&& r+1 < disks.length && c+1 >=0){
            i++;
            if (disks[r+2][c-2].getPlayer().equals(s)&& r+2 < disks.length && c+2 >=0){
                i++;
                if (disks[r+3][c-3].getPlayer().equals(s)&& r+3 < disks.length && c+3 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
            if(disks[r-1][c+i].getPlayer().equals(s) && r-1 >=0 && c+1 < disks[r].length){
                i++;
                if (i == 4){
                    return true;
                }
                if(disks[r-2][c+2].getPlayer().equals(s) && r-2 >=0 && c+2 < disks[r].length){
                    i++;
                    if (i == 4){
                        return true;
                    }

                }

            }
        }
        if(disks[r-1][c+1].getPlayer().equals(s) && r+1 >=0 && c+1 < disks[r].length){
            i++;
            if(disks[r-2][c+2].getPlayer().equals(s) && r+2 >=0 && c+2 < disks[r].length){
                i++;
                if(disks[r-3][c+3].getPlayer().equals(s) && r+3 >=0 && c+3 < disks[r].length){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
        }

        //checks for vertical
        if (disks[r][c+1].getPlayer().equals(s)&& c+1 < disks.length){
            i++;
            if(disks[r][c+2].getPlayer().equals(s) && c+2 < disks.length){
                i++;
                if(disks[r][c+3].getPlayer().equals(s)&& c+3 < disks.length){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
            if(disks[r][c-1].getPlayer().equals(s) && c-1 >=0){
                i++;
                if (i == 4){
                    return true;
                }
                if(disks[r-2][c].getPlayer().equals(s) && c-2 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }

                }

            }
        }
        if(disks[r][c-1].getPlayer().equals(s) && c-1>=0){
            i++;
            if(disks[r][c-2].getPlayer().equals(s) && c-2 >= 0){
                i++;
                if(disks[r][c-3].getPlayer().equals(s)&& c-3 >=0){
                    i++;
                    if (i == 4){
                        return true;
                    }
                }
            }
        }



        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        Drawable border = getResources().getDrawable(R.drawable.tile_empty);
        Drawable p1_tile = getResources().getDrawable(R.drawable.p1_tile_occupied);
        Drawable p2_tile = getResources().getDrawable(R.drawable.p2_tile_occupied);
        Button one = (Button) findViewById(R.id.one);
        Button two = (Button) findViewById(R.id.two);
        Button three = (Button) findViewById(R.id.three);
        Button four = (Button) findViewById(R.id.four);
        Button five = (Button) findViewById(R.id.five);
        Button six = (Button) findViewById(R.id.six);
        Button seven = (Button) findViewById(R.id.seven);

        for(int i = 0; i < gameCols; i++) {
            for(int j = 0; j < gameRows; j++) {
                //ArrayList<Cell> tiles = gameLogic.getNonEmptyTiles();

                int ri = j;
                int ci = i;

                TextView myText = new TextView(this);

                //for(int k = 0; k < size; k++) {
                    //tiles.size()

                    /*if(tiles.get(k).column != j && tiles.get(k).row == i) {
                        myText.setText(" ");
                    }
                    else if(tiles.get(k).column == j && tiles.get(k).row == i) {
                        myText.setText("" + tiles.get(k).value);
                        tiles.remove(k);
                    }*/
                //}
                myText.setTextSize(25);

                //if()
                myText.setBackground(border);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setHeight(139);
                myText.setWidth(139);
                /* place the TextView at the desired row and column */
                GridLayout.Spec r_spec = GridLayout.spec(ri, GridLayout.CENTER);
                GridLayout.Spec c_spec = GridLayout.spec(ci, GridLayout.CENTER);
                GridLayout.LayoutParams par = new GridLayout.LayoutParams(r_spec, c_spec);
                par.setGravity(Gravity.CENTER_HORIZONTAL);
                grid.addView(myText, par);

                /*for(int m = 0; m < cols-1; m++) {
                    for(int n = 0; n < rows-1; n++) {
                        if(disks[m][n].getPlayer().equals(player1.name())) {
                            myText.setBackground(p1_tile);
                        }
                        else if(disks[m][n].getPlayer().equals(player2.name())) {
                            myText.setBackground(p2_tile);
                        }
                    }
                }*/
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if()
                placePiece();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //String name = Games.getCurrentAccountName(mClient);
        //Log.d(TAG, "onConnected: " + name + " " + mClient.isConnected());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked /*|| mAutoStartSignInFlow*/) {
            //mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."

            if(mSignInClicked) {
                mSignInClicked = false;
                mResolvingConnectionFailure = true;
            }

            if(connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mResolvingConnectionFailure = true;
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    mResolvingConnectionFailure = false;
                    mClient.connect();
                }
            }
            /*else {
                Snackbar.make(this, connectionResult.getErrorMessage(), Snackbar.LENGTH_LONG);
            }

           if (!asd.resolveConnectionFailure(this,
                    mClient, connectionResult,
                    RC_SIGN_IN, R.string.on_signIn_failed)) {
                mResolvingConnectionFailure = false;
            }*/
        }

        // Put code here to display the sign-in button
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."


                //BaseGameUtils.showActivityResultError(this,
                //        requestCode, resultCode, R.string.on_signIn_failed);

                if (requestCode == RC_WAITING_ROOM) {
                    if (resultCode == Activity.RESULT_OK) {
                        // (start game)
                    }
                    else if (resultCode == Activity.RESULT_CANCELED) {
                        // Waiting room was dismissed with the back button. The meaning of this
                        // action is up to the game. You may choose to leave the room and cancel the
                        // match, or do something else like minimize the waiting room and
                        // continue to connect in the background.

                        // in this example, we take the simple approach and just leave the room:
                        Games.RealTimeMultiplayer.leave(mClient, null, mRoomId);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                        // player wants to leave the room.
                        Games.RealTimeMultiplayer.leave(mClient, null, mRoomId);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.

            // get waiting room intent
            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mClient, room, 2);
            startActivityForResult(i, RC_WAITING_ROOM);
        }
    }

    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.

            // get waiting room intent
            Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mClient, room, 2);
            startActivityForResult(i, RC_WAITING_ROOM);
        }
    }

    public void onRoomConnected(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
        }
    }

}
