package com.dentist.konselorhalodent.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {
    private static final String KEY_GROUP_ID    ="group_id";
    private static final String KEY_GROUP_NAME  ="group_name";
    private static final String KEY_GROUP_PHOTO ="group_photo";
    private static final String KEY_DOKTER_ID = "dokter_id";

    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setKeyDokterId(Context context, String dokterId){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_DOKTER_ID, dokterId);
        editor.apply();
    }

    public static String getKeyDokterId(Context context){
        return getSharedPreference(context).getString(KEY_DOKTER_ID,"");
    }


    public static void setKeyGroupId(Context context, String groupId){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_ID, groupId);
        editor.apply();
    }

    public static String getKeyGroupId(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_ID,"");
    }

    public static void setKeyGroupName(Context context, String groupName){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_NAME, groupName);
        editor.apply();
    }

    public static String getKeyGroupName(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_NAME,"");
    }

    public static void setKeyGroupPhoto(Context context, String groupPhoto){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_GROUP_PHOTO, groupPhoto);
        editor.apply();
    }

    public static String getKeyGroupPhoto(Context context){
        return getSharedPreference(context).getString(KEY_GROUP_PHOTO,"");
    }

    //Deklarasi Edit Preferences dan menghapus data, sehingga menjadikannya bernilai default
    public static void removeGroupData(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_GROUP_ID);
        editor.remove(KEY_GROUP_NAME);
        editor.remove(KEY_GROUP_PHOTO);
        editor.apply();
    }

}
