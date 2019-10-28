package com.arminbrandy.ethme;

import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "salt",
        "iv",
        "cipher",
        "hash",
        "addresses"
})
public class WalletData {
    @JsonProperty("salt")
    private String salt;
    @JsonProperty("iv")
    private String iv;
    @JsonProperty("cipher")
    private String cipher;
    @JsonProperty("hash")
    private String hash;
    @JsonProperty("addresses")
    private List<String> addresses;

    public WalletData(){}

    public WalletData(byte[] _salt, byte[] _iv, byte[] _cipher, String _hash, List<String> _addresses){
        salt = new String(Base64.getEncoder().encode(_salt));
        iv = new String(Base64.getEncoder().encode(_iv));
        cipher = new String(Base64.getEncoder().encode(_cipher));
        hash = _hash;
        addresses = _addresses;
    }

    @JsonGetter("salt")
    public byte[] getSalt(){
        return Base64.getDecoder().decode(salt);
    }

    @JsonGetter("iv")
    public byte[] getIv(){
        return Base64.getDecoder().decode(iv);
    }

    @JsonGetter("cipher")
    public byte[] getCipher(){
        return Base64.getDecoder().decode(cipher);
    }

    @JsonGetter("hash")
    public String getHash(){
        return hash;
    }

    @JsonGetter("addresses")
    public List<String> getAddresses(){
        return addresses;
    }

    @JsonSetter("addresses")
    public void setAddresses(List<String> _addresses){
        addresses = _addresses;
    }

}
