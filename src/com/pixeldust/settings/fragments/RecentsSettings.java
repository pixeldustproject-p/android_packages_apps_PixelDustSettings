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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.internal.util.pixeldust.PixeldustUtils;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.FooterPreference;

import com.pixeldust.support.preferences.IconPackPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentsSettings extends SettingsPreferenceFragment implements Indexable, OnPreferenceChangeListener {

    private static final String RECENTS_COMPONENT_TYPE = "recents_component";
    private static final String PREF_STOCK_HAFR = "hide_app_from_recents";
    private static final String PREF_STOCK_ICON_PACK = "recents_icon_pack";
    private static final String PREF_SLIM_RECENTS_SETTINGS = "slim_recents_settings";
    private static final String PREF_SLIM_RECENTS = "use_slim_recents";

    private ListPreference mRecentsComponentType;
    private SwitchPreference mSlimToggle;
    private Preference mSlimSettings;
    private Preference mStockHAFR;
    private Preference mStockIconPack;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_recents);

        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);

        mStockHAFR = (Preference) findPreference(PREF_STOCK_HAFR);
        mStockIconPack = (Preference) findPreference(PREF_STOCK_ICON_PACK);

        // Slim Recents
        mSlimSettings = (Preference) findPreference(PREF_SLIM_RECENTS_SETTINGS);
        mSlimToggle = (SwitchPreference) findPreference(PREF_SLIM_RECENTS);
        mSlimToggle.setOnPreferenceChangeListener(this);

        updateRecentsPreferences();
    }

    private void updateRecentsPreferences() {
        boolean slimEnabled = Settings.System.getIntForUser(
                getActivity().getContentResolver(), Settings.System.USE_SLIM_RECENTS, 0,
                UserHandle.USER_CURRENT) == 1;
        // Either Stock or Slim Recents can be active at a time
        mRecentsComponentType.setEnabled(!slimEnabled);
        mStockHAFR.setEnabled(!slimEnabled);
        mStockIconPack.setEnabled(!slimEnabled);
        mSlimToggle.setChecked(slimEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) objValue);
            int index = mRecentsComponentType.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            if (type == 1) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            PixeldustUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mSlimToggle) {
            boolean value = (Boolean) objValue;
            int type = Settings.System.getInt(
                getActivity().getContentResolver(), Settings.System.RECENTS_COMPONENT, 0);
            if (value && (type == 0)) { // change recents type to oreo when we are about to switch to slimrecents
               Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, 1);
               PixeldustUtils.showSystemUiRestartDialog(getContext());
            }
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.USE_SLIM_RECENTS, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            updateRecentsPreferences();
            return true;
        }
        return false;
    }

    public void onResume() {
        super.onResume();
        IconPackPreference iconPackPref = (IconPackPreference) findPreference(PREF_STOCK_ICON_PACK);
        // Re-initialise preference
        iconPackPref.init();
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
                    sir.xmlResId = R.xml.pixeldust_settings_recents;
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
