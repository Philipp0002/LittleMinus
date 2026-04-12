package de.hahnphilipp.littleminus;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class QRFragment extends BottomSheetDialogFragment {

    private MaterialButton paperReceiptEnableButton;
    private MaterialButton paperReceiptDisableButton;
    private Button enableAllCouponsButton;
    private ImageView qrImageView;

    public QRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paperReceiptEnableButton = view.findViewById(R.id.paperreceiptenable);
        paperReceiptDisableButton = view.findViewById(R.id.paperreceiptdisable);
        enableAllCouponsButton = view.findViewById(R.id.couponsenableall);
        qrImageView = view.findViewById(R.id.qrimage);

        paperReceiptEnableButton.setOnClickListener(v -> {
            LoyaltyService.enablePaperReceipt(true);
            updateQR(qrImageView);
            updatePaperReceiptButtons();
        });
        paperReceiptDisableButton.setOnClickListener(v -> {
            LoyaltyService.enablePaperReceipt(false);
            updateQR(qrImageView);
            updatePaperReceiptButtons();
        });
        enableAllCouponsButton.setOnClickListener(v -> enableAllCoupons());
        updateQR(qrImageView);

        updatePaperReceiptButtons();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setFullBrightness(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setFullBrightness(false);
    }

    private void updatePaperReceiptButtons() {
        if(LoyaltyService.isPaperReceiptEnabled()) {
            paperReceiptEnableButton.setChecked(true);
            paperReceiptDisableButton.setChecked(false);
        } else {
            paperReceiptEnableButton.setChecked(false);
            paperReceiptDisableButton.setChecked(true);
        }
    }

    private void setFullBrightness(boolean fullBrightness) {
        WindowManager.LayoutParams layout = requireActivity().getWindow().getAttributes();
        layout.screenBrightness = 1F * (fullBrightness ? 1 : -1);
        requireActivity().getWindow().setAttributes(layout);

        if(fullBrightness) {
            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void enableAllCoupons() {
        enableAllCouponsButton.setEnabled(false);
        LoyaltyService.requestAllCouponsEnable(new LoyaltyService.RequestCouponEnableCallback() {
            @Override
            public void onSuccess() {
                // ignore
                requireActivity().runOnUiThread(() -> enableAllCouponsButton.setEnabled(true));
                ((MainActivity)requireActivity()).loadCoupons();
            }

            @Override
            public void onFailure(String error) {
                requireActivity().runOnUiThread(() -> enableAllCouponsButton.setEnabled(true));
                ((MainActivity)requireActivity()).loadCoupons();
            }
        }, true);
    }

    public void updateQR(ImageView imageView) {
        try {
            String barcodeData = LoyaltyService.getLoyaltyId();
            barcodeData += LoyaltyService.isPaperReceiptEnabled() ? "0" : "1";

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(barcodeData, BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}