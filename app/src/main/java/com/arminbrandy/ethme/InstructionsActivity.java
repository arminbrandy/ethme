package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class InstructionsActivity extends AppCompatActivity {
    CheckBox checkedRead;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        checkedRead = findViewById(R.id.checkedRead);
        btnContinue = findViewById(R.id.btnContinue);

        checkedRead.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isRead) ->
                readChange(isRead)
        );
    }

    public void readChange(boolean isRead){
        btnContinue.setEnabled(isRead);
        btnContinue.setBackgroundTintList(null);
        btnContinue.setBackgroundColor(isRead ?
                getColor(R.color.colorPrimaryDark) :
                getColor(R.color.btnDisabled)
        );
    }

    public void continueWallet(View view) {
        Intent next = new Intent(this, WalletCreationActivity.class);
        next.putExtra("pin",
                getIntent().getIntArrayExtra("pin")
        );
        startActivity(next);

        btnContinue.setEnabled(false);
        checkedRead.setChecked(false);
    }
}
