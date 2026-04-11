package de.hahnphilipp.littleminus.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class OkHttpAuthenticated implements Authenticator {

    private static OkHttpClient client;

    public static OkHttpClient getAuthenticatedClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .authenticator(new OkHttpAuthenticated())
                    .build();
        }
        return client;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) {
        if (responseCount(response) >= 2) {
            return null;
        }

        // Neuen Token holen (blockierend – läuft bereits auf Background-Thread)
        if (!TokenService.refreshAccessTokenSync()) {
            return null; // Refresh fehlgeschlagen, Request abbrechen
        }
        String newToken = TokenService.getAccessToken();

        // Originalrequest mit neuem Token wiederholen
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newToken)
                .build();
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
