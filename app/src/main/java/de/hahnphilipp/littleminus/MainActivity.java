package de.hahnphilipp.littleminus;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hahnphilipp.littleminus.location.Store;
import de.hahnphilipp.littleminus.location.StoresService;
import de.hahnphilipp.littleminus.loyalty.Coupon;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class MainActivity extends AppCompatActivity
        implements CouponsAdapter.CouponClickListener, StoresFragment.StoreSelectListener {

    private FloatingActionButton qrFab;
    private RecyclerView couponsRecycler;
    private CouponsAdapter couponsAdapter;
    private Chip storesFilterChip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        storesFilterChip = findViewById(R.id.couponsfilterStore);
        storesFilterChip.setOnClickListener(v -> showStoresFilterFragment());
        storesFilterChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            storesFilterChip.setChecked(isChecked);
        });

        qrFab = findViewById(R.id.qrfab);
        qrFab.setOnClickListener(v -> showQRFragment());

        couponsRecycler = findViewById(R.id.couponsrecycler);
        couponsAdapter = new CouponsAdapter(this);
        couponsRecycler.setLayoutManager(new LinearLayoutManager(this));
        couponsRecycler.setAdapter(couponsAdapter);

        if(StoresService.getSelectedStore().first == null) {
            unfilterStore();
        } else {
            filterStore(StoresService.getSelectedStore().first, StoresService.getSelectedStore().second);
        }
    }

    public void loadCoupons() {
        LoyaltyService.requestCoupons(new LoyaltyService.RequestCouponsCallback() {
            @Override
            public void onSuccess(List<Coupon> couponList) {
                showCoupons(couponList);
            }

            @Override
            public void onFailure(String error) {
                // TODO ERROR HANDLING
            }
        });
    }

    private void showCoupons(List<Coupon> couponList) {
        Pair<String, String> selectedStore = StoresService.getSelectedStore();

        List<Object> result = couponList.stream()
                .filter(coupon -> {
                    if(coupon.getStores() == null || coupon.getStores().length == 0) {
                        return true;
                    }
                    if(selectedStore.first != null) {
                        String selectedStoreKey = StoresService.getSelectedStore().first;
                        return Arrays.asList(coupon.getStores()).contains(selectedStoreKey);
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(Coupon::getSection))
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

    private void showStoresFilterFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        StoresFragment newFragment = new StoresFragment();
        newFragment.show(fragmentManager, "storesDialog");
    }

    private void showQRFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        QRFragment newFragment = new QRFragment();
        newFragment.show(fragmentManager, "qrDialog");
    }

    @Override
    public void onCouponDetailButtonClick(Coupon coupon) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CouponDetailFragment newFragment = CouponDetailFragment.newInstance(coupon);
        newFragment.show(fragmentManager, "couponDetailDialog");
    }

    @Override
    public void onCouponEnableButtonClick(Coupon coupon) {
        LoyaltyService.requestCouponEnable(coupon.getId(), new LoyaltyService.RequestCouponEnableCallback() {
            @Override
            public void onSuccess() {
                coupon.setActivated(!coupon.isActivated());
                int position = couponsAdapter.objects.indexOf(coupon);
                runOnUiThread(() -> couponsAdapter.notifyItemChanged(position));
            }

            @Override
            public void onFailure(String error) {

            }
        }, !coupon.isActivated());
    }

    public void filterStore(String storeKey, String storeName) {
        storesFilterChip.setText(storeName);
        storesFilterChip.setChecked(true);
        loadCoupons();
    }

    public void unfilterStore() {
        storesFilterChip.setText(R.string.filter_store_chip);
        storesFilterChip.setChecked(false);
        loadCoupons();
    }

    @Override
    public void onStoreSelected(@Nullable Store store) {
        if(store == null) {
            unfilterStore();
            return;
        }
        filterStore(store.getStoreKey(), store.getName());
    }
}