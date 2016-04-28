package edu.gvsu.cis.radeckia.python;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ResultsScreen extends AppCompatActivity implements View.OnClickListener {

    private Button return_button;
    private int is_game_win = 0;
    private int is_win;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        return_button = (Button) findViewById(R.id.return_button);
        Intent intent = new Intent();
        intent.getIntExtra("winner", is_win);
        setSupportActionBar(toolbar);

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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.return_button:
                if(is_game_win > 0) {
                    Intent winCounter = new Intent();
                    winCounter.putExtra("winCounter", is_game_win);
                    setResult(RESULT_OK, winCounter);
                    finish();
                }
        }
    }
}
