package com.example.ruben.takeme;

/**
 * Created by ruben on 14/6/15.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruben.takeme.data.TakeMeContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private Uri mUri;
    static final String NEW_DETAIL_URI = "NEW_DETAIL_URI";
    private static final int NEW_DETAIL_LOADER = 0;


    private static final String[] NEWS_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            TakeMeContract.NewEntry._ID,
            TakeMeContract.NewEntry.COLUMN_ANIMAL_ID,
            TakeMeContract.NewEntry.COLUMN_BREED_ID,
            TakeMeContract.NewEntry.COLUMN_CATEGORY_ID,
            TakeMeContract.NewEntry.COLUMN_DESCRIPTION,
            TakeMeContract.NewEntry.COLUMN_ANIMALS_TAKED,
            TakeMeContract.NewEntry.COLUMN_ANIMALS_TOTAL,
            TakeMeContract.NewEntry.COLUMN_COORD_LAT,
            TakeMeContract.NewEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_NEW_ID = 0;
    static final int COL_ANIMAL_ID = 1;
    static final int COL_BREED_ID = 2;
    static final int COL_CATEGORY_ID = 3;
    static final int COL_DESCRIPTION_ID = 4;
    static final int COL_ANIMALS_TAKED = 5;
    static final int COL_ANIMALS_TOTAL = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    static final String STATE_PREVIOUS_PROGRESS = "PREVIOUS_PROGRESS";
    static final String STATE_ANIMALS_TAKED = "ANIMALS_TAKED";
    static final String STATE_SEEK_BAR_STEPS = "SEEK_BAR_STEPS";




    private Boolean mRequestSend;
    private Float mNewLat;
    private Float mNewLong;
    private Integer mPreviousProgress;
    private Integer mSeekBarSteps;
    private Integer mAnimalsTaked;
    private Integer mInitAnimalsTaked;
    private Integer mAnimalsTotal;


    private SeekBar mSeekBar;
    private ImageView mIconView;
    private Button mShowLocationView;
    private Button mTakemeView;

    private TextView mAnimalView;
    private TextView mBreedView;
    private TextView mDescriptionView;
    private TextView mCurrentView;

    public NewDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(NewDetailFragment.NEW_DETAIL_URI);
        }

        mAnimalsTaked = mSeekBarSteps = -1;
        mPreviousProgress = 0;

        if(savedInstanceState!= null) {
            mAnimalsTaked = savedInstanceState.getInt(STATE_ANIMALS_TAKED);
            mPreviousProgress = savedInstanceState.getInt(STATE_PREVIOUS_PROGRESS);
        }

        View rootView = inflater.inflate(R.layout.fragment_new_detail, container, false);

        LinearLayout holder = (LinearLayout) rootView.findViewById(R.id.new_detail_container);


        mIconView = (ImageView) rootView.findViewById(R.id.new_detail_icon);

        mShowLocationView = (Button)holder.findViewById(R.id.new_detail_show_location);
        mTakemeView= (Button) holder.findViewById(R.id.new_detail_take_me);

        mAnimalView =  (TextView) holder.findViewById(R.id.new_detail_animal);
        mBreedView = (TextView) holder.findViewById(R.id.new_detail_breed);
        mDescriptionView =  (TextView) holder.findViewById(R.id.new_detail_description);

        mSeekBar = (SeekBar) holder.findViewById(R.id.new_detail_hm_seekbar);
        mCurrentView = (TextView) holder.findViewById(R.id.new_detail_animals_taked);

        mRequestSend = false;

        mShowLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewLocationInMap();
            }
        });

        mTakemeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRequestSend)Toast.makeText(getActivity(),"Your request has been send",Toast.LENGTH_SHORT).show();
                else Toast.makeText(getActivity(),"Wait an answer",Toast.LENGTH_SHORT).show();
                mRequestSend = true;
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NEW_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    NEWS_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    private String getTextWithCurrentProgress()
    {
       return String.valueOf(mAnimalsTaked) +"/"+ String.valueOf(mAnimalsTotal);

    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            int animalId = data.getInt(COL_ANIMAL_ID);
            int breedId = data.getInt(COL_BREED_ID);
            String description = data.getString(COL_DESCRIPTION_ID);

            mAnimalsTotal = data.getInt(COL_ANIMALS_TOTAL);
            mInitAnimalsTaked = data.getInt(COL_ANIMALS_TAKED);

            if (mAnimalsTaked < 0)  mAnimalsTaked = mInitAnimalsTaked;

            mSeekBarSteps = mAnimalsTotal-mInitAnimalsTaked;
            mSeekBar.setMax(mSeekBarSteps);

            mSeekBar.setProgress(mPreviousProgress);
            mCurrentView.setText(getTextWithCurrentProgress());




            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mPreviousProgress > progress) --mAnimalsTaked;
                    else ++mAnimalsTaked;

                    mPreviousProgress = progress;
                    mCurrentView.setText(getTextWithCurrentProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mNewLat = data.getFloat(COL_COORD_LAT);
            mNewLong = data.getFloat(COL_COORD_LONG);


            // Use weather art image
            int resourceId = Utils.getBreedIconRrcWithId(breedId);
            mIconView.setImageResource(resourceId);

            String animalName = getResources().getString(Utils.getAnimalNameRrcWithId(animalId));
            String breedName = getResources().getString(Utils.getAnimalNameRrcWithId(breedId));

            mAnimalView.setText(animalName);
            mBreedView.setText(breedName);
            mBreedView.setText(Utils.getBreedNameRrcWithId(breedId));
            mDescriptionView.setText(description);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void openNewLocationInMap() {


        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<lat>,<long>?q=<lat>,<long>(Label+Name)"));

        String latS = Float.toString(mNewLat);
        String longS = Float.toString(mNewLong);

//        Uri geoLocation = Uri.parse("geo:<"+latS+">,<"+longS+">?q<"+latS+">,<"+longS+">()").buildUpon()
//                .build();


        Uri geoLocation = Uri.parse("geo:0,0?q="+latS+","+longS+"(label)");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_ANIMALS_TAKED,mAnimalsTaked);
        outState.putInt(STATE_PREVIOUS_PROGRESS,mPreviousProgress);
        outState.putInt(STATE_SEEK_BAR_STEPS, mSeekBarSteps);
        super.onSaveInstanceState(outState);

    }
}
