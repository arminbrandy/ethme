package com.arminbrandy.ethme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arminbrandy.ethme.Fragments.PinPadFragment;
import com.arminbrandy.ethme.Fragments.SuccessFragment;
import com.arminbrandy.ethme.Utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PinPadActivity extends AppCompatActivity {

    private final int PINSIZE = 5;
    private boolean isConfirmationMode = false;

    private List<Integer> PIN = new ArrayList<>();
    private List<Integer> PINc = new ArrayList<>();
    private String intent;

    private TextView viewPIN;
    private TextView viewPINConfirmation;
    private TextView viewPINInfo;
    private Button btnReset;
    private View viewSuccess;

    private char emptyCircle;
    private char fullCircle;
    private String emptyPIN;
    private String fullPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_pad);

        PinPadFragment pinPad = new PinPadFragment();

        /*// TODO pinPadFragment Bundle args isExample
        Bundle args = new Bundle();
        String item = getIntent().getStringExtra("wallet");
        args.putString(Intent.EXTRA_TEXT, item);
        pinPad.setArguments(args);*/

        intent = getIntent().getStringExtra("wallet");

        getSupportFragmentManager().beginTransaction().
                replace(R.id.pin_pad_container, pinPad).commit();

        SuccessFragment success = new SuccessFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.pin_pad_layout, success).commit();

        if(savedInstanceState != null){
            isConfirmationMode = savedInstanceState.getBoolean("isC");

            int[] pinR = savedInstanceState.getIntArray("pin");
            if(pinR != null) {
                for (int value : pinR)
                    PIN.add(value);
            }

            int[] pinCR = savedInstanceState.getIntArray("pinC");
            if(pinCR != null) {
                for (int value : pinCR)
                    PINc.add(value);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putIntArray("pin", PIN.stream().mapToInt(i->i).toArray());
        outState.putIntArray("pinC", PINc.stream().mapToInt(i->i).toArray());
        outState.putBoolean("isC", isConfirmationMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        emptyCircle = getString(R.string.pin_pad_empty_cicle).charAt(0);
        fullCircle = getString(R.string.pin_pad_full_cicle).charAt(0);
        emptyPIN = new String(new char[PINSIZE]).replace('\0', emptyCircle);
        fullPIN = new String(new char[PINSIZE]).replace('\0', fullCircle);

        viewPIN = findViewById(R.id.tv_pin);
        viewPINConfirmation = findViewById(R.id.tv_pin_confirmation);
        viewPINInfo = findViewById(R.id.tv_pin_info);
        btnReset = findViewById(R.id.btn_pin_reset);
        viewSuccess = findViewById(R.id.success_layout);
        viewSuccess.setVisibility(View.GONE);

        Button del = findViewById(R.id.btn_pin_del);
        del.setOnClickListener((View v) -> delLastPIN());
        del.setOnLongClickListener((View v) -> delPIN());
        btnReset.setOnClickListener((View v) -> resetPIN(true));

        updateUI();
    }

    public void addPIN(View v){
        int p = Integer.parseInt(v.getTag().toString());
        if(PIN.size() < PINSIZE && p >= 0 && p <= 9){
            PIN.add(p);
            updateViewPIN();
            AndroidUtils.vibrate(this);
        }
        if(PIN.size() == PINSIZE){
            if(!isConfirmationMode){
                switchMode();
            } else {
                if(PINc.equals(PIN)){
                    pinSuccess();
                } else {
                    errorClearPIN(getString(R.string.toast_pin_didnt_match));
                }
            }
        }
    }

    private void updateViewPIN(){
        StringBuilder partialPIN = new StringBuilder();
        for(int i = 0; i < PINSIZE; i++){
            partialPIN.append(PIN.size() <= i ? emptyCircle : fullCircle);
        }
        (isConfirmationMode ? viewPINConfirmation : viewPIN)
                .setText(partialPIN);
    }

    private void updateUI() {
        if(isConfirmationMode){
            viewPINInfo.setText(R.string.pin_creation_tv_info_confirmation);
            viewPIN.setText(fullPIN);
            viewPIN.setTextColor(getColor(R.color.textDarkGrey));
            viewPINConfirmation.setVisibility(View.VISIBLE);
            btnReset.setText(getString(R.string.pin_creation_reset));
        } else {
            viewPINInfo.setText(R.string.pin_creation_tv_info);
            viewPINConfirmation.setText(emptyPIN);
            viewPIN.setTextColor(getColor(R.color.colorPrimaryDark));
            viewPINConfirmation.setVisibility(View.INVISIBLE);
            btnReset.setText(null);
        }
        updateViewPIN();
    }

    private void switchMode(){
        if(!isPINSimpleLinear()){
            isConfirmationMode = true;
            PINc = new ArrayList<>(PIN);
            clearPIN();
        } else {
            errorClearPIN(getString(R.string.toast_pin_isnt_safe));
        }
    }

    public void delLastPIN() {
        if(PIN.size() != 0){
            AndroidUtils.vibrate(getBaseContext());
            PIN.remove(PIN.size()-1);
            updateViewPIN();
        }
    }
    public boolean delPIN() {
        if(PIN.size() != 0){
            AndroidUtils.vibrate(getBaseContext(), 80);
            PIN.clear();
            updateViewPIN();
        }
        return true;
    }

    private void resetPIN(boolean vib) {
        if(isConfirmationMode){
            if(vib)
                AndroidUtils.vibrate(getBaseContext(), 80);
            isConfirmationMode = false;
            PINc.clear();
            clearPIN();
        }
    }

    /* clears PIN and updates View */
    private void clearPIN(){
        PIN.clear();
        updateUI();
    }

    private void errorClearPIN(@Nullable String toastMsg){
        AndroidUtils.vibrate(getBaseContext(), 80);
        clearPIN();
        if (toastMsg != null)
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }


    private void pinSuccess() {
        viewSuccess.setVisibility(View.VISIBLE);
    }

    public void nextActivity(View v){
        String pinS = PIN.stream().map(i -> i.toString()).collect(Collectors.joining());
        Intent next = new Intent(this, InstructionsActivity.class);
        next.putExtra("pin",pinS);
        next.putExtra("wallet",intent);
        startActivity(next);
        resetPIN(false);
    }

    /*
        Checks if PIN consists of simple linear number structures as
        1234, 3333, 8765, ...
     */
    private boolean isPINSimpleLinear(){
        boolean[] isEqualR = new boolean[3];
        for(int offset = -1; offset <= 1; offset++){
            isEqualR[offset+1] = true;
            for(int i = 0; (i+1) < PIN.size() && isEqualR[offset+1]; i++){
                isEqualR[offset+1] = ((PIN.get(i) + offset) == PIN.get(i+1));
            }
        }
        return isEqualR[0] || isEqualR[1] || isEqualR[2];
    }
}
