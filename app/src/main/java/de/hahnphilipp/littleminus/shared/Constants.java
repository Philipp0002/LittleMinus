package de.hahnphilipp.littleminus.shared;

public class Constants {

    public static final String BASE_PROFILE_API = "https://profile.lidlplus.com";
    public static final String ENDPOINT_LOYALTY_ID = "/api/v1/DE/loyalty";
    public static final String BASE_COUPONS_API = "https://coupons.lidlplus.com";
    public static final String ENDPOINT_COUPONS_LIST = "/app/api/v3/promotionslist";
    public static final String ENDPOINT_COUPONS_ENABLE = "/app/api/v2/promotions/%s/activation";
    public static final String BASE_AUTH_API = "https://accounts.lidl.com";
    public static final String ENDPOINT_AUTH = "/connect/authorize";
    public static final String ENDPOINT_TOKEN = "/connect/token";
    public static final String AUTH_REDIRECT_URI = "com.lidlplus.app://callback";
    public static final String AUTH_CLIENT_ID = "LidlPlusNativeClient";
    public static final String AUTH_CLIENT_SECRET = "secret";
    public static final String AUTH_SCOPE = "openid profile offline_access lpprofile lpapis";
    public static final String USER_AGENT = "LidlPlus/16.0.0 (iPhone; iOS 17.0; Scale/3.00)";


    public static final String PREF_PAPER_RECEIPT = "paper_receipt_enabled";
    public static final String PREF_LOYALTY_ID = "loyalty_id";
    public static final String PREF_ACCESS_TOKEN = "access_token";
    public static final String PREF_REFRESH_TOKEN = "refresh_token";


    public static final int LARGE_SCREEN_WIDTH_SIZE = 1150;
}
