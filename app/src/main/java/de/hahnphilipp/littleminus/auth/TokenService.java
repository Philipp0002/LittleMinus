package de.hahnphilipp.littleminus.auth;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.hahnphilipp.littleminus.shared.Constants;
import de.hahnphilipp.littleminus.shared.Preferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TokenService {

    public static void requestTokenFromCode(String code, PKCEUtil.PKCEPair pkcePair, TokenFromCodeCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("redirect_uri", Constants.AUTH_REDIRECT_URI)
                .add("code", code)
                .add("code_verifier", pkcePair.codeVerifier)
                .build();

        Request request = new Request.Builder()
                .url(Constants.BASE_AUTH_API + Constants.ENDPOINT_TOKEN)
                .addHeader("User-Agent", Constants.USER_AGENT)
                .addHeader("Authorization", "Basic " + Base64.encodeToString(
                        (Constants.AUTH_CLIENT_ID + ":" + Constants.AUTH_CLIENT_SECRET).getBytes(),
                        Base64.NO_WRAP))
                .addHeader("Accept", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(response.body().string());
                    String accessToken = node.get("access_token").asText();
                    String refreshToken = node.get("refresh_token").asText();
                    Preferences.putString(Constants.PREF_ACCESS_TOKEN, accessToken);
                    Preferences.putString(Constants.PREF_REFRESH_TOKEN, refreshToken);
                    callback.onSuccess(accessToken, refreshToken);
                } catch (Throwable e) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public static boolean refreshAccessTokenSync() {
        String refreshToken = Preferences.getString(Constants.PREF_REFRESH_TOKEN, null);
        if (refreshToken == null) {
            return false;
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder()
                .url(Constants.BASE_AUTH_API + Constants.ENDPOINT_TOKEN)
                .addHeader("Authorization", "Basic " + Base64.encodeToString(
                        (Constants.AUTH_CLIENT_ID + ":" + Constants.AUTH_CLIENT_SECRET).getBytes(),
                        Base64.NO_WRAP))
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body().string());
            String accessToken = node.get("access_token").asText();
            String newRefreshToken = node.get("refresh_token").asText();
            Preferences.putString(Constants.PREF_ACCESS_TOKEN, accessToken);
            Preferences.putString(Constants.PREF_REFRESH_TOKEN, newRefreshToken);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getAccessToken() {
        return Preferences.getString(Constants.PREF_ACCESS_TOKEN, null);
    }

    public interface TokenFromCodeCallback {
        void onSuccess(String accessToken, String refreshToken);
        void onFailure(String error);
    }

    public interface AccessTokenCallback {
        void onSuccess(String accessToken, String refreshToken);
        void onFailure(String error);
    }

}
