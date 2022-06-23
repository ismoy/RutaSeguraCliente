package cl.rutasegura.rutaseguracliente.DataProccessor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class DataProccessor {
    private static Context context;

    public DataProccessor(Context context){
        DataProccessor.context = context;
    }

    public final static String PREFS_NAME = "appname_prefs";

    public static void setInt( String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(key, 0);
    }

    public static void setStr(String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
//        Toast.makeText(context, "string add", Toast.LENGTH_SHORT).show();
    }

    public static String getStr(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(key,"");
    }

    public static void setBool(String key, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBool(String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(key,false);
    }

    @SuppressLint("CommitPrefEdits")
    public static void clears() {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        sharedPref.edit().clear().apply();
    }
}