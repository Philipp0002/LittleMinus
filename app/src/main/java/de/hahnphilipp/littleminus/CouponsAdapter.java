package de.hahnphilipp.littleminus;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import de.hahnphilipp.littleminus.loyalty.LoyaltyService;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.CouponViewHolder> {

    public ArrayList<LoyaltyService.Coupon> objects;

    public CouponsAdapter() {
        objects = new ArrayList<>();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon, parent, false);
        return new CouponViewHolder(v);
    }

    @Override
    public void onViewRecycled(@NonNull final CouponViewHolder holder) {
        Glide.with(LittleMinusApplication.context).clear(holder.couponImage);
        holder.couponImage.setImageDrawable(null);
    }

    @Override
    public void onBindViewHolder(@NonNull final CouponViewHolder holder, int position) {
        updateView(position, holder);
    }

    public void updateView(int indexPos, CouponViewHolder holder) {
        final LoyaltyService.Coupon item = objects.get(indexPos);
        holder.couponTitle.setText(item.title);
        holder.couponDiscountTitle.setText(item.discountTitle);
        holder.couponDiscountDescription.setText(item.discountDescription);
        Glide.with(LittleMinusApplication.context)
                .load(item.image)
                .centerInside()
                .into(holder.couponImage);

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {

        public CardView mainView;
        public ShapeableImageView couponImage;
        public TextView couponTitle;
        public TextView couponDiscountTitle;
        public TextView couponDiscountDescription;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView;
            couponTitle = itemView.findViewById(R.id.coupontitle);
            couponImage = itemView.findViewById(R.id.couponimage);
            couponDiscountTitle = itemView.findViewById(R.id.coupondiscounttitle);
            couponDiscountDescription = itemView.findViewById(R.id.coupondiscountdescription);

        }
    }

}
