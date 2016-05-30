package com.killerrech.sharedPrefrences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cratuz on 14/10/15.
 */
public class SharedPrefrence {
    private static final String SHAREDPREFRENCE_NAME="geoutility";


    public static void saveStringSharedPrefernces(Context ctx,String key, String value) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        SharedPreferences.Editor toEdit = sh_Pref.edit();
        toEdit.putString(key, value);
        toEdit.commit();

    }

    public static String getStringSharedPrefernces(Context ctx,String key) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
       return sh_Pref.getString(key,"");

    }

    public static void saveIntSharedPrefernces(Context ctx,String key, int value) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        SharedPreferences.Editor toEdit = sh_Pref.edit();
        toEdit.putInt(key, value);
        toEdit.commit();

    }


    public static int getIntSharedPrefernces(Context ctx,String key) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        return sh_Pref.getInt(key, 0);

    }

    public static void saveBooleanSharedPrefernces(Context ctx,String key, boolean value) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        SharedPreferences.Editor toEdit = sh_Pref.edit();
        toEdit.putBoolean(key, value);
        toEdit.commit();

    }


    public static boolean getBooleanSharedPrefernces(Context ctx,String key) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        return sh_Pref.getBoolean(key, false);

    }

    public static void saveFloatSharedPrefernces(Context ctx,String key, float value) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        SharedPreferences.Editor toEdit = sh_Pref.edit();
        toEdit.putFloat(key, value);
        toEdit.commit();

    }

    public static float getFloatSharedPrefernces(Context ctx,String key) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        return sh_Pref.getFloat(key, 0);

    }

    public static void saveLongSharedPrefernces(Context ctx,String key, long value) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        SharedPreferences.Editor toEdit = sh_Pref.edit();
        toEdit.putLong(key, value);
        toEdit.commit();

    }

    public static long getLongSharedPrefernces(Context ctx,String key) {

        SharedPreferences sh_Pref = ctx.getSharedPreferences(SHAREDPREFRENCE_NAME, ctx.MODE_PRIVATE);
        return sh_Pref.getLong(key, 0);

    }
}
