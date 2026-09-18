package de.hahnphilipp.littleminus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import de.hahnphilipp.littleminus.location.Store;
import de.hahnphilipp.littleminus.utils.GroupTitleViewHolder;

public class StoresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<Object> objects;
    private StoreClickListener storeClickListener;

    public StoresAdapter(StoreClickListener storeClickListener) {
        objects = new ArrayList<>();
        this.storeClickListener = storeClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store, parent, false);
            return new StoreViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_title, parent, false);
            return new GroupTitleViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder _holder, int position) {
        updateView(position, _holder);
    }

    @Override
    public int getItemViewType(int position) {
        if(objects.get(position) instanceof Store) {
            return 1;
        }
        return 0;
    }

    public void updateView(int indexPos, RecyclerView.ViewHolder _holder) {
        if(_holder instanceof StoreViewHolder holder) {
            final Store item = (Store) objects.get(indexPos);
            holder.storeName.setText(item.getName());

            StringBuilder address = new StringBuilder();
            address.append(item.getAddress());
            address.append(", ");
            address.append(item.getPostalCode());
            address.append(" ");
            address.append(item.getLocality());
            holder.storeAddress.setText(address);

            holder.storeSelectButton.setOnClickListener(v -> storeClickListener.onStoreSelectButtonClick(item));
        } else {
            final String title = (String) objects.get(indexPos);
            GroupTitleViewHolder holder = (GroupTitleViewHolder) _holder;
            holder.title.setText(title);
        }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView mainView;
        public TextView storeName;
        public TextView storeAddress;
        public MaterialButton storeSelectButton;

        public StoreViewHolder(View itemView) {
            super(itemView);
            mainView = (MaterialCardView) itemView;
            storeName = itemView.findViewById(R.id.storename);
            storeAddress = itemView.findViewById(R.id.storeaddress);
            storeSelectButton = itemView.findViewById(R.id.storeselectbutton);
        }
    }

    public static interface StoreClickListener {
        void onStoreSelectButtonClick(Store store);
    }

}
