// إنشاء ملف جديد: LogoutHelper.java
package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class LogoutHelper {

    public static void logout(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}