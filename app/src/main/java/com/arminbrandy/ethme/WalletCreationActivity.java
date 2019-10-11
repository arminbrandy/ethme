package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class WalletCreationActivity extends AppCompatActivity {
    //private KeyguardManager mKeyguardManager;
    //private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_creation);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        // TODO All wallet and crypto stuff

        //this.finish();
        //finishActivity(0);


        /* NOTES
        get Real user auth
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }*/
    }

}
