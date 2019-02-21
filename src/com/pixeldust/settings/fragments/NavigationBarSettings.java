/*
 * Copyright (C) 2018 The Pixel Dust Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pixeldust.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.utils.Config;
import com.android.internal.utils.Config.ButtonConfig;
import com.android.internal.utils.ActionConstants;
import com.android.internal.utils.ActionUtils;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.fragments.FlingSettings;
import com.pixeldust.settings.fragments.PulseSettings;
import com.pixeldust.settings.fragments.SmartbarSettings;
import com.pixeldust.settings.preferences.CustomSeekBarPreference;
import com.pixeldust.settings.preferences.SecureSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
         OnPreferenceChangeListener, Indexable {

    private static final String NAVIGATION_BAR_VISIBLE = "navigation_bar_visible";
    private static final String KEY_NAVBAR_MODE = "navigation_bar_mode";
    private static final String KEY_NAVIGATION_HEIGHT_PORT = "navbar_height_portrait";
    private static final String KEY_NAVIGATION_HEIGHT_LAND = "navbar_height_landscape";
    private static final String KEY_NAVIGATION_WIDTH = "navbar_width";
    private static final String KEY_CATEGORY_NAVIGATION_INTERFACE = "category_navbar_interface";
    private static final String KEY_CATEGORY_NAVIGATION_GENERAL = "category_navbar_general";
    private static final String KEY_STOCKNAVBAR_SETTINGS = "stocknavbar_settings";
    private static final String KEY_SMARTBAR_SETTINGS = "smartbar_settings";
    private static final String KEY_FLING_NAVBAR_SETTINGS = "fling_settings";

    private SecureSettingSwitchPreference mNavigationBarShow;
    private ListPreference mNavbarMode;
    private CustomSeekBarPreference mBarHeightPort;
    private CustomSeekBarPreference mBarHeightLand;
    private CustomSeekBarPreference mBarWidth;

    private Preference mStockSettings;
    private Preference mSmartbarSettings;
    private Preference mFlingSettings;

    private PreferenceCategory mNavInterface;
    private PreferenceCategory mNavGeneral;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_navigation);

        // navigation bar show
        mNavigationBarShow = (SecureSettingSwitchPreference) findPreference(NAVIGATION_BAR_VISIBLE);
        mNavigationBarShow.setOnPreferenceChangeListener(this);
        int navigationBarShow = Settings.Secure.getInt(getContentResolver(),
                NAVIGATION_BAR_VISIBLE, 0);
        mNavigationBarShow.setChecked(navigationBarShow != 0);

        mNavInterface = (PreferenceCategory) findPreference(KEY_CATEGORY_NAVIGATION_INTERFACE);
        mNavGeneral = (PreferenceCategory) findPreference(KEY_CATEGORY_NAVIGATION_GENERAL);

        int mode = Settings.Secure.getIntForUser(getContentResolver(), Settings.Secure.NAVIGATION_BAR_MODE,
                    0, UserHandle.USER_CURRENT);

        mStockSettings = (Preference) findPreference(KEY_STOCKNAVBAR_SETTINGS);
        mSmartbarSettings = (Preference) findPreference(KEY_SMARTBAR_SETTINGS);
        mFlingSettings = (Preference) findPreference(KEY_FLING_NAVBAR_SETTINGS);
        mNavbarMode = (ListPreference) findPreference(KEY_NAVBAR_MODE);

        updateBarModeSettings(mode);
        mNavbarMode.setOnPreferenceChangeListener(this);

        int size = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.NAVIGATION_BAR_HEIGHT, 100, UserHandle.USER_CURRENT);
        mBarHeightPort = (CustomSeekBarPreference) findPreference(KEY_NAVIGATION_HEIGHT_PORT);
        mBarHeightPort.setValue(size);
        mBarHeightPort.setOnPreferenceChangeListener(this);

        final boolean canMove = ActionUtils.navigationBarCanMove();
        if (canMove) {
            mNavInterface.removePreference(findPreference(KEY_NAVIGATION_HEIGHT_LAND));
            size = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_WIDTH, 100, UserHandle.USER_CURRENT);
            mBarWidth = (CustomSeekBarPreference) findPreference(KEY_NAVIGATION_WIDTH);
            mBarWidth.setValue(size);
            mBarWidth.setOnPreferenceChangeListener(this);
        } else {
            mNavInterface.removePreference(findPreference(KEY_NAVIGATION_WIDTH));
            size = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_HEIGHT_LANDSCAPE, 100, UserHandle.USER_CURRENT);
            mBarHeightLand = (CustomSeekBarPreference) findPreference(KEY_NAVIGATION_HEIGHT_LAND);
            mBarHeightLand.setValue(size);
            mBarHeightLand.setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNavigationBarShow) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getContentResolver(),
		NAVIGATION_BAR_VISIBLE, value ? 1 : 0);
            return true;
        } else if (preference == mNavbarMode) {
            int mode = Integer.parseInt((String) newValue);
            updateBarModeSettings(mode);
            return true;
        } else if (preference == mBarHeightPort) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(resolver,
                    Settings.Secure.NAVIGATION_BAR_HEIGHT, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBarHeightLand) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(resolver,
                    Settings.Secure.NAVIGATION_BAR_HEIGHT_LANDSCAPE, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBarWidth) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(resolver,
                    Settings.Secure.NAVIGATION_BAR_WIDTH, val, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    private void updateBarModeSettings(int mode) {
        switch (mode) {
            case 0:
                mStockSettings.setEnabled(true);
                mStockSettings.setSelectable(true);
                mSmartbarSettings.setEnabled(false);
                mSmartbarSettings.setSelectable(false);
                mFlingSettings.setEnabled(false);
                mFlingSettings.setSelectable(false);
                break;
            case 1:
                mStockSettings.setEnabled(false);
                mStockSettings.setSelectable(false);
                mSmartbarSettings.setEnabled(true);
                mSmartbarSettings.setSelectable(true);
                mFlingSettings.setEnabled(false);
                mFlingSettings.setSelectable(false);
                Settings.Secure.putInt(getContentResolver(),
	            Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
                Settings.System.putInt(getContentResolver(),
	            Settings.System.FULL_GESTURE_NAVBAR, 0);
                break;
            case 2:
                mStockSettings.setEnabled(false);
                mStockSettings.setSelectable(false);
                mSmartbarSettings.setEnabled(false);
                mSmartbarSettings.setSelectable(false);
                mFlingSettings.setEnabled(true);
                mFlingSettings.setSelectable(true);
                Settings.Secure.putInt(getContentResolver(),
	            Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
                Settings.System.putInt(getContentResolver(),
	            Settings.System.FULL_GESTURE_NAVBAR, 0);
                break;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.pixeldust_settings_navigation;
                    result.add(sir);
                    return result;
                }
                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
    };
}
