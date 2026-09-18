package de.hahnphilipp.littleminus;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import de.hahnphilipp.littleminus.loyalty.Coupon;
import de.hahnphilipp.littleminus.loyalty.LoyaltyService;
import de.hahnphilipp.littleminus.utils.GroupTitleViewHolder;

public class CouponsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<Object> objects;
    private CouponClickListener couponClickListener;

    public CouponsAdapter(CouponClickListener couponClickListener) {
        objects = new ArrayList<>();
        this.couponClickListener = couponClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon, parent, false);
            return new CouponViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_title, parent, false);
            return new GroupTitleViewHolder(v);
        }
    }

    @Override
    public void onViewRecycled(@NonNull final RecyclerView.ViewHolder _holder) {
        if(_holder instanceof CouponViewHolder holder) {
            Glide.with(LittleMinusApplication.context).clear(holder.couponImage);
            holder.couponImage.setImageDrawable(null);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder _holder, int position) {
        updateView(position, _holder);
    }

    @Override
    public int getItemViewType(int position) {
        if(objects.get(position) instanceof Coupon) {
            return 1;
        }
        return 0;
    }

    public void updateView(int indexPos, RecyclerView.ViewHolder _holder) {
        if(_holder instanceof CouponViewHolder holder) {
            final Coupon item = (Coupon) objects.get(indexPos);
            holder.couponTitle.setText(item.getTitle());
            holder.couponDiscountTitle.setText(item.getDiscountTitle());
            holder.couponDiscountDescription.setText(item.getDiscountDescription());
            Glide.with(LittleMinusApplication.context)
                    .load(item.getImage())
                    .centerInside()
                    .into(holder.couponImage);

            holder.mainView.setChecked(item.isActivated());

            holder.couponEnableButton.setText(
                    item.isActivated() ? R.string.disable_coupon_action : R.string.enable_coupon_action
            );

            holder.couponEnableButton.setOnClickListener(v -> couponClickListener.onCouponEnableButtonClick(item));
            holder.couponDetailButton.setOnClickListener(v -> couponClickListener.onCouponDetailButtonClick(item));

            String relativeValidity = DateUtils.getRelativeTimeSpanString(
                    LittleMinusApplication.context,
                    item.getValidUntil().toEpochSecond() * 1000,
                    false
            ).toString();
            holder.couponValidity.setText(relativeValidity);

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

    public static class CouponViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView mainView;
        public ShapeableImageView couponImage;
        public TextView couponTitle;
        public TextView couponDiscountTitle;
        public TextView couponDiscountDescription;
        public TextView couponValidity;
        public MaterialButton couponEnableButton;
        public MaterialButton couponDetailButton;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mainView = (MaterialCardView) itemView;
            couponTitle = itemView.findViewById(R.id.coupontitle);
            couponImage = itemView.findViewById(R.id.couponimage);
            couponDiscountTitle = itemView.findViewById(R.id.coupondiscounttitle);
            couponDiscountDescription = itemView.findViewById(R.id.coupondiscountdescription);
            couponValidity = itemView.findViewById(R.id.couponvalidity);
            couponEnableButton = itemView.findViewById(R.id.couponenablebutton);
            couponDetailButton = itemView.findViewById(R.id.coupondetailbutton);
        }
    }

    public static interface CouponClickListener {
        void onCouponDetailButtonClick(Coupon coupon);
        void onCouponEnableButtonClick(Coupon coupon);
    }

}
