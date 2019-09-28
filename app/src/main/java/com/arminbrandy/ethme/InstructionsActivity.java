package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


public class InstructionsActivity extends AppCompatActivity {
    private boolean isRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        Switch swRead = findViewById(R.id.swRead);
        swRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isRead) {
                readChange(isRead);
            }
        });
    }

    public void readChange(boolean isRead){
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setEnabled(isRead);
    }

    public void continueWallet(View view) {
        Intent act = new Intent(this, WalletCreationActivity.class);
        act.putExtra("wallet","create");
        startActivity(act);

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setEnabled(isRead);

        Switch swRead = findViewById(R.id.swRead);
        swRead.setChecked(false);
    }
}
