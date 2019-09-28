package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void createWallet(View view) {
        Intent act = new Intent(this, InstructionsActivity.class);
        act.putExtra("wallet","create");
        startActivity(act);
    }

    public void restoreWallet(View view) {
        Intent act = new Intent(this, InstructionsActivity.class);
        act.putExtra("wallet","restore");
        startActivity(act);
    }
}
