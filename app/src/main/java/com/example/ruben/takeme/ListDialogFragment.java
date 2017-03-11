package com.example.ruben.takeme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.ruben.takeme.data.TakeMeContract;

/**
 * Created by ruben on 13/6/15.
 */
public class ListDialogFragment extends DialogFragment {



    private Integer lastSelection;

    static final public int LIST_DIALOG_ANIMALS = 0;
    static final public int LIST_DIALOG_BREED = 1;
    static final public int LIST_DIALOG_CATEGORY = 2;
    static final public int LIST_DIALOG_NUMBER = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            mListener = (ListDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Cursor dialogCursor = null;

        int dialogId = this.getArguments().getInt("dialog_id");


        switch (dialogId)
        {
            case (LIST_DIALOG_ANIMALS):
                dialogCursor = this.getActivity().getContentResolver().query(TakeMeContract.AnimalEntry.CONTENT_URI,
                        null,null,null,null);
                lastSelection = this.getArguments().getInt("last_animal");
                break;
            case (LIST_DIALOG_BREED):
                dialogCursor = this.getActivity().getContentResolver().query(TakeMeContract.BreedEntry.CONTENT_URI,
                        null,null,null,null);
                lastSelection = this.getArguments().getInt("last_breed");
                break;
            case (LIST_DIALOG_CATEGORY):
                dialogCursor = this.getActivity().getContentResolver().query(TakeMeContract.CategoryEntry.CONTENT_URI,
                        null,null,null,null);
                lastSelection = this.getArguments().getInt("last_category");
                break;


        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        final Cursor dialogCursorFinal = dialogCursor;
        builder.setTitle("")
                .setIcon(null)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(dialogCursor, lastSelection,
                        TakeMeContract.AnimalEntry.COLUMN_NAME, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int dialogId = getDialogId();
                        dialogCursorFinal.moveToPosition(which);

                        String selection = dialogCursorFinal.getString(1);
                        mListener.onListDialogSelectedItem(selection,which,dialogId);
                        dismiss();
                    }
                });
        return builder.create();
    }

    private int getDialogId()
    {
        return this.getArguments().getInt(getString(R.string.dialog_id));
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ListDialogListener {
        public void onListDialogSelectedItem(String selection,int which,int dialogId);
    }

    // Use this instance of the interface to deliver action events
    ListDialogListener mListener;

}
