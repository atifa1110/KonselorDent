package com.dentist.konselorhalodent.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

    //pendeklarasian key-data berupa String, untuk sebagai wadah penyimpanan data
    //jadi setiap data mempunyai key yang berbeda satu sama lain
    private static final String KEY_GROUP_ID    ="group_id";
    private static final String KEY_GROUP_NAME  ="group_name";
    private static final String KEY_GROUP_PHOTO ="group_photo";

    private static final String KEY_BUTTON_CLICK = "isButtonClick";

    private static final String KEY_JADWAL_FROM = "jadwal_from";
    private static final String KEY_JADWAL_TO = "jadwal_to";

    private static final String KEY_DOKTER_ID = "dokter_id";

    //deklarasi shared preference berdasarkan context
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

    public static void setKeyJadwalFrom(Context context, String jadwal_from){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_JADWAL_FROM, jadwal_from);
        editor.apply();
    }

    public static String getKeyJadwalFrom(Context context){
        return getSharedPreference(context).getString(KEY_JADWAL_FROM,"");
    }

    public static void setKeyJadwalTo(Context context, String jadwal_to){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_JADWAL_TO, jadwal_to);
        editor.apply();
    }

    public static String getKeyJadwalTo(Context context){
        return getSharedPreference(context).getString(KEY_JADWAL_TO,"");
    }

    public static void setKeyButtonClick(Context context, Boolean isButtonClickBefore){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(KEY_BUTTON_CLICK, isButtonClickBefore);
        editor.apply();
    }

    public static Boolean getKeyButtonClick(Context context){
        return getSharedPreference(context).getBoolean(KEY_BUTTON_CLICK,false);
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
