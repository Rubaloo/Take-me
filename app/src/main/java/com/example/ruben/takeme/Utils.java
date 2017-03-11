package com.example.ruben.takeme;

import android.graphics.PointF;

import java.util.Random;

/**
 * Created by ruben on 19/6/15.
 */
public class Utils {

    public static final int dogId = 1;
    static public final int catId = 2;

    public static final int boxerId = 1;
    static public final int goldenId = 2;
    static public final int germanId = 3;
    static public final int dobermanId = 4;
    static public final int esgypId = 5;
    static public final int somaliId = 6;
    static public final int siberianId = 7;
    static public final int russianId = 8;

    public static float getRandomFloatBetween(float x, float y)
    {
        Random r  = new Random();
        return r.nextFloat() * (x - y) + x;
    }

    public static int getRandomIntBetween(int x, int y)
    {
        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((y - x) + 1) + x;

        return randomNum;
    }

    public static PointF getRandomCordinates()
    {
        PointF p = new PointF();

        float latitude = getRandomFloatBetween(40.665899f,42.425331f);
        float longitude = getRandomFloatBetween(4.099892f, 5.3529174f);

        p.set(latitude,-longitude);

        return p;

    }

    public static int getBreedIconRrcWithId(int id) {
        switch (id) {
            case (boxerId):
                return R.drawable.boxer;

            case (goldenId):
                return R.drawable.golden;

            case (germanId):
                return R.drawable.german_shepherd;

            case (dobermanId):
                return R.drawable.doberman;

            case (somaliId):
                return R.drawable.somali;

            case (russianId):
                return R.drawable.russian;

            case (esgypId):
                return R.drawable.egypt;

            case (siberianId):
                return R.drawable.siberian;

        }
        return R.drawable.boxer;
    }

    public static int getAnimalNameRrcWithId(int id) {
        switch (id) {
            case (dogId):
                return  R.string.Dog;

            case (catId):
                return R.string.Cat;

        }
        return R.string.Dog;
    }

    public static int getBreedNameRrcWithId(int id) {
        switch (id) {
            case (boxerId):
                return R.string.Boxer;

            case (goldenId):
                return R.string.Golden;

            case (germanId):
                return R.string.German_Shepard;
            case (dobermanId):
                return R.string.Doberman;
            case (somaliId):
                return R.string.Somali;
            case (russianId):
                return R.string.Russian_Blue;

            case (esgypId):
                return R.string.Egyptian;

            case (siberianId):
                return R.string.Siberian;

        }
        return R.string.Siberian;
    }
}
