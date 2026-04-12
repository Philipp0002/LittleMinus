package de.hahnphilipp.littleminus;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class MainActivity extends AppCompatActivity implements CouponsAdapter.CouponClickListener {

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
        couponsAdapter = new CouponsAdapter(this);
        couponsRecycler.setLayoutManager(new LinearLayoutManager(this));
        couponsRecycler.setAdapter(couponsAdapter);

        loadCoupons();
    }

    public void loadCoupons() {
        LoyaltyService.requestCoupons(new LoyaltyService.RequestCouponsCallback() {
            @Override
            public void onSuccess(List<LoyaltyService.Coupon> couponList) {
                showCoupons(couponList);
            }

            @Override
            public void onFailure(String error) {
                // TODO ERROR HANDLING
            }
        });
    }

    private void showCoupons(List<LoyaltyService.Coupon> couponList) {
        List<Object> result = couponList.stream()
                .collect(Collectors.groupingBy(coupon -> coupon.section))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> Stream.concat(
                        Stream.of(entry.getKey()),
                        entry.getValue().stream()
                ))
                .toList();

        couponsAdapter.objects.clear();
        couponsAdapter.objects.addAll(result);
        runOnUiThread(() -> couponsAdapter.notifyDataSetChanged());
    }

    private void showQRFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        QRFragment newFragment = new QRFragment();
        newFragment.show(fragmentManager, "qrDialog");
    }


    @Override
    public void onCouponDetailButtonClick(LoyaltyService.Coupon coupon) {

    }

    @Override
    public void onCouponEnableButtonClick(LoyaltyService.Coupon coupon) {
        LoyaltyService.requestCouponEnable(coupon.id, new LoyaltyService.RequestCouponEnableCallback() {
            @Override
            public void onSuccess() {
                coupon.isActivated = !coupon.isActivated;
                int position = couponsAdapter.objects.indexOf(coupon);
                runOnUiThread(() -> couponsAdapter.notifyItemChanged(position));
            }

            @Override
            public void onFailure(String error) {

            }
        }, !coupon.isActivated);
    }
}