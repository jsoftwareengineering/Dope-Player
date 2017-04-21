package com.example.jamesb.dopeplayer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

    private static final String ACCESS_TOKEN_NAME = "webapi.credentials.access_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_AT = "expires_at";

    public static void setToken(Context context, String token, TimeUnit unit) {
        Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, "BQCjzhw1_hl9QvKx4ZZ_Q68QPt_8QeWx393wMCMZjAAq7EW2PDiZp0Kpoco3B8MmKVXuEINMzZDQNcXtIWgPxuSIEvX61lwmw2-RnjpPIpFiniWTMkSgaxjcEhP_V79fWUKFTp9Amx0SSHZAHOG-FcbcEuL8");
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(ACCESS_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) {
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        String token = sharedPref.getString(ACCESS_TOKEN, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        return token;
    }
}
