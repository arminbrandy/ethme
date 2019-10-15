package com.arminbrandy.ethme;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Bip44WalletUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Wallet {
    private static final String LOG_TAG = Wallet.class.getCanonicalName();
    private Context mC;
    private final String password;
    private String walletPath;
    private File walletDir;
    private String address;
    private String walletName;
    private String mnemonic;

    public Wallet(Context c, String pw){

        password = pw;

        mC = c;
        walletPath = mC.getFilesDir().getAbsolutePath();

        walletDir = new File(walletPath);

        createWallet();

        try{
            address = getWalletCredentials().getAddress();
            Log.d(LOG_TAG, address);
            Log.d(LOG_TAG, password);
            Log.d(LOG_TAG, walletName);
            Log.d(LOG_TAG, mnemonic);
            Log.d(LOG_TAG, "WORKED EVERYTHING");
        } catch (Exception e){
            Log.e(LOG_TAG, "Error getting wallet file credentials");
        }

//
//
//
//        String nodeUrl = getNodeUrl(1);
//
//        Web3j web3 = Web3j.build(new HttpService(nodeUrl));
//        try {
//                Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
//
//                if(!web3ClientVersion.hasError()){
//
//                    System.out.println(walletPath);
//                    System.out.println(walletDir);
//                    walletDir = mC.getFileStreamPath("wallet");
//                    //walletDir = new File("./");
//
//                    String filename = "keystore";
//                    String fileContents = "Hello world!";
//                    FileOutputStream outputStream;
//                   // File keystoreFile = new File(mC.getFilesDir(),"keystore");
//                    File keystoreFile = mC.getFilesDir();
//                    keystoreFile = mC.getFilesDir();
//                   // keystoreFile = mC.getExternalFilesDir();
//                    try {
//
//                            // CREATE WALLET
//                        final Bip39Wallet wallet = Bip44WalletUtils.generateBip44Wallet(this.password, keystoreFile);
//                        wallet.toString();
//                        wallet.getFilename();
//                        System.out.println(wallet.toString());
//                        final File file = new File(keystoreFile, wallet.getFilename());
//                        if (!file.exists()) throw new IOException("No file created");
//                        //saveWallet(wallet, mApp.getPrefs());
//                        //return wallet;
//
//
//                        /*
//                            DO PREFERENCES
//                        SharedPreferences sharedPreferences =
//                                PreferenceManager.getDefaultSharedPreferences(this);
//
//                        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
//                        */
//
//
//                        /*
//                            READ WALLET FILE
//                        keystoreFile.getAbsolutePath().toString();
//                        boolean a = keystoreFile.isFile();
//                        boolean b = keystoreFile.isDirectory();
//                        File[] x = keystoreFile.listFiles();
//                        for (File f : x){
//                            System.out.println(f.getAbsolutePath());
//                            Credentials crs = Bip44WalletUtils.loadCredentials(this.password, f);
//                            String addy = crs.getAddress();
//                            System.out.println(addy);
//                        }*/
//
//                        String addy = "";
//                        System.out.println("\n\n\n\n\nSTAST\\\n\n\n\n");
//                        for(int i = 0; i < 100; i++){
//                            //addy = testing();
//                            System.out.println(i);
//                            i = addy.contains("abba") ? 100 : i;
//                        }
//                        System.out.println(addy);
//
//
///*
//                        String fileName = WalletUtils.generateNewWalletFile(
//                                password,
//                                keystoreFile);*/
//
//                       /* String seed = fileName.toString();
//                        //String seed = "fake news";
//                        fileWriter.write(seed);
//                        fileWriter.close();*/
//                        System.out.println("WALLET CREATED");
//
//                        /*
//                            web3j create walletfile
//                        String fileName = WalletUtils.generateNewWalletFile(
//                                "your password",
//                                new File("/path/to/destination"));
//                        */
//
//                        /*
//                              Proofing FIle write to Data Dir
//                        //String seed = WalletUtils.generateBip39Wallet(password,walletDir).toString();
//                        //OutputStream os = x.
//                        String fileContent = "satoshi bitcoin santos banana words crypto explosion infinite value gold digital moon";
//
//                        FileWriter fileWriter = new FileWriter(x);
//                        fileWriter.write(fileContent);
//                        fileWriter.close();
//
//                        */
//
//                        /*
//                            Reading Data File
//                        BufferedReader br = new BufferedReader(new FileReader(x));
//                        String st;
//                        while ((st = br.readLine()) != null) {
//                            System.out.println(st);
//                        }
//                         */
//                    } /*catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (CipherException e) {
//                        e.printStackTrace();
//                    }*/
//                    finally {
//
//                    }
////                    WalletUtils.generateFullNewWalletFile(password,walletDir);
//
//                    String clientVersion = web3ClientVersion.getWeb3ClientVersion();
//                    //Subscription subscription = web3.blockFlowable(false).subscribe(block -> {});
//                    System.out.println("WE ARE ETHEREUM!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                    //System.out.println(web3.ethBlockNumber().flowable());
//                    System.out.println(clientVersion);
//                    //Connected
//                }
//                else {
//                    //Show Error
//                }
//            }
//        catch (Exception e) {
//            //Show Error
//        }
    }


    public String getAddress(){
        return address;
    }

    // TODO Cipher Exception INvalid password is our case for wrong pin
    private Credentials getWalletCredentials(){
        try{
            File f = new File(walletDir + "/" + walletName);
            System.out.println(f.getAbsolutePath());
            if(f.exists()){
                return Bip44WalletUtils.loadCredentials(password, f);//(password+"hihi", f);
            } else {
                Log.d(LOG_TAG,"No such walletfile found");
            }
        } catch (CipherException e){
            Log.w(LOG_TAG,"No such walletfile found", e);
        } catch (IOException e){
            Log.w(LOG_TAG,"Could not read Walletfile", e);
        }
        return null;
    }
    /*
        @network:
            0: mainnet
            1: rinkeby
            2: goerli
            3: ropsten
            4: kovan
     */
    private String getNodeUrl(int network){
        String[] networks = {"mainnet", "rinkeby", "goerli", "ropsten", "kovan"};
        try {
            String json = loadJSONFromAsset("node.json");
            if(json != null && network >= 0 && network < networks.length){
                JSONObject obj = new JSONObject(json);
                String pID = obj.getString("projectID");
                String net = obj.getString(networks[network]);
                return "https://".concat(net.concat(pID));
            }
            return null;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    private void createWallet(){
        try {
            // CREATE WALLET
            final Bip39Wallet wallet = Bip44WalletUtils.generateBip44Wallet(password, walletDir);
            mnemonic = wallet.getMnemonic();
            walletName = wallet.getFilename();
            final File file = new File(walletDir, wallet.getFilename());
            if (!file.exists()) throw new IOException("No file created");


        } catch (Exception e) {
            //Show Error
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = mC.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
