package de.hahnphilipp.littleminus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import de.hahnphilipp.littleminus.auth.PKCEUtil;
import de.hahnphilipp.littleminus.auth.TokenService;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;
import de.hahnphilipp.littleminus.shared.Constants;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ViewAnimator;

import org.apache.hc.core5.net.URIBuilder;

import java.net.URISyntaxException;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private ViewAnimator viewAnimator;
    private TextView loadingTextView;
    private PKCEUtil.PKCEPair pkcePair;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(TokenService.getAccessToken() != null){
            skipLogin();
            return;
        }

        setContentView(R.layout.activity_login);

        pkcePair = PKCEUtil.generatePKCE();
        Log.d("LOGIN_PKCE", "Verifier: " + pkcePair.codeVerifier);
        Log.d("LOGIN_PKCE", "Challenge: " + pkcePair.codeChallenge);

        loadingTextView = findViewById(R.id.loadingtext);
        viewAnimator = findViewById(R.id.viewanimator);
        viewAnimator.setDisplayedChild(0);
        webView = findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri.toString().startsWith(Constants.AUTH_REDIRECT_URI)) {
                    String code = uri.getQueryParameter("code");
                    requestToken(code);
                }
                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        openAuthURL();
    }

    public void requestToken(String code) {
        runOnUiThread(() -> {
            viewAnimator.setDisplayedChild(1);
            loadingTextView.setText(R.string.signing_in);
        });

        TokenService.requestTokenFromCode(code, pkcePair, new TokenService.TokenFromCodeCallback() {
                    @Override
                    public void onSuccess(String accessToken, String refreshToken) {
                        Log.d("LOGIN_SUCC", "access_token: " + accessToken);
                        Log.d("LOGIN_SUCC", "refresh_token: " + refreshToken);
                        requestLoyaltyId();
                    }

                    @Override
                    public void onFailure(String error) {
                        openAuthURL();
                        runOnUiThread(() -> viewAnimator.setDisplayedChild(0));
                    }
                });

    }

    public void requestLoyaltyId() {
        runOnUiThread(() -> {
            viewAnimator.setDisplayedChild(1);
            loadingTextView.setText(R.string.fetching_loyalty_id);
        });
        LoyaltyService.requestLoyaltyId(new LoyaltyService.LoyaltyIdCallback() {
            @Override
            public void onSuccess(String loyaltyId) {
                Log.d("LOGIN_SUCC", "loyalty_id: " + loyaltyId);
                skipLogin();
            }

            @Override
            public void onFailure(String error) {
                openAuthURL();
                runOnUiThread(() -> viewAnimator.setDisplayedChild(0));
            }
        });
    }

    private void openAuthURL() {
        try {
            String uri = new URIBuilder(Constants.BASE_AUTH_API + Constants.ENDPOINT_AUTH)
                    .addParameter("client_id", Constants.AUTH_CLIENT_ID)
                    .addParameter("redirect_uri", Constants.AUTH_REDIRECT_URI)
                    .addParameter("response_type", "code")
                    .addParameter("scope", Constants.AUTH_SCOPE)
                    .addParameter("Country", "DE")
                    .addParameter("language", "de-DE")
                    .addParameter("state", "12345")
                    .addParameter("nonce", "67890")
                    .addParameter("code_challenge", pkcePair.codeChallenge)
                    .addParameter("code_challenge_method", "S256")
                    .build()
                    .toString();

            runOnUiThread(() -> webView.loadUrl(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void skipLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}