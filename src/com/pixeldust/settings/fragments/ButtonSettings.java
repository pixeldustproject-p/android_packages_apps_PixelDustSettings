package com.pixeldust.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

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
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.CustomSeekBarPreference;

public class ButtonSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private SwitchPreference mKillAppLongPressBack;

    private static final String LONG_PRESS_KILL_DELAY = "long_press_kill_delay";
    private CustomSeekBarPreference mLongpressKillDelay;

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";
    private ListPreference mTorchPowerButton;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_button);
        ContentResolver resolver = getActivity().getContentResolver();

        // kill-app long press back
        mKillAppLongPressBack = (SwitchPreference) findPreference(KILL_APP_LONGPRESS_BACK);
        mKillAppLongPressBack.setOnPreferenceChangeListener(this);
        int killAppLongPressBack = Settings.Secure.getInt(resolver,
                KILL_APP_LONGPRESS_BACK, 0);
        mKillAppLongPressBack.setChecked(killAppLongPressBack != 0);

        // kill-app long press back delay
        mLongpressKillDelay = (CustomSeekBarPreference) findPreference(LONG_PRESS_KILL_DELAY);
        int killconf = Settings.System.getInt(resolver,
                Settings.System.LONG_PRESS_KILL_DELAY, 1000);
        mLongpressKillDelay.setValue(killconf);
        mLongpressKillDelay.setOnPreferenceChangeListener(this);

        // screen off torch
        mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
        int mTorchPowerButtonValue = Settings.Secure.getInt(resolver,
                Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0);
        mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
        mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
        mTorchPowerButton.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean DoubleTapPowerGesture = Settings.Secure.getInt(resolver,
                    Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED, 1) == 0;
        if (preference == mKillAppLongPressBack) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver,
		KILL_APP_LONGPRESS_BACK, value ? 1 : 0);
            return true;
        } else if (preference == mLongpressKillDelay) {
            int killconf = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.LONG_PRESS_KILL_DELAY, killconf);
            return true;
        } else if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.Secure.putInt(resolver, Settings.Secure.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            if (mTorchPowerButtonValue == 1 && DoubleTapPowerGesture) {
                //if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(resolver, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        1);
                Toast.makeText(getActivity(),
                    (R.string.torch_power_button_gesture_dt_toast),
                    Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
