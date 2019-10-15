package com.arminbrandy.ethme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;

public class WalletCreationActivity extends AppCompatActivity {
    private static final String LOG_TAG = WalletCreationActivity.class.getCanonicalName();
    private byte[] signature_pw;
    private TextView viewCreateWalletTxt;

    //private KeyguardManager mKeyguardManager;
    //private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_creation);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        viewCreateWalletTxt = findViewById(R.id.txtCreateWallet);
        String pin = getIntent().getStringExtra("pin");

        // TODO move signing task seperate
        try{
            String walletPINKeyAlias = "keys_wallet_pw";
            byte[] data = pin.getBytes(StandardCharsets.UTF_8);


            /*
             * Load the Android KeyStore instance using the
             * "AndroidKeyStore" provider to list out what entries are
             * currently stored.
             */
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            boolean isAli = ks.containsAlias(walletPINKeyAlias);
            Enumeration<String> aliases = ks.aliases();

            /*
             * Use a PrivateKey in the KeyStore to create a signature over
             * some data.
             */

            KeyStore.Entry entry = ks.getEntry(walletPINKeyAlias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(LOG_TAG, "Not an instance of a PrivateKeyEntry");

                /*
                 * Generate a new EC key pair entry in the Android Keystore by
                 * using the KeyPairGenerator API. The private key can only be
                 * used for signing or verification and only with SHA-256 or
                 * SHA-512 as the message digest.
                 */
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                kpg.initialize(new KeyGenParameterSpec.Builder
                        (walletPINKeyAlias,
                                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .build());

                KeyPair kp = kpg.generateKeyPair();
            }

            ks.load(null);
            entry = ks.getEntry(walletPINKeyAlias, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {

            }
            //Signature s2 = Signature.getInstance("SHA256withECDSA");
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
            s.update(data);
            signature_pw = s.sign();
            String signature_pw64 = new String(Base64.getEncoder().encode(signature_pw));

            Wallet myNewWallet = new Wallet(this, signature_pw64);
            viewCreateWalletTxt.setText(myNewWallet.getAddress());

        } catch (NoSuchAlgorithmException | NoSuchProviderException e){
            Log.w(LOG_TAG, "Error creating KeyPairGenerator", e);
        } catch (InvalidAlgorithmParameterException e){
            Log.w(LOG_TAG, "Error generating key", e);
        } catch (KeyStoreException e){
            Log.w(LOG_TAG, "KeyStoreException", e);
        } catch (IOException | CertificateException e){
            Log.w(LOG_TAG, "Error loading KeyStore aliases", e);
        } catch (UnrecoverableEntryException e){
            Log.w(LOG_TAG, "KeyStore entry is no more recoverable", e);
        } catch (InvalidKeyException e){
            Log.w(LOG_TAG, "Key doesn't support cryptography", e);
        } catch (SignatureException e){
            Log.w(LOG_TAG, "Signing data failed", e);
        }

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
