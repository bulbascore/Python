package edu.gvsu.cis.radeckia.python;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int size = 0;
    private GameLogic gameLogic;
    private long reset = 0;

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

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        Drawable border = getResources().getDrawable(R.drawable.tile);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                ArrayList<Cell> tiles = gameLogic.getNonEmptyTiles();

                int ri = j;
                int ci = i;

                TextView myText = new TextView(this);

                for(int k = 0; k < tiles.size(); k++) {
                    if(tiles.get(k).column != j && tiles.get(k).row == i) {
                        myText.setText(" ");
                    }
                    else if(tiles.get(k).column == j && tiles.get(k).row == i) {
                        myText.setText("" + tiles.get(k).value);
                        tiles.remove(k);
                    }
                }
                myText.setTextSize(25
                );
                myText.setBackground(border);
                myText.setGravity(Gravity.CENTER_HORIZONTAL);
                myText.setHeight(246);
                myText.setWidth(246);
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
}
