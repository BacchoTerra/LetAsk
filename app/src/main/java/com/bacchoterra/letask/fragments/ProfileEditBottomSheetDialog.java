package com.bacchoterra.letask.fragments;

import android.content.Context;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bacchoterra.letask.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ProfileEditBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    //Interface
    private OnFabChoiceListener mListener;

    //Layout components
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private FloatingActionButton fabRemovePic;

    //Fab choices
    public static final int FAB_CAMERA = 0;
    public static final int FAB_GALLERY = 1;
    public static final int FAB_REMOVE_PIC = 2;


    public ProfileEditBottomSheetDialog() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFabChoiceListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement the interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit_bottom_sheet_dialog, container, false);
        initViews(view);


        return view;
    }

    private void initViews(View view) {

        fabCamera = view.findViewById(R.id.profile_edit_btmSheet_fabCamera);
        fabGallery = view.findViewById(R.id.profile_edit_btmSheet_fabGallery);
        fabRemovePic = view.findViewById(R.id.profile_edit_btmSheet_fabRemovePic);

        fabCamera.setOnClickListener(this);
        fabGallery.setOnClickListener(this);
        fabRemovePic.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.profile_edit_btmSheet_fabCamera:

                mListener.onFabChose(FAB_CAMERA);
                dismiss();
                break;

            case R.id.profile_edit_btmSheet_fabGallery:

                mListener.onFabChose(FAB_GALLERY);
                dismiss();
                break;

            case R.id.profile_edit_btmSheet_fabRemovePic:

                mListener.onFabChose(FAB_REMOVE_PIC);
                dismiss();
                break;

        }


    }

    public interface OnFabChoiceListener {

        void onFabChose(int choice);


    }

}
