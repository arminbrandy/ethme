package com.arminbrandy.ethme;

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

import androidx.appcompat.app.AppCompatActivity;

import com.arminbrandy.ethme.Fragments.SuccessFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MnemonicConfirmationActivity extends AppCompatActivity {

    private List<String> mWords = new ArrayList<>();
    private List<TextView> tvMnemonics = new ArrayList<>();
    private List<EditText> etMnemonics = new ArrayList<>();
    private Button btnFinish;
    private View successLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic_confirmation);

        btnFinish = findViewById(R.id.btn_mnemonic_confirmation_finish);

        tvMnemonics.add(findViewById(R.id.tv_mnemonic_confirmation_a));
        tvMnemonics.add(findViewById(R.id.tv_mnemonic_confirmation_b));
        tvMnemonics.add(findViewById(R.id.tv_mnemonic_confirmation_c));
        tvMnemonics.add(findViewById(R.id.tv_mnemonic_confirmation_d));

        etMnemonics.add(findViewById(R.id.et_mnemonic_confirmation_a));
        etMnemonics.add(findViewById(R.id.et_mnemonic_confirmation_b));
        etMnemonics.add(findViewById(R.id.et_mnemonic_confirmation_c));
        etMnemonics.add(findViewById(R.id.et_mnemonic_confirmation_d));

        for(EditText et : etMnemonics){
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkEditTextFilled();
                }
            });
            et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        SuccessFragment success = new SuccessFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.mnemonic_confirmation_layout, success).commit();

        selectWords();
    }

    @Override
    protected void onStart() {
        super.onStart();
        successLayout = findViewById(R.id.success_layout);
        successLayout.setVisibility(View.GONE);
        TextView successView = findViewById(R.id.view_success);
        successView.setText(R.string.mnemonic_confirmation_success);
    }

    private void selectWords(){
        String mnemonic = getIntent().getStringExtra("mnemonic");

        String[] mnemonic12 = mnemonic.split(" ");
        List<Integer> selectedNrs = new ArrayList<>();

        List<Integer> twelve = new ArrayList<>(
                Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12)
        );

        Collections.shuffle(twelve);

        for(int i = 0; i < 4; i++){
            selectedNrs.add(twelve.get(i));
        }

        Collections.sort(selectedNrs);

        for(int i = 0; i < 4; i++){
            tvMnemonics.get(i).setText(selectedNrs.get(i)+":");
            mWords.add(mnemonic12[selectedNrs.get(i)-1]);
            // TODO: Remove Debug autofill
                etMnemonics.get(i).setText(mnemonic12[selectedNrs.get(i)-1]);
        }
    }

    private void checkEditTextFilled(){
        boolean isFull = true;
        for(EditText et : etMnemonics) {
            if(et.getText().toString().trim().equals(""))
                isFull = false;
        }
        btnFinish.setEnabled(isFull);
    }

    public void checkWords(View v){
        boolean isEqual = true;
        for(int i = 0; i < 4; i++){
            if(!mWords.get(i).equals(etMnemonics.get(i).getText().toString().toLowerCase().trim())){
                isEqual = false;
                etMnemonics.get(i).setText("");
            }
        }
        if(isEqual)
            walletSuccess();
        else
            Toast.makeText(this,R.string.mnemonic_confirmation_error_nomatch,Toast.LENGTH_LONG).show();
    }

    private void walletSuccess() {
        successLayout.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.GONE);
    }

    public void nextActivity(View v){
        String walletData = getIntent().getStringExtra("walletData");
        Wallet wallet = new Wallet(MainActivity.walletPath);
        wallet.persistWallet(walletData);

        Intent next = new Intent(this, MainActivity.class);
        startActivity(next);

        finishAffinity();
    }
}
