package ua.com.expertsoft.android_smeta.tweet;

import android.util.Base64;

import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * Created by mityai on 26.05.2016.
 */
public class SignatureGenerator {

    private static final String ENC = "UTF-8";
    private static final String HMAC_SHA1 = "HmacSHA1";


    private String generateSignature(String method, String url,
                                     String consumerSecret,
                                     String tokenSecret, String paramsStr ){
        String toReturn = "";
        try {
            String signingKey = consumerSecret + "&" + tokenSecret;
            String encodedURl = URLEncoder.encode(url, ENC);
            String encodedParams = URLEncoder.encode(paramsStr, ENC);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(method);
            stringBuilder.append("&");
            stringBuilder.append(encodedURl);
            stringBuilder.append("&");
            stringBuilder.append(encodedParams);

            byte[] keyBytes = (signingKey).getBytes(ENC);
            SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(key);
            byte[] signature = mac.doFinal(stringBuilder.toString().getBytes(ENC));
            toReturn = new String(Base64.encode(signature, Base64.DEFAULT), ENC).trim();

        }catch(Exception e){
            e.printStackTrace();
        }
        return toReturn;
    }

    public String getSignature(String method, String url,
                               String consumerSecret,
                               String tokenSecret, String paramsStr){
        return generateSignature(method, url, consumerSecret,tokenSecret,paramsStr);
    }

}
