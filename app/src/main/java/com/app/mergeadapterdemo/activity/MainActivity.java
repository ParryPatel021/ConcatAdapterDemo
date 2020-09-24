package com.app.mergeadapterdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.mergeadapterdemo.R;
import com.app.mergeadapterdemo.adapter.AddImageAdapter;
import com.app.mergeadapterdemo.adapter.ImageSelectionAdapter;
import com.app.mergeadapterdemo.databinding.ActivityMainBinding;
import com.app.mergeadapterdemo.listener.OnImageItemListener;
import com.app.mergeadapterdemo.utils.ContentUriUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddImageAdapter.OnAddImageClickListener, OnImageItemListener {

    private static final int ITEM_CAMERA = 1;
    private static final int ITEM_GALLERY = 2;
    private ActivityMainBinding binding;
    private ImageSelectionAdapter imageSelectionAdapter;
    private AddImageAdapter addImageAdapter;
    private int totalPhotosLimit = 10;
    private ArrayList<Image> selectedImageList;
    private ArrayList<Image> selectedResultList;
    private ConcatAdapter concatAdapter;
    private boolean isAllowed = false;

    private Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialization();
    }

    private void initialization() {
        selectedImageList = new ArrayList<>();
        selectedResultList = new ArrayList<>();
        prepareImageSelectionRecyclerView();
        getPermissionForImageSelection();
    }

    private void getPermissionForImageSelection() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                isAllowed = true;
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                isAllowed = false;
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.request_camera_permission))
                .setDeniedMessage(getString(R.string.on_denied_permission))
                .setGotoSettingButtonText(getString(R.string.action_settings))
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.text_choose_an_option));
        String[] options = {getString(R.string.text_open_camera), getString(R.string.text_open_gallery)};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    ImagePicker.with(getActivity())
                            .setToolbarColor("#ffffff")
                            .setStatusBarColor("#ffffff")
                            .setToolbarTextColor("#000000")
                            .setToolbarIconColor("#E9F12A")
                            .setProgressBarColor("#E9F12A")
                            .setBackgroundColor("#ffffff")
                            .setCameraOnly(true)
                            .setMultipleMode(true)
                            .setFolderMode(false)
                            .setDoneTitle(getString(R.string.text_done))
                            .setMaxSize(totalPhotosLimit)
                            .setLimitMessage(String.format(getString(R.string.msg_reached_image_selection_limit), "" + totalPhotosLimit))
                            .setSelectedImages(selectedResultList)
                            .setAlwaysShowDoneButton(true)
                            .setRequestCode(ITEM_CAMERA)
                            .start();
                    break;
                case 1:
                    ImagePicker.with(getActivity())
                            .setToolbarColor("#ffffff")
                            .setStatusBarColor("#ffffff")
                            .setToolbarTextColor("#000000")
                            .setToolbarIconColor("#E9F12A")
                            .setProgressBarColor("#E9F12A")
                            .setBackgroundColor("#ffffff")
                            .setCameraOnly(false)
                            .setMultipleMode(true)
                            .setFolderMode(true)
                            .setShowCamera(false)
                            .setFolderTitle("Albums")
                            .setImageTitle("Galleries")
                            .setLimitMessage(String.format(getString(R.string.msg_reached_image_selection_limit), "" + totalPhotosLimit))
                            .setDoneTitle(getString(R.string.text_done))
                            .setMaxSize(totalPhotosLimit)
                            .setSelectedImages(selectedResultList)
                            .setAlwaysShowDoneButton(true)
                            .setRequestCode(ITEM_GALLERY)
                            .start();
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prepareImageSelectionRecyclerView() {
        if (selectedImageList != null) {
            if (addImageAdapter == null) {
                addImageAdapter = new AddImageAdapter(getActivity(), this);
            }
            if (imageSelectionAdapter == null) {
                imageSelectionAdapter = new ImageSelectionAdapter(getActivity(), this);
            }

            imageSelectionAdapter.doRefresh(selectedImageList);

            //Concat Adapter
            concatAdapter = new ConcatAdapter(addImageAdapter, imageSelectionAdapter);

            binding.recyclerGrid.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            binding.recyclerGrid.setLayoutManager(gridLayoutManager);
            binding.recyclerGrid.setAdapter(concatAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*Camera & Gallery Result*/
        if (requestCode == ITEM_CAMERA && resultCode == RESULT_OK && data != null) {
            if (data.getParcelableArrayListExtra(Config.EXTRA_IMAGES) != null) {
                selectedResultList = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                    if (selectedResultList != null) {
                        selectedResultList.get(0).setPath(ContentUriUtils.getFilePath(getActivity(), selectedResultList.get(0).getUri()));
                    }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    setResultToList(selectedResultList);
                } else {
                    setResultToList(selectedResultList);
                }
            }
            prepareImageSelectionRecyclerView();
        } else if (requestCode == ITEM_GALLERY && resultCode == RESULT_OK && data != null) {
            if (data.getParcelableArrayListExtra(Config.EXTRA_IMAGES) != null) {
                selectedResultList = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                setResultToList(selectedResultList);
            }
            prepareImageSelectionRecyclerView();
        }
    }

    private void setResultToList(ArrayList<Image> selectedResult) {
        selectedImageList.addAll(selectedResult);
        selectedResultList.clear();
        totalPhotosLimit = 10 - selectedImageList.size();
    }

    @Override
    public void onAddImageClick(int position) {
        if (selectedImageList.size() < totalPhotosLimit) {
            if (isAllowed) {
                showPicker();
            } else {
                getPermissionForImageSelection();
            }
        } else {
            Toast.makeText(getActivity(), String.format(getString(R.string.msg_reached_image_selection_limit), "" + totalPhotosLimit), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCloseClick(int position) {
        selectedImageList.remove(position);
        imageSelectionAdapter.notifyItemRemoved(position);
        imageSelectionAdapter.notifyItemRangeChanged(position, imageSelectionAdapter.getItemCount());
        totalPhotosLimit = 10 - selectedImageList.size();
    }
}