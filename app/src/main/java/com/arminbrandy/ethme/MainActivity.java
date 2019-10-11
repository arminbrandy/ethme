package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static AndroidUtils androidUtils = AndroidUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void createWallet(View view) {
        Intent next = new Intent(this, PinPadActivity.class);
        next.putExtra("wallet","create");
        startActivity(next);
    }

    public void restoreWallet(View view) {
        Intent next = new Intent(this, PinPadActivity.class);
        next.putExtra("wallet","restore");
        startActivity(next);
    }

}
