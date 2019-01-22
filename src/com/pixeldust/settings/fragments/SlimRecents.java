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
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;

import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.SystemSettingSwitchPreference;
import com.pixeldust.support.preferences.IconPackPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SlimRecents extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String RECENT_PANEL_LEFTY_MODE = "recent_panel_lefty_mode";

    private static final String RECENT_ICON_PACK = "slim_icon_pack";

    private SwitchPreference mRecentPanelLeftyMode;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.slim_recents);

        mRecentPanelLeftyMode = (SwitchPreference) findPreference(RECENT_PANEL_LEFTY_MODE);
        mRecentPanelLeftyMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean recentLeftyMode = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.RECENT_PANEL_GRAVITY, Gravity.END) == Gravity.START;
        mRecentPanelLeftyMode.setChecked(recentLeftyMode);

        IconPackPreference iconPackPref = (IconPackPreference) findPreference(RECENT_ICON_PACK);
        // Re-initialise preference
        iconPackPref.init();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentPanelLeftyMode) {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) newValue) ? Gravity.START : Gravity.END);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
