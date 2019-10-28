package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WalletCreationActivity extends AppCompatActivity {
    private static final String LOG_TAG = WalletCreationActivity.class.getCanonicalName();
    private TextView tvCreationHeader;
    private TextView tvCreationInfo;
    private List<TextView> tvsMnemonic = new ArrayList<>();
    private ProgressBar pbCreation;
    private GridLayout glMnemonic;
    private Button btnContinue;
    private Wallet mWallet;
    private String mMnemonic;

    //private KeyguardManager mKeyguardManager;
    //private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_creation);
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        tvCreationHeader = findViewById(R.id.tv_wallet_creation_header);
        tvCreationInfo = findViewById(R.id.tv_wallet_creation_info);
        pbCreation = findViewById(R.id.pb_wallet_creation);
        glMnemonic = findViewById(R.id.gl_wallet_creation_mnemonic);
        btnContinue = findViewById(R.id.btn_wallet_creation_continue);
        asignTvsMnemonic();

        String pin = getIntent().getStringExtra("pin");
        newWallet(pin);

        /* NOTES
        get Real user auth
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }*/
    }

    protected void onDestroy(){
        super.onDestroy();
        btnContinue.setTag(null);
    }

    private void asignTvsMnemonic() {
        for(int i = 1; i <= 12; i++){
            tvsMnemonic.add(
                    findViewById(this.getResources().
                        getIdentifier(
                                "tv_wallet_creation_mnemonic_".concat(Integer.toString(i)),
                                "id",
                                this.getPackageName()
                        ))
            );
        }
    }
    private void insertMnemonic(String mnemonic){
        mMnemonic = mnemonic;
        try{
            String[] mnemonic12 = mnemonic.split(" ");
            if(mnemonic12.length != 12)
                throw new Wallet.WalletInvalidStateException("Mnemonic seed is invalid");
            for(int i = 0; i < 12; i++){
                tvsMnemonic.get(i).setText("" + (i+1) + ". " + mnemonic12[i]);
            }
        } catch (Wallet.WalletInvalidStateException e){
            Log.w(LOG_TAG, "No valid mnemonic found", e);
        }
    }

    public void nextActivity(View v){
        Intent next = new Intent(this, MnemonicConfirmationActivity.class);
        next.putExtra("mnemonic", mMnemonic);
        next.putExtra("walletData", mWallet.getWalletDataAsString());
        startActivity(next);
    }

    private void countdownContinue(String uuid){
        new AsyncTask<String, Integer, Integer>() {
            int wait = 30;
            int milliseconds = 1000;
            String uuid;
            protected Integer doInBackground(String... args) {
                uuid = args[0];
                for(int i = wait; i > 0; i--){
                    publishProgress(i);
                    try {
                         Thread.sleep(milliseconds);
                     } catch (InterruptedException ex){}
                }
                return wait;
            }

            protected void onProgressUpdate(Integer... progress) {
                super.onProgressUpdate(progress);
                if(btnContinue.getTag() != null && btnContinue.getTag().equals(uuid)){
                    btnContinue.setText(getString(R.string.btn_continue) + " (" + progress[0] + ")");
                } else {
                    milliseconds = 0;
                }
            }

            protected void onPreExecute() {
                super.onPreExecute();
                btnContinue.setEnabled(false);
                btnContinue.setText(getString(R.string.btn_continue) + " (" + wait + ")");
            }

            protected void onPostExecute(Integer wait) {
                super.onPostExecute(wait);
                if(btnContinue.getTag() != null && btnContinue.getTag().equals(uuid)){
                    btnContinue.setEnabled(true);
                    btnContinue.setText(getString(R.string.btn_continue));
                }
            }
        }.execute(uuid);
    }

    private void newWallet(String pin){
        new AsyncTask<String, Void, String>() {
            String uuid = UUID.randomUUID().toString();
            @Override
            protected String doInBackground(String... args) {
                mWallet = new Wallet(args[0]);
                return mWallet.create(args[1]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tvCreationHeader.setText("creating wallet...");
                glMnemonic.setVisibility(View.INVISIBLE);
                pbCreation.setVisibility(View.VISIBLE);
                tvCreationHeader.setText(getString(R.string.wallet_creation_header));
                btnContinue.setTag(uuid);
            }

            @Override
            protected void onPostExecute(String mnemonic) {
                super.onPostExecute(mnemonic);
                if(mnemonic != null) {
                    insertMnemonic(mnemonic);
                    tvCreationHeader.setText(getString(R.string.wallet_creation_header_fin));
                    glMnemonic.setVisibility(View.VISIBLE);
                    pbCreation.setVisibility(View.INVISIBLE);

                    if(btnContinue.getTag() != null && btnContinue.getTag().equals(uuid))
                        countdownContinue(uuid);
                } else {
                    // TODO - check if wallet is considered as saved or before creation
                    tvCreationHeader.setText(getString(R.string.wallet_creation_header_error_file_exists));
                    tvCreationInfo.setText(getString(R.string.wallet_creation_info_error_file_exists));
                    pbCreation.setVisibility(View.INVISIBLE);
                    glMnemonic.setVisibility(View.GONE);
                    btnContinue.setVisibility(View.INVISIBLE);
                }
            }
        }.execute(MainActivity.walletPath, pin);
    }
}
