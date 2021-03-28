package com.example.memorina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    TilesView view;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.view);
        button = findViewById(R.id.button);
//        button.setVisibility(View.INVISIBLE);
    }

    public void onNewGameClick(View v) {
        view.newGame(); // запустить игру заново
        // very useful comment
//        button.setVisibility(View.INVISIBLE);
    }
}