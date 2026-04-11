package de.hahnphilipp.littleminus;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class QRFragment extends DialogFragment {

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

        ImageView qrImageView = view.findViewById(R.id.qrimage);
        MaterialSwitch paperReceiptSwitch = view.findViewById(R.id.paperreceiptswitch);
        paperReceiptSwitch.setChecked(LoyaltyService.isPaperReceiptEnabled());
        paperReceiptSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LoyaltyService.enablePaperReceipt(isChecked);
            updateQR(qrImageView);
        });
        updateQR(qrImageView);
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