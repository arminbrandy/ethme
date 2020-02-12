package com.arminbrandy.ethme;

import android.util.Log;

import com.arminbrandy.ethme.Utils.CipherUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip44WalletUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.MnemonicUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private static final String LOG_TAG = Wallet.class.getCanonicalName();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String WALLET_FILE_NAME = "wallet";
    private static final String AKS_ALIAS_PIN = "wallet_pin_signer";
    private static final String AKS_ALIAS_DATA = "wallet_data_encryption";
    private File mWalletFile;
    private WalletData mWalletData;
    private int mAddressIndex = 0;
    private boolean isLoaded = false;

    public Wallet(String dir){
        mWalletFile = getWalletFile(dir);
    }

    public String create(String pin){
        return generate(pin, null);
    }

    public void restore(String pin, String mnemonic){
        generate(pin, mnemonic);
    }

    private String generate(String pin, String mnemonic){
        if(mWalletFile.exists()) {
            Log.d(LOG_TAG, "Cannot overwrite existing wallet file");
            return null;
        }

        if(mnemonic == null)
            mnemonic = CipherUtils.generateMnemonic();

        // addresses List to add HD addresses anytime later on
        mAddressIndex = 0;
        List<String> addresses = new ArrayList<>();
        addresses.add(deriveCredentials(mnemonic, mAddressIndex).getAddress());

        // Iterating hashes the PW for easy verification to users PIN input later on
        String PW = signedPIN(pin);
        String hash = CipherUtils.digestPW(PW);

        // encrypting mnemonic seed with PW
        byte[] salt = CipherUtils.generateSalt();
        byte[] iv = CipherUtils.generateIv();
        byte[] cipher = CipherUtils.encryptWithPW(PW.toCharArray(), salt, iv, mnemonic);

        if(cipher != null) {
            mWalletData = new WalletData(salt, iv, cipher, hash, addresses);
            return mnemonic;
        } else {
            Log.d(LOG_TAG, "Error during mnemonic encryption");
            return null;
        }
    }

    public boolean persistWallet(String data){
        try{
            return persistWallet(objectMapper.readValue(data, WalletData.class));
        } catch (IOException e) {
            Log.w(LOG_TAG, "IO Problem - No wallet file created", e);
            return false;
        }
    }

    public boolean persistWallet(WalletData data){
        if(mWalletFile.exists()) {
            Log.d(LOG_TAG, "Cannot overwrite existing wallet file");
            return false;
        }
        mWalletData = data;
        return persistWallet();
    }

    public boolean persistWallet(){
        try{
            // encrypt total WalletData, inclusive addresses for higher privacy / security reasons
            // using an AndroidKeyStore symmetric key this time
            byte[][] encryptedData = CipherUtils.encryptWithAndoridKS(
                    AKS_ALIAS_DATA,
                    objectMapper.writeValueAsString(mWalletData)
            );

            // store encrypted WalletData as simple (IV, data) pair
            if(encryptedData != null){
                FileWriter fileWriter = new FileWriter(mWalletFile);
                fileWriter.write(objectMapper.writeValueAsString(encryptedData));
                fileWriter.close();

                isLoaded = true;
                return true;
            } else {
                Log.d(LOG_TAG, "Wallet couldn't be created, error during data encryption");
                return false;
            }
        } catch (IOException e) {
            Log.w(LOG_TAG, "IO Problem - No wallet file created", e);
            return false;
        }
    }

    private boolean load(){
        try {
            if(!mWalletFile.exists()) {
                Log.d(LOG_TAG, "No wallet file exists");
                return false;
            }

            String walletDataJson = CipherUtils.decryptWithAndoridKS(
                    AKS_ALIAS_DATA,
                    objectMapper.readValue(mWalletFile, byte[][].class)
            );

            if(walletDataJson == null){
                Log.d(LOG_TAG, "Wallet file decryption error");
                return false;
            }

            mWalletData = objectMapper.readValue(walletDataJson, WalletData.class);
            isLoaded = true;
            return true;
        } catch (IOException e) {
            Log.w(LOG_TAG, "IO problem with wallet file", e);
            return false;
        }
    }

    private boolean verifyPin(String pin)
            throws WalletInvalidStateException {
        if(!isLoaded && !load()){
            Log.w(LOG_TAG, "Problem loading wallet file data");
            throw new WalletInvalidStateException();
        }

        if(mWalletData.getHash().equals(CipherUtils.digestPW(signedPIN(pin))))
            return true;

        Log.d(LOG_TAG, "Wrong PIN");
        return false;
    }

    private String signedPIN(String pin){
        return CipherUtils.bytesToBase64(
                CipherUtils.signWithAndoridKS(AKS_ALIAS_PIN, pin)
        );
    }

    private String decryptMnemonic(String pin){
        return CipherUtils.decryptWithPW(
                signedPIN(pin).toCharArray(),
                mWalletData.getSalt(),
                mWalletData.getIv(),
                mWalletData.getCipher());
    }

    private Credentials deriveCredentials(String mnemonic, int index){
        Bip32ECKeyPair masterEcKeyPair =
                (Bip32ECKeyPair) Bip44WalletUtils.loadBip44Credentials("", mnemonic).getEcKeyPair();

        // m/44'/60'/0'/0/index
        return Credentials.create(Bip32ECKeyPair.deriveKeyPair(
                masterEcKeyPair,
                new int[]{index})
        );
    }

    protected ECDSASignature sign(byte[] txHash, String pin)
            throws WalletInvalidStateException {
        return sign(txHash, mAddressIndex, pin);
    }
    protected ECDSASignature sign(byte[] txHash, int index, String pin)
        throws WalletInvalidStateException {
        return (verifyPin(pin)) ?
                deriveCredentials(decryptMnemonic(pin), index).getEcKeyPair().sign(txHash) :
                null;
    }

    protected String getWalletDataAsString(){
        try {
            return objectMapper.writeValueAsString(mWalletData);
        } catch (JsonProcessingException e){
            Log.w(LOG_TAG, "Error parsing walletData to String",e);
            return null;
        }
    }

    protected String getAddress()
            throws WalletInvalidStateException {
        return getAddress(mAddressIndex);
    }
    protected String getAddress(int index)
            throws WalletInvalidStateException {
        if(!isLoaded && !load())
            throw new WalletInvalidStateException();
        return (mWalletData.getAddresses().size() > index) ?
                mWalletData.getAddresses().get(index) : null;
    }

    public void setAddressIndex(int index){
        mAddressIndex = index;
    }

    public boolean generateAddress(int index, String pin)
            throws WalletInvalidStateException {
        if(getAddress(index) != null)
            return true;
        if(verifyPin(pin)){
            mWalletData.getAddresses().add(
                    deriveCredentials(decryptMnemonic(pin), index).getAddress()
            );
            return persistWallet();
        }
        return false;
    }

    static File getWalletFile(String dir){
        return new File(dir, WALLET_FILE_NAME);
    }

    protected File getWalletFile(){
        return mWalletFile;
    }

    protected boolean deleteWalletFile(){
        if(!mWalletFile.exists()) return false;

        return mWalletFile.delete();
    }

    public boolean isLoaded(){
        return isLoaded;
    }

    public static class WalletInvalidStateException extends Exception {
        public WalletInvalidStateException(String errorMessage) {
            super(errorMessage);
        }
        public WalletInvalidStateException() {
            this("Wallet needs to have Wallet.isLoaded() set to true for interaction");
        }
    }
}
