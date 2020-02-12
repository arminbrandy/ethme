package com.arminbrandy.ethme.Utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.NonNull;

import org.web3j.crypto.MnemonicUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.arminbrandy.ethme.Utils.SecureRandomUtils.generateRandomBytes;

public final class CipherUtils {
    private static final String LOG_TAG = CipherUtils.class.getCanonicalName();

    public static final int MNEMONIC_LENGTH = 16;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    // signWithAndoridKS
    private static final String SIGNING_ALGORITHM_AKS = "SHA512withRSA";

    // encryptWithAndoridKS
    private static final String CIPHER_ALGORITHM_AKS = "AES/GCM/NoPadding";

    // encryptWithPW
    private static final int SALT_LENGTH = 64;
    private static final int IV_LENGTH = 16;
    private static final int ITERATION_COUNT = 2048;
    private static final int KEY_LENGTH = 256;
    private static final String CIPHER_ALGORITHM_PW = "AES/CBC/PKCS7Padding";
    private static final String PBE_ALGORITHM = "PBKDF2WithHmacSHA512";


    private static PrivateKey generateKeyPairAKS(String alias)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        Log.w(LOG_TAG, alias + " is not an instance of a PrivateKeyEntry");

        KeyPairGenerator kpg = KeyPairGenerator.
                getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);

        kpg.initialize(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .build());

        return kpg.generateKeyPair().getPrivate();
    }

    private static SecretKey generateSecretKeyAKS(String alias)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        Log.w(LOG_TAG, alias + " is not an instance of a SecretKeyEntry");

        KeyGenerator kg = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        kg.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        );

        return kg.generateKey();
    }

    public static byte[] signWithAndoridKS(@NonNull String alias, String data){
        return signWithAndoridKS(alias, stringToBytes(data));
    }
    public static byte[] signWithAndoridKS(@NonNull String alias, byte[] data){
        try{
            KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);

            PrivateKey privateKey = (entry instanceof KeyStore.PrivateKeyEntry) ?
                    ((KeyStore.PrivateKeyEntry) entry).getPrivateKey() :
                    generateKeyPairAKS(alias);

            Signature s = Signature.getInstance(SIGNING_ALGORITHM_AKS);
            s.initSign(privateKey);
            s.update(data);

            return s.sign();

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
        return null;
    }

    private static byte[][] cryptWithAndoridKS(@NonNull String alias, byte[] data, byte[] iv, int mode){
        try {
            KeyStore ks = KeyStore.getInstance(ANDROID_KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(alias, null);

            if(!(entry instanceof KeyStore.SecretKeyEntry) && mode != Cipher.ENCRYPT_MODE)
                return null;

            SecretKey secretKey = (entry instanceof KeyStore.SecretKeyEntry) ?
                    ((KeyStore.SecretKeyEntry) entry).getSecretKey() :
                    generateSecretKeyAKS(alias);

            final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AKS);

            if(mode == Cipher.ENCRYPT_MODE)
                cipher.init(mode, secretKey);
            else
                cipher.init(mode, secretKey, new GCMParameterSpec(128, iv));

            return new byte[][]{
                    cipher.getIV(),
                    cipher.doFinal(data)
            };
        } catch (NoSuchProviderException |
                InvalidAlgorithmParameterException |
                KeyStoreException |
                CertificateException |
                IOException |
                UnrecoverableEntryException e){
            Log.w(LOG_TAG, "Something went wrong using AndroidKeystore", e);
        } catch (NoSuchPaddingException |
                NoSuchAlgorithmException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e){
            Log.w(LOG_TAG, "Something went wrong during " +
                            ((mode == Cipher.ENCRYPT_MODE) ?
                                    "encryption" :
                                    ((mode == Cipher.DECRYPT_MODE) ?
                                            "decryption" :
                                            "action: Cipher.MODE = " + mode)),
                    e);
        }
        return null;
    }
    public static byte[][] encryptWithAndoridKS(@NonNull String alias, String data){
        return cryptWithAndoridKS(alias, stringToBytes(data), null, Cipher.ENCRYPT_MODE);
    }
    public static String decryptWithAndoridKS(@NonNull String alias, byte[] iv, byte[] data) {
        byte[][] decrypted = cryptWithAndoridKS(alias, data, iv, Cipher.DECRYPT_MODE);
        return (decrypted != null) ? bytesToString(decrypted[1]) : null;
    }
    public static String decryptWithAndoridKS(@NonNull String alias, byte[][] ivDataPair) {
        return decryptWithAndoridKS(alias, ivDataPair[0], ivDataPair[1]);
    }

    private static byte[] cryptWithPW(char[] pw, byte[] salt, byte[] iv, byte[] data, int mode){
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(pw, salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKey pbeKey = SecretKeyFactory.getInstance(PBE_ALGORITHM).generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(pbeKey.getEncoded(), KeyProperties.KEY_ALGORITHM_AES);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_PW);
            cipher.init(mode, secretKeySpec, new IvParameterSpec(iv));

            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                InvalidKeySpecException |
                InvalidAlgorithmParameterException |
                BadPaddingException |
                IllegalBlockSizeException e){
            Log.w(LOG_TAG, "Something went wrong during " +
                    ((mode == Cipher.ENCRYPT_MODE) ?
                            "encryption" :
                            ((mode == Cipher.DECRYPT_MODE) ?
                                    "decryption" :
                                    "action: Cipher.MODE = " + mode)),
                    e);
        }
        return null;
    }

    public static byte[] encryptWithPW(char[] pw, byte[] salt, byte[] iv, String data)
    {
        return cryptWithPW(pw, salt, iv, stringToBytes(data), Cipher.ENCRYPT_MODE);
    }

    public static String decryptWithPW(char[] pw, byte[] salt, byte[] iv, byte[] data){
        return bytesToString(cryptWithPW(pw, salt, iv, data, Cipher.DECRYPT_MODE));
    }

    public static byte[] generateSalt(){
        return generateRandomBytes(SALT_LENGTH);
    }

    public static byte[] generateIv(){
        return generateRandomBytes(IV_LENGTH);
    }

    public static String generateMnemonic(){
        return MnemonicUtils.generateMnemonic(
                generateRandomBytes(MNEMONIC_LENGTH)
        );
    }

    public static boolean validateMnemonic(String mnemonic){
        return MnemonicUtils.validateMnemonic(mnemonic);
    }

    private static String digestHash(String data, String mode){
        try {
            MessageDigest msdDigest = MessageDigest.getInstance(mode);
            msdDigest.update(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
            return bytesToBase64(msdDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            Log.w(LOG_TAG, "No such Algorithm ", e);
        }
        return null;
    }

    public static String digestSha1(String data) {
        return digestHash(data, KeyProperties.DIGEST_SHA1);
    }

    public static String digestSha256(String data) {
        return digestHash(data, KeyProperties.DIGEST_SHA256);
    }

    public static String digestSha512(String data) {
        return digestHash(data, KeyProperties.DIGEST_SHA512);
    }

    public static String digestPW(String hash){
        for(int i = 0; i < 1024; i++) {
            hash = digestSha1(digestSha512(hash));
        }
        return hash;
    }

    private static byte[] stringToBytes(String data){
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] data){
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String bytesToBase64(byte[] data){
        return new String(Base64.getEncoder().encode(data));
    }

    public static byte[] base64ToBytes(String data){
        return Base64.getDecoder().decode(data);
    }
}
