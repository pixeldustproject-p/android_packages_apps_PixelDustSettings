package com.pixeldust.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.SystemSettingSwitchPreference;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
         OnPreferenceChangeListener {

    private static final String NAVIGATION_BAR_SHOW = "navigation_bar_show";
    private static final String USE_BOTTOM_GESTURE_NAVIGATION = "use_bottom_gesture_navigation";

    private SystemSettingSwitchPreference mNavigationBarShow;
    private SystemSettingSwitchPreference mUseBottomGestureNavigation;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_navigation);

        // navigation bar show
        mNavigationBarShow = (SystemSettingSwitchPreference) findPreference(NAVIGATION_BAR_SHOW);
        mNavigationBarShow.setOnPreferenceChangeListener(this);
        int navigationBarShow = Settings.System.getInt(getContentResolver(),
                NAVIGATION_BAR_SHOW, 0);
        mNavigationBarShow.setChecked(navigationBarShow != 0);
         // use bottom gestures
        mUseBottomGestureNavigation = (SystemSettingSwitchPreference) findPreference(USE_BOTTOM_GESTURE_NAVIGATION);
        mUseBottomGestureNavigation.setOnPreferenceChangeListener(this);
        int useBottomGestureNavigation = Settings.System.getInt(getContentResolver(),
                USE_BOTTOM_GESTURE_NAVIGATION, 0);
        mUseBottomGestureNavigation.setChecked(useBottomGestureNavigation != 0);

        updateNavigationBarOptions();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavigationBarShow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		NAVIGATION_BAR_SHOW, value ? 1 : 0);
            updateNavigationBarOptions();
            return true;
        } else if (preference == mUseBottomGestureNavigation) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
		USE_BOTTOM_GESTURE_NAVIGATION, value ? 1 : 0);
            updateNavigationBarOptions();
            return true;
        }
        return false;
    }

    private void updateNavigationBarOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.NAVIGATION_BAR_SHOW, 0) == 0) {
            mUseBottomGestureNavigation.setEnabled(true);
        } else {
            mUseBottomGestureNavigation.setEnabled(false);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
