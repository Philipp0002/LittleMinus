package de.hahnphilipp.littleminus;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import de.hahnphilipp.littleminus.loyalty.Coupon;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class CouponDetailFragment extends BottomSheetDialogFragment {

    private TextView couponTitle;
    private ImageView couponImage;
    private TextView couponDiscountTitle;
    private TextView couponDiscountDescription;

    private Coupon coupon;

    public CouponDetailFragment() {
        // Required empty public constructor
    }

    static CouponDetailFragment newInstance(Coupon coupon) {
        CouponDetailFragment f = new CouponDetailFragment();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Bundle args = new Bundle();
        try {
            args.putString("coupon", objectMapper.writeValueAsString(coupon));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupon_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() == null) {
            dismiss();
            return;
        }
        String couponJson = getArguments().getString("coupon", null);
        if(couponJson == null) {
            dismiss();
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            coupon = objectMapper.readValue(couponJson, Coupon.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            dismiss();
            return;
        }

        couponTitle = view.findViewById(R.id.coupontitle);
        couponImage = view.findViewById(R.id.couponimage);
        couponDiscountTitle = view.findViewById(R.id.coupondiscounttitle);
        couponDiscountDescription = view.findViewById(R.id.coupondiscountdescription);

        couponTitle.setText(coupon.getTitle());
        Glide.with(LittleMinusApplication.context)
                .load(coupon.getImage())
                .centerInside()
                .into(couponImage);
        couponDiscountTitle.setText(coupon.getDiscountTitle());
        couponDiscountDescription.setText(coupon.getDiscountDescription());
    }

}