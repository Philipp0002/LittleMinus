package de.hahnphilipp.littleminus;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.hc.core5.net.URIBuilder;

import java.net.URISyntaxException;
import java.util.List;

import de.hahnphilipp.littleminus.auth.PKCEUtil;
import de.hahnphilipp.littleminus.auth.TokenService;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;
import de.hahnphilipp.littleminus.shared.Constants;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton qrFab;
    private RecyclerView couponsRecycler;
    private CouponsAdapter couponsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        qrFab = findViewById(R.id.qrfab);
        qrFab.setOnClickListener(v -> showQRFragment());

        couponsRecycler = findViewById(R.id.couponsrecycler);
        couponsAdapter = new CouponsAdapter();
        couponsRecycler.setLayoutManager(new LinearLayoutManager(this));
        couponsRecycler.setAdapter(couponsAdapter);

        loadCoupons();
    }

    private void loadCoupons() {
        LoyaltyService.requestCoupons(new LoyaltyService.RequestCouponsCallback() {
            @Override
            public void onSuccess(List<LoyaltyService.Coupon> couponList) {
                couponsAdapter.objects.clear();
                couponsAdapter.objects.addAll(couponList);
                runOnUiThread(() -> couponsAdapter.notifyDataSetChanged());
            }

            @Override
            public void onFailure(String error) {
                // TODO ERROR HANDLING
            }
        });
    }

    private void showQRFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        QRFragment newFragment = new QRFragment();

        int screenWidth = getResources().getConfiguration().screenWidthDp;
        if (Constants.LARGE_SCREEN_WIDTH_SIZE <= screenWidth) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "qrDialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(newFragment, "qrDialog")
                    .addToBackStack(null).commit();
        }
    }


}