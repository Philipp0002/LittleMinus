package de.hahnphilipp.littleminus.utils;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import lombok.Getter;

public class GroupTitleViewHolder extends RecyclerView.ViewHolder {

    @Getter
    public TextView title;

    public GroupTitleViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView;
    }
}
