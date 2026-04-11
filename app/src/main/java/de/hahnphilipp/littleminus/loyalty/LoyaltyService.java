package de.hahnphilipp.littleminus.loyalty;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hahnphilipp.littleminus.auth.OkHttpAuthenticated;
import de.hahnphilipp.littleminus.auth.TokenService;
import de.hahnphilipp.littleminus.shared.Constants;
import de.hahnphilipp.littleminus.shared.Preferences;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoyaltyService {

    public static String getLoyaltyId() {
        return Preferences.getString(Constants.PREF_LOYALTY_ID, null);
    }

    public static boolean isPaperReceiptEnabled() {
        return Preferences.getBoolean(Constants.PREF_PAPER_RECEIPT, false);
    }

    public static void enablePaperReceipt(boolean enable) {
        Preferences.putBoolean(Constants.PREF_PAPER_RECEIPT, enable);
    }

    public static void requestCouponEnable(RequestCouponEnableCallback callback, boolean enable) {
        OkHttpClient client = OkHttpAuthenticated.getAuthenticatedClient();

        try {
            String uri = new URIBuilder(Constants.BASE_AUTH_API + Constants.ENDPOINT_AUTH)
                    .addParameter("Country", "DE")
                    .build()
                    .toString();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(uri)
                    .addHeader("User-Agent", Constants.USER_AGENT)
                    .addHeader("Authorization", "Bearer " + TokenService.getAccessToken());
            if(enable) {
                requestBuilder.post(RequestBody.create(new byte[0]));
            } else {
                requestBuilder.delete();
            }
            Request request = requestBuilder.build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Failed to enable coupon: " + response.code());
                    }
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void requestLoyaltyId(LoyaltyIdCallback callback) {
        OkHttpClient client = OkHttpAuthenticated.getAuthenticatedClient();

        Request request = new Request.Builder()
                .get()
                .url(Constants.BASE_PROFILE_API + Constants.ENDPOINT_LOYALTY_ID)
                .addHeader("User-Agent", Constants.USER_AGENT)
                .addHeader("Authorization", "Bearer " + TokenService.getAccessToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String loyaltyId;
                try {
                    loyaltyId = response.body().string();
                    Preferences.putString(Constants.PREF_LOYALTY_ID, loyaltyId);
                } catch (Throwable e) {
                    callback.onFailure(e.getMessage());
                    return;
                }
                callback.onSuccess(loyaltyId);
            }
        });
    }

    public static void requestCoupons(RequestCouponsCallback callback) {
        OkHttpClient client = OkHttpAuthenticated.getAuthenticatedClient();

        Request request = new Request.Builder()
                .get()
                .url(Constants.BASE_COUPONS_API + Constants.ENDPOINT_COUPONS_LIST)
                .addHeader("User-Agent", Constants.USER_AGENT)
                .addHeader("Country", "DE")
                .addHeader("Authorization", "Bearer " + TokenService.getAccessToken())
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
                    List<Coupon> couponsList = new ArrayList<>();
                    ObjectMapper mapper = new ObjectMapper();
                    String json = response.body().string();
                    JsonNode node = mapper.readTree(json);
                    node = node.get("sections");

                    for(JsonNode section : node) {
                        for(JsonNode promotion : section.get("promotions")) {
                            Coupon coupon = new Coupon();
                            coupon.id = promotion.get("id").asText();
                            coupon.promotionId = promotion.get("promotionId").asText();
                            coupon.image = promotion.get("image").asText();
                            coupon.type = promotion.get("type").asText();
                            coupon.discountTitle = promotion.get("discount").get("title").asText();
                            coupon.discountDescription = promotion.get("discount").get("description").asText();
                            coupon.discountScope = promotion.get("discount").get("scope").asText();
                            coupon.title = promotion.get("title").asText();

                            couponsList.add(coupon);
                        }
                    }

                    callback.onSuccess(couponsList);
                } catch (Throwable e) {
                    callback.onFailure(e.getMessage());
                }
            }
        });
    }

    public interface LoyaltyIdCallback {
        void onSuccess(String loyaltyId);
        void onFailure(String error);
    }

    public interface RequestCouponsCallback {
        void onSuccess(List<Coupon> couponList);
        void onFailure(String error);
    }

    public interface RequestCouponEnableCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public static class Coupon {
        public String id;
        public String promotionId;
        public String image;
        public String type;

        public String discountTitle;
        public String discountDescription;
        public String discountScope;
        public String title;
        public boolean isActivated;

    }
}
