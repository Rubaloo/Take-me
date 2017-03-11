package com.example.ruben.takeme;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ruben on 11/6/15.
 */
public class NewsAdapter extends CursorAdapter {

    static final int COL_NEW_ID = 0;
    static final int COL_ANIMAL_ID = 1;
    static final int COL_BREED_ID = 2;
    static final int COL_CATEGORY_ID = 3;
    static final int COL_DESCRIPTION_ID = 4;
    static final int COL_ANIMALS_TAKED = 5;
    static final int COL_ANIMALS_TOTAL = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private Context mContext;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView animalView;
        public final TextView breedView;
        public final TextView adoptionsView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.grid_item_basic_new_icon);
            animalView = (TextView) view.findViewById(R.id.grid_item_basic_new_animal_textview);
            breedView = (TextView) view.findViewById(R.id.grid_item_basic_new_breed_textview);
            adoptionsView = (TextView) view.findViewById(R.id.grid_item_basic_new_adoption_textview);
        }
    }

    public NewsAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView;

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.grid_item_basic_new;


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String animalName = mContext.getResources().getString(Utils.getAnimalNameRrcWithId(cursor.getInt(COL_ANIMAL_ID)));
        String breedName = mContext.getResources().getString(Utils.getBreedNameRrcWithId(cursor.getInt(COL_BREED_ID)));

        viewHolder.animalView.setText(animalName);
        viewHolder.breedView.setText(breedName);
        viewHolder.iconView.setImageResource(Utils.getBreedIconRrcWithId(cursor.getInt(COL_BREED_ID)));
        viewHolder.adoptionsView.setText(cursor.getString(COL_ANIMALS_TAKED) + "/" + cursor.getString(COL_ANIMALS_TOTAL));
    }



}