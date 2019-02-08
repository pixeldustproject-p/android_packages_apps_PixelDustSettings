package com.pixeldust.settings.fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PDGestureSettings extends SettingsPreferenceFragment {

    private static final String ACTIVE_EDGE_CATEGORY = "active_edge_category";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_gestures);

        Preference ActiveEdge = findPreference(ACTIVE_EDGE_CATEGORY);
        if (!getResources().getBoolean(R.bool.has_active_edge)) {
            getPreferenceScreen().removePreference(ActiveEdge);
        } else {
            if (!getContext().getPackageManager().hasSystemFeature(
                    "android.hardware.sensor.assist")) {
                getPreferenceScreen().removePreference(ActiveEdge);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
