package com.app.mergeadapterdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mergeadapterdemo.R;
import com.app.mergeadapterdemo.databinding.ItemImageSelectionBinding;
import com.app.mergeadapterdemo.listener.OnImageItemListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;

public class ImageSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Image> selectionList;
    private OnImageItemListener onImageItemListener;

    public ImageSelectionAdapter(Context context, OnImageItemListener onImageItemListener) {
        this.context = context;
        this.onImageItemListener = onImageItemListener;
    }

    public void doRefresh(ArrayList<Image> selectionList) {
        this.selectionList = selectionList;
        notifyDataSetChanged();
    }

    public ArrayList<Image> getImageSelectionList() {
        return selectionList;
    }

    public void clearItems() {
        selectionList.clear();
        selectionList = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageSelectionBinding ItemImageBinding = ItemImageSelectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageViewHolder(ItemImageBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ImageViewHolder) holder).setDataToView(selectionList.get(position));
    }

    @Override
    public int getItemCount() {
        return selectionList == null ? 0 : selectionList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemImageSelectionBinding selectionBinding;
        private ImageSelectionAdapter adapter;

        ImageViewHolder(ItemImageSelectionBinding selectionBinding, ImageSelectionAdapter adapter) {
            super(selectionBinding.getRoot());
            this.selectionBinding = selectionBinding;
            this.adapter = adapter;
            selectionBinding.ivCancel.setVisibility(View.VISIBLE);
            selectionBinding.ivCancel.setOnClickListener(this);
        }

        void setDataToView(Image data) {
            Glide.with(adapter.context)
                    .load(data.getPath())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.user_placeholder)
                            .error(R.drawable.user_placeholder)
                            .centerCrop())
                    .into(selectionBinding.ivImage);

            selectionBinding.ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_cancel) {
                adapter.onImageItemListener.onCloseClick(getAdapterPosition());
            }
        }
    }

}
