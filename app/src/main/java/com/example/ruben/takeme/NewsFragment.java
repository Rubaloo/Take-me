package com.example.ruben.takeme;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.ruben.takeme.data.TakeMeContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.example.ruben.takeme.NewsFragment.OnNewsSelectedListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = NewsFragment.class.getSimpleName();

    private NewsAdapter mNewsAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int NEWS_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
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

    private OnNewsSelectedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mNewsAdapter = new NewsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(mNewsAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v,
                                    int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((OnNewsSelectedListener) getActivity())
                            .onNewSelected(TakeMeContract.NewEntry.buildNewUri(cursor.getLong(COL_NEW_ID)));
                }
                mPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today



        return new CursorLoader(getActivity(),
                TakeMeContract.NewEntry.CONTENT_URI,
                NEWS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNewsAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNewsSelectedListener {
        // TODO: Update argument type and name
        public void onNewSelected(Uri uri);
    }

}
