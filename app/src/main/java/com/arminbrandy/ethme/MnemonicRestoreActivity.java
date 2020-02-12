package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arminbrandy.ethme.Fragments.SuccessFragment;
import com.arminbrandy.ethme.Utils.CipherUtils;

import java.util.ArrayList;
import java.util.List;

public class MnemonicRestoreActivity extends AppCompatActivity {

    private int mnemonicLength = 12;
    private List<String> mWords = new ArrayList<>();

    private List<EditText> etsMnemonic = new ArrayList<>();
    private List<TextView> tvsMnemonic = new ArrayList<>();

    private Button btnLess;
    private Button btnMore;
    private Button btnFinish;
    private View successLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic_restore);

        btnLess = findViewById(R.id.btn_mnemonic_restore_less);
        btnMore = findViewById(R.id.btn_mnemonic_restore_more);
        btnFinish = findViewById(R.id.btn_mnemonic_restore_finish);

        asignMnemonicViews();

        for(EditText et : etsMnemonic){
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    btnFinish.setEnabled(isAllFilled());
                }
            });
            et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }


        SuccessFragment success = new SuccessFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.mnemonic_restore_layout, success).commit();

    }

    private void asignMnemonicViews() {
        for(int i = 1; i <= 24; i++){
            etsMnemonic.add(
                findViewById(this.getResources().
                    getIdentifier(
                        "et_mnemonic_restore_".concat(Integer.toString(i)),
                        "id",
                        this.getPackageName()
                    ))
            );
            tvsMnemonic.add(
                findViewById(this.getResources().
                    getIdentifier(
                        "tv_mnemonic_restore_".concat(Integer.toString(i)),
                        "id",
                        this.getPackageName()
                    ))
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        successLayout = findViewById(R.id.success_layout);
        successLayout.setVisibility(View.GONE);
        TextView successView = findViewById(R.id.view_success);
        successView.setText(R.string.mnemonic_restore_success);
    }

    public void showMore(View v){
        if (mnemonicLength < 24)
            mnemonicLength += 3;
        displayEtsMnemonic();
    }
    public void showLess(View v){
        if (mnemonicLength > 12)
            mnemonicLength -= 3;
        displayEtsMnemonic();
    }

    private void displayEtsMnemonic(){
        for (int i = 12; i < etsMnemonic.size(); i++){
            etsMnemonic.get(i).setVisibility((i < mnemonicLength) ? View.VISIBLE : View.GONE);
            tvsMnemonic.get(i).setVisibility((i < mnemonicLength) ? View.VISIBLE : View.GONE);
        }
        btnLess.setEnabled(mnemonicLength > 12);
        btnMore.setEnabled(mnemonicLength < 24);
        btnFinish.setEnabled(isAllFilled());
    }

    private boolean isAllFilled(){
        for(int i = 0; i < mnemonicLength; i++)
            if(etsMnemonic.get(i).getText().toString().toLowerCase().trim().equals(""))
                return false;
        return true;
    }

    private boolean loadWords(){
        if (!isAllFilled())
            return false;

        mWords.clear();
        for(int i = 0; i < mnemonicLength; i++)
            mWords.add(etsMnemonic.get(i).getText().toString().toLowerCase().trim());

        return true;
    }

    public void checkWords(View v){
        if(loadWords() && CipherUtils.validateMnemonic(String.join(" ", mWords)))
            walletSuccess();
        else
            Toast.makeText(this,R.string.mnemonic_restore_error_invalid_words,Toast.LENGTH_LONG).show();
    }

    private void walletSuccess() {
        Wallet wallet = new Wallet(MainActivity.walletPath);
        wallet.restore(getIntent().getStringExtra("pin"), String.join(" ", mWords));
        wallet.persistWallet();

        successLayout.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.GONE);
    }

    public void nextActivity(View v){
        Intent next = new Intent(this, MainActivity.class);
        startActivity(next);
        finishAffinity();
    }
}
