package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.arminbrandy.ethme.Fragments.HomeFragment;
import com.arminbrandy.ethme.Fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    public static String walletPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        walletPath = this.getDataDir().getAbsolutePath();
        //this.getExternalFilesDir(null).getAbsolutePath()

        if(Wallet.getWalletFile(walletPath).exists()){
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.main_container, homeFragment).commit();

        } else {
            WelcomeFragment welcomeFragment = new WelcomeFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.main_container, welcomeFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Wallet.getWalletFile(walletPath).exists()){
            TextView homeHeader = findViewById(R.id.home_header);

            Wallet w = new Wallet(walletPath);
            try{
                String x = "This is your wallet address:\n\n";
                if(w.getAddress() != null){
                    x += w.getAddress();
                }

                homeHeader.setText(x);

            } catch (Wallet.WalletInvalidStateException e){

            }
        }
    }

    public void createWallet(View view) {
        Intent next = new Intent(this, PinPadActivity.class);
        next.putExtra("wallet","create");
        startActivity(next);
    }

    public void restoreWallet(View view) {
        /* TODO Implement Restore functionality
        Intent next = new Intent(this, PinPadActivity.class);
        next.putExtra("wallet","restore");
        startActivity(next);*/
    }

}
