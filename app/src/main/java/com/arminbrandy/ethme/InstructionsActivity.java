package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class InstructionsActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    CheckBox checkedRead;
    ScrollView scrollView;
    Button btnContinue;
    boolean isRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        linearLayout = findViewById(R.id.LLcheckedRead);
        checkedRead = findViewById(R.id.checkedRead);
        btnContinue = findViewById(R.id.btnContinue);

        checkedRead.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isRead) ->
                readChange(isRead)
        );

        scrollView = findViewById(R.id.instructions_scrollview);
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        scrollViewEndCheck();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        scrollViewEndCheck();
    }
    private void scrollViewEndCheck(){
        if (scrollView.getChildAt(0).getBottom()
                <= (scrollView.getHeight() + scrollView.getScrollY()) + scrollView.getChildAt(0).getBottom() * 0.02) {
            checkedRead.setEnabled(true);
        } else {
            if(!isRead)
                checkedRead.setEnabled(false);
        }
    }

    private void readChange(boolean _isRead){
        isRead =_isRead;
        btnContinue.setEnabled(isRead);
        linearLayout.setBackgroundColor(getColor(isRead ? R.color.colorAccentTint : R.color.colorGreyTint));
        scrollViewEndCheck();
    }

    public void continueWallet(View view) {
        Intent next = new Intent();
        String intent = getIntent().getStringExtra("wallet");

        switch(intent) {
            case "create":
                next = new Intent(this, WalletCreationActivity.class);
                break;
            case "restore":
                next = new Intent(this, MnemonicRestoreActivity.class);
                break;
        }
        next.putExtra("pin",
                getIntent().getStringExtra("pin")
        );
        startActivity(next);

        scrollView.scrollTo(0,0);
        btnContinue.setEnabled(false);
        checkedRead.setChecked(false);
    }
}
