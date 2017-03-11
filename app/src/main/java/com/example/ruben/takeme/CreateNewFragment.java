package com.example.ruben.takeme;

/**
 * Created by ruben on 13/6/15.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateNewFragment extends Fragment implements ListDialogFragment.ListDialogListener{



    private Integer lastAnimalSelection;
    private Integer lastBreedSelection;
    private Integer lastCategorySelection;


    private ShareActionProvider mShareActionProvider;
    private String mDetail;

    static final int TAG_IMAGE_URI = 1;
    static final int TAG_IMAGE_BITMAP = 1;


    static final String STATE_LAST_BREED = "last_breed";
    static final String STATE_LAST_ANIMAL = "last_animal";
    static final String STATE_LAST_CATEGORY = "last_category";
    static final String STATE_ANIMAL = "animal";
    static final String STATE_BREED = "breed";
    static final String STATE_CATEGORY = "category";
    static final String STATE_ANIMALS_NUMBER = "animals_number";
    static final String STATE_DESCRIPTION = "description";
    static final String STATE_IMAGE_BITMAP = "imageUri";


    final private int ACTION_REQUEST_GALLERY = 0;
    final private int ACTION_REQUEST_CAMERA = 1;



    private Bitmap iconBitmap;

    private ImageButton icon;
    private Button animal;
    private Button breed;
    private Button category;
    private EditText animalsNumber;
    private EditText description;

    public CreateNewFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_new, container, false);

        LinearLayout form = (LinearLayout) rootView.findViewById(R.id.create_new_form);

        lastAnimalSelection = lastBreedSelection = lastCategorySelection = 0;

        animalsNumber= (EditText) form.findViewById(R.id.create_new_how_many);
        description = (EditText) form.findViewById(R.id.create_new_short_description);
        icon = (ImageButton)form.findViewById(R.id.create_new_icon);
        animal = (Button)form.findViewById(R.id.create_new_choose_animal);
        breed = (Button)form.findViewById(R.id.create_new_choose_breed);
        category = (Button)form.findViewById(R.id.create_new_choose_category);

        if(savedInstanceState!= null) {
            lastCategorySelection = savedInstanceState.getInt(STATE_LAST_CATEGORY);
            lastBreedSelection = savedInstanceState.getInt(STATE_LAST_BREED);
            lastAnimalSelection = savedInstanceState.getInt(STATE_LAST_ANIMAL);

            animalsNumber.setText(savedInstanceState.getString(STATE_ANIMALS_NUMBER));
            description.setText(savedInstanceState.getString(STATE_DESCRIPTION));
            animal.setText(savedInstanceState.getString(STATE_ANIMAL));
            breed.setText(savedInstanceState.getString(STATE_BREED));
            category.setText(savedInstanceState.getString(STATE_CATEGORY));

            iconBitmap = (Bitmap)savedInstanceState.getParcelable(STATE_IMAGE_BITMAP);
            icon.setImageBitmap(iconBitmap);
        }




        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                showImageDialog();

            }
        });


        animal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                showListDialog(ListDialogFragment.LIST_DIALOG_ANIMALS);

            }
        });

        breed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                showListDialog(ListDialogFragment.LIST_DIALOG_BREED);
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                showListDialog(ListDialogFragment.LIST_DIALOG_CATEGORY);
            }
        });
        return rootView;
    }

    private void showListDialog(int dialogType)
    {
        ListDialogFragment dialog = new ListDialogFragment();

        Bundle args = new Bundle();
        args.putInt(getString(R.string.dialog_id),dialogType);

        args.putInt("last_animal",lastAnimalSelection);
        args.putInt("last_breed",lastBreedSelection);
        args.putInt("last_category",lastCategorySelection);

        dialog.setArguments(args);

        dialog.setTargetFragment(this,0);
        dialog.show(getFragmentManager(),String.valueOf(dialogType));
    }


    @Override
    public void onListDialogSelectedItem(String optionSelected, int which, int dialogId) {

        switch (dialogId) {
            case ListDialogFragment.LIST_DIALOG_ANIMALS: {
                animal.setText(optionSelected);
                lastAnimalSelection = which;
                break;
            }
            case ListDialogFragment.LIST_DIALOG_BREED: {
                breed.setText(optionSelected);
                lastBreedSelection = which;
                break;
            }
            case ListDialogFragment.LIST_DIALOG_CATEGORY: {
                category.setText(optionSelected);
                lastCategorySelection = which;
                break;
            }
        }
    }


    public void showImageDialog(){
        Dialog dialog = new Dialog(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[] { "Gallery", "Camera" },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(
                                        Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");

                                Intent chooser = Intent
                                        .createChooser(
                                                intent,
                                                "Choose a Picture");
                                startActivityForResult(
                                        chooser,
                                        0);

                                break;

                            case 1:
                                Intent cameraIntent = new Intent(
                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(
                                        cameraIntent,
                                        1);

                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();
        dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == ACTION_REQUEST_GALLERY) {
                // System.out.println("select file from gallery ");
                Uri selectedImageUri = data.getData();
                icon.setImageURI(selectedImageUri);
                iconBitmap = ((BitmapDrawable)icon.getDrawable()).getBitmap();
            } else if (requestCode == ACTION_REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras()
                        .get("data");

                icon.setImageBitmap(photo);
                iconBitmap = ((BitmapDrawable)icon.getDrawable()).getBitmap();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_LAST_ANIMAL, lastAnimalSelection);
        outState.putInt(STATE_LAST_BREED, lastBreedSelection);
        outState.putInt(STATE_LAST_CATEGORY, lastCategorySelection);
        outState.putString(STATE_ANIMAL, animal.getText().toString());
        outState.putString(STATE_BREED,  breed.getText().toString());
        outState.putString(STATE_CATEGORY, category.getText().toString());
        outState.putString(STATE_DESCRIPTION, animalsNumber.getText().toString());
        outState.putString(STATE_ANIMALS_NUMBER, animalsNumber.getText().toString());
        outState.putParcelable(STATE_IMAGE_BITMAP,iconBitmap);

        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}


