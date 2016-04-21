package edu.gvsu.cis.radeckia.python;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Date;
import com.google.android.gms.*;
import com.google.android.gms.games.Games;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private int size = 20;
    private GameLogic gameLogic;
    private long reset = 0;
    private GoogleApiClient mClient;

    private void reset() {
        Date increment = new Date();
        reset = increment.getTime();
        if(reset == 100) {
            //updateBoard();
            increment.setTime(0);
            reset = 0;
        }
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
        Drawable border = getResources().getDrawable(R.drawable.tile);
        Drawable snake = getResources().getDrawable(R.drawable.snake);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                //ArrayList<Cell> tiles = gameLogic.getNonEmptyTiles();

                int ri = j;
                int ci = i;

                TextView myText = new TextView(this);

                for(int k = 0; k < size; k++) {
                    //tiles.size()

                    /*if(tiles.get(k).column != j && tiles.get(k).row == i) {
                        myText.setText(" ");
                    }
                    else if(tiles.get(k).column == j && tiles.get(k).row == i) {
                        myText.setText("" + tiles.get(k).value);
                        tiles.remove(k);
                    }*/
                }
                myText.setTextSize(25);

                //if()
                myText.setBackground(border);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setHeight(49);
                myText.setWidth(49);
                /* place the TextView at the desired row and column */
                GridLayout.Spec r_spec = GridLayout.spec(ri, GridLayout.CENTER);
                GridLayout.Spec c_spec = GridLayout.spec(ci, GridLayout.CENTER);
                GridLayout.LayoutParams par = new GridLayout.LayoutParams(r_spec, c_spec);
                par.setGravity(Gravity.CENTER_HORIZONTAL);
                grid.addView(myText, par);
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
