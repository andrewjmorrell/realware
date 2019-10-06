package com.pivot.pivot360.pivoteye

import android.content.Context

public object PreferenceUtil {

    private val PREF = "PivotAppPref"
    private val UNIQUE_ID = "uniqueId"
    private val ACCESS_TOKEN = "accessToken"
    private val ACCESS_TOKEN_MOXTRA = "accessTokenMoxtra"

    fun saveUserUniqueIdentity(context: Context, uniqueId: String) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString(UNIQUE_ID, uniqueId).apply()
    }

    fun removeUser(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.remove(UNIQUE_ID).apply()
    }

    /*public static MoxieUser getUserUniqueIdentiry(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String email = sp.getString(UNIQUE_ID, null);
        if (email == null) {
            return null;
        }
        return DummyData.findByUniqueId(email);
    }*/

    fun getUserUniqueIdentiry(context: Context): String? {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString(UNIQUE_ID, "")

    }

    fun saveAccessToken(context: Context, Token: String?) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString(ACCESS_TOKEN, Token).apply()
    }

    fun saveMoxtraAccessToken(context: Context, Token: String?) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString(ACCESS_TOKEN_MOXTRA, Token).apply()
    }

    fun removeAccessToken(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString(ACCESS_TOKEN, "").apply()
    }

    fun getToken(context: Context): String? {
        println("AccessToken........>$ACCESS_TOKEN")
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString(ACCESS_TOKEN, "")
    }

    fun SaveFirebaseToken(context: Context, FToken: String?) {
        println("firebasetoken........>$ACCESS_TOKEN")
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString("FirebaseToken", FToken).apply()
    }

    fun getFirebaseTokenToken(context: Context): String? {
        println("AccessToken........>$ACCESS_TOKEN")
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getString("FirebaseToken", "")
    }

    fun removeFirebaseTokenToken(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val ed = sp.edit()
        ed.putString("FirebaseToken", "").apply()
    }
}
