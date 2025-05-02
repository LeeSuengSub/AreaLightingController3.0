package net.woorisys.lighting.control3.admin.sjp.usb.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil {

	public static void putSharedPreference(Context context, String key, int value) {
		SharedPreferences prefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.apply();  // commit()도 가능하지만 apply()는 비동기
	}

	public static void putSharedPreference(Context context, String key, boolean value) {
		SharedPreferences prefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static int getSharedPreference(Context context, String key, int def) {
		SharedPreferences prefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
		return prefs.getInt(key, def);
	}

	public static boolean getSharedPreferenceAsBoolean(Context context, String key, boolean def) {
		SharedPreferences prefs = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
		return prefs.getBoolean(key, def);
	}

}
