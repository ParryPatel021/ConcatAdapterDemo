package com.app.mergeadapterdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mergeadapterdemo.R;
import com.app.mergeadapterdemo.databinding.ItemAddImageBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class AddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnAddImageClickListener onImageClick;

    public AddImageAdapter(Context context, OnAddImageClickListener onImageClick) {
        this.context = context;
        this.onImageClick = onImageClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddImageBinding itemAddImageBinding = ItemAddImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddImageViewHolder(itemAddImageBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AddImageViewHolder) holder).setDataToView();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public interface OnAddImageClickListener {
        void onAddImageClick(int position);
    }

    public class AddImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemAddImageBinding itemAddImageBinding;
        private AddImageAdapter addImageAdapter;

        AddImageViewHolder(ItemAddImageBinding itemAddImageBinding, AddImageAdapter addImageAdapter) {
            super(itemAddImageBinding.getRoot());
            this.itemAddImageBinding = itemAddImageBinding;
            this.addImageAdapter = addImageAdapter;
            itemAddImageBinding.ivImage.setOnClickListener(this);
        }

        void setDataToView() {
            Glide.with(context)
                    .load(R.drawable.add_image)
                    .apply(new RequestOptions().transform(new RoundedCorners(14)))
                    .into(itemAddImageBinding.ivImage);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_image) {
                addImageAdapter.onImageClick.onAddImageClick(getAdapterPosition());
            }
        }
    }
}
