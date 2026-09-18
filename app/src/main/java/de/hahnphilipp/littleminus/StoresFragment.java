package de.hahnphilipp.littleminus;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hahnphilipp.littleminus.location.Store;
import de.hahnphilipp.littleminus.location.StoresService;
import de.hahnphilipp.littleminus.loyalty.Coupon;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class StoresFragment extends BottomSheetDialogFragment implements StoresAdapter.StoreClickListener {

    private ViewAnimator viewAnimator;
    private RecyclerView recyclerView;
    private TextInputLayout searchInput;
    private StoresAdapter storesAdapter;

    private List<Store> stores;

    private StoreSelectListener storeSelectListener;

    public StoresFragment() {
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
        return inflater.inflate(R.layout.fragment_stores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewAnimator = view.findViewById(R.id.viewanimator);
        recyclerView = view.findViewById(R.id.storesrecycler);
        searchInput = view.findViewById(R.id.storessearch);

        storesAdapter = new StoresAdapter(this);
        recyclerView.setAdapter(storesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewAnimator.setDisplayedChild(1);
        loadStores();

        searchInput.addOnEditTextAttachedListener(textInputLayout -> {
                    if (textInputLayout.getEditText() != null) {
                        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable s) {

                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                showStores(s.toString());
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof StoreSelectListener) {
            storeSelectListener = (StoreSelectListener) context;
            return;
        }
        if (getActivity() instanceof StoreSelectListener) {
            storeSelectListener = (StoreSelectListener) getActivity();
        }

    }

    public void loadStores() {
        StoresService.requestStores(new StoresService.RequestStoresCallback() {
            @Override
            public void onSuccess(List<Store> storeList) {
                stores = storeList;
                showStores(null);
            }

            @Override
            public void onFailure(String error) {
                Log.e("StoresFragment", "Failed to load stores: " + error);
            }
        });
    }

    private void showStores(String searchQuery) {
        List<Store> result = stores.stream()
                .sorted(Comparator.comparing(Store::getPostalCode))
                .filter(
                        store -> searchQuery == null ||
                                store.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                store.getAddress().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                store.getLocality().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                store.getPostalCode().toLowerCase().contains(searchQuery.toLowerCase())
                )
                .toList();

        storesAdapter.objects.clear();
        storesAdapter.objects.addAll(result);

        recyclerView.post(() -> {
            storesAdapter.notifyDataSetChanged();
            viewAnimator.setDisplayedChild(0);
        });
    }

    @Override
    public void onStoreSelectButtonClick(Store store) {
        StoresService.setSelectedStore(store.getStoreKey(), store.getName());
        if (storeSelectListener != null) {
            storeSelectListener.onStoreSelected(store);
        }
        dismiss();
    }

    public static interface StoreSelectListener {
        void onStoreSelected(Store store);
    }
}