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
import android.content.res.Resources;
import android.content.Context;
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
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
         OnPreferenceChangeListener, Indexable {

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
