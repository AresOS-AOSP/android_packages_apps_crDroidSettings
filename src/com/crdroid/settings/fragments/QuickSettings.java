/*
 * Copyright (C) 2016-2026 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crdroid.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreferenceCompat;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.crdroid.settings.fragments.quicksettings.LayoutSettings;
import com.crdroid.settings.preferences.CustomSeekBarPreference;
import com.crdroid.settings.preferences.SystemSettingSwitchPreference;
import com.crdroid.settings.preferences.SystemSettingListPreference;
import com.crdroid.settings.utils.DeviceUtils;
import com.crdroid.settings.utils.SystemUtils;

import lineageos.providers.LineageSettings;

import java.util.List;
import java.util.ArrayList;

@SearchIndexable
public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "QuickSettings";

    private static final String QS_BRIGHTNESS_CATEGORY = "qs_brightness_slider_category";
    private static final String QS_LAYOUT_CATEGORY = "qs_layout_category";
    private static final String KEY_SHOW_BRIGHTNESS_SLIDER = "qs_show_brightness_slider";
    private static final String KEY_BRIGHTNESS_SLIDER_POSITION = "qs_brightness_slider_position";
    private static final String KEY_BRIGHTNESS_SLIDER_HAPTIC = "qs_brightness_slider_haptic";
    private static final String KEY_SHOW_AUTO_BRIGHTNESS = "qs_show_auto_brightness";
    private static final String KEY_SHOW_VOLUME_SLIDER = "qs_show_volume_slider";
    private static final String KEY_SHOW_RINGER_MODE = "qs_show_ringer_mode";
    private static final String KEY_QS_TILE_HAPTIC = "qs_tile_haptic";
    private static final String KEY_QS_PANEL_STYLE = "qs_panel_style";
    private static final String KEY_QS_TILE_SHAPE = "qs_tile_shape";
    private static final String KEY_QS_TILE_ICON_SHAPE = "qs_tile_icon_shape";
    private static final String KEY_QS_TILE_LABEL_HIDE = "qs_tile_label_hide";
    private static final String KEY_SINGLE_QS_TONE_ENABLED = "single_qs_tone_enabled";
    private static final String KEY_DUAL_TARGET_TILE_STYLE = "dual_target_tile_style";
    private static final String KEY_QS_TILE_ALTERNATE_COLOR = "qs_tile_alternate_color";
    private static final String KEY_QS_TILE_STYLE_MINIMAL = "qs_tile_style_minimal";
    private static final String KEY_QS_TILE_STYLE_MINIMAL_INVERT = "qs_tile_style_minimal_invert";
    private static final String KEY_QS_USE_MODIFIED_TILE_SPACING = "qs_use_modified_tile_spacing";
    private static final String KEY_BRIGHTNESS_SLIDER_STYLE = "qs_brightness_slider_style";
    private static final String KEY_BRIGHTNESS_SLIDER_SHAPE = "qs_brightness_slider_shape";

    private ListPreference mShowBrightnessSlider;
    private ListPreference mVolumeSliderMode;
    private ListPreference mBrightnessSliderPosition;
    private SwitchPreferenceCompat mBrightnessSliderHaptic;
    private SwitchPreferenceCompat mShowAutoBrightness;
    private SwitchPreferenceCompat mShowRingerMode;
    private SystemSettingSwitchPreference mBrightnessSliderStyle;
    private SystemSettingListPreference mBrightnessSliderShape;
    private SwitchPreferenceCompat mQsTileHaptic;
    private ListPreference mQsPanelStyle;
    private Preference mQsTileIconShape;
    private SwitchPreferenceCompat mQsTileLabelHide;
    private SystemSettingSwitchPreference mSingleQsToneEnabled;
    private SystemSettingSwitchPreference mDualTargetTileStyle;
    private SwitchPreferenceCompat mQsTileAlternateColor;
    private SystemSettingSwitchPreference mQsTileStyleMinimal;
    private SystemSettingSwitchPreference mQsTileStyleMinimalInvert;
    private SystemSettingSwitchPreference mQsUseModifiedTileSpacing;
    private SystemSettingListPreference mQsTileShape;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.crdroid_settings_quicksettings);

        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();

        PreferenceCategory brightnessCategory = (PreferenceCategory) findPreference(QS_BRIGHTNESS_CATEGORY);
        PreferenceCategory tileCategory = (PreferenceCategory) findPreference(QS_LAYOUT_CATEGORY);

        mShowBrightnessSlider = findPreference(KEY_SHOW_BRIGHTNESS_SLIDER);
        mShowBrightnessSlider.setOnPreferenceChangeListener(this);
        boolean showSlider = LineageSettings.Secure.getIntForUser(resolver,
                LineageSettings.Secure.QS_SHOW_BRIGHTNESS_SLIDER, 1, UserHandle.USER_CURRENT) > 0;

        mVolumeSliderMode = findPreference(KEY_SHOW_VOLUME_SLIDER);
        mVolumeSliderMode.setEnabled(showSlider);

        mBrightnessSliderPosition = findPreference(KEY_BRIGHTNESS_SLIDER_POSITION);
        mBrightnessSliderPosition.setEnabled(showSlider);

        mBrightnessSliderHaptic = findPreference(KEY_BRIGHTNESS_SLIDER_HAPTIC);
        mQsTileHaptic = findPreference(KEY_QS_TILE_HAPTIC);
        boolean hapticAvailable = DeviceUtils.hasVibrator(context);

        if (hapticAvailable) {
            mBrightnessSliderHaptic.setEnabled(showSlider);
        } else {
            brightnessCategory.removePreference(mBrightnessSliderHaptic);
            tileCategory.removePreference(mQsTileHaptic);
        }

        mShowAutoBrightness = findPreference(KEY_SHOW_AUTO_BRIGHTNESS);
        boolean automaticAvailable = context.getResources().getBoolean(
                com.android.internal.R.bool.config_automatic_brightness_available);

        if (automaticAvailable) {
            mShowAutoBrightness.setEnabled(showSlider);
        } else {
            brightnessCategory.removePreference(mShowAutoBrightness);
        }

        mShowRingerMode = findPreference(KEY_SHOW_RINGER_MODE);
        mShowRingerMode.setEnabled(showSlider);

        mBrightnessSliderStyle = findPreference(KEY_BRIGHTNESS_SLIDER_STYLE);
        mBrightnessSliderShape = findPreference(KEY_BRIGHTNESS_SLIDER_SHAPE);

        if (mBrightnessSliderStyle != null) {
            mBrightnessSliderStyle.setOnPreferenceChangeListener(this);
            updateBrightnessSliderStyleDependencies();
        }

        mQsPanelStyle = findPreference(KEY_QS_PANEL_STYLE);
        mQsPanelStyle.setOnPreferenceChangeListener(this);
        mQsTileShape = findPreference(KEY_QS_TILE_SHAPE);
        mQsTileIconShape = findPreference(KEY_QS_TILE_ICON_SHAPE);
        mQsTileLabelHide = findPreference(KEY_QS_TILE_LABEL_HIDE);

        int panelStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE, 0, UserHandle.USER_CURRENT);
        updatePanelStylePrefs(panelStyle);

        mSingleQsToneEnabled = (SystemSettingSwitchPreference) findPreference(KEY_SINGLE_QS_TONE_ENABLED);
        if (mSingleQsToneEnabled != null) {
            mSingleQsToneEnabled.setOnPreferenceChangeListener(this);
        }

        mDualTargetTileStyle = findPreference(KEY_DUAL_TARGET_TILE_STYLE);
        if (mDualTargetTileStyle != null) {
            mDualTargetTileStyle.setOnPreferenceChangeListener(this);
        }

        mQsTileAlternateColor = findPreference(KEY_QS_TILE_ALTERNATE_COLOR);
        if (mQsTileAlternateColor != null) {
            mQsTileAlternateColor.setOnPreferenceChangeListener(this);
        }

        mQsUseModifiedTileSpacing = findPreference(KEY_QS_USE_MODIFIED_TILE_SPACING);
        if (mQsUseModifiedTileSpacing != null) {
            mQsUseModifiedTileSpacing.setOnPreferenceChangeListener(this);
        }

        mQsTileStyleMinimal = findPreference(KEY_QS_TILE_STYLE_MINIMAL);
        mQsTileStyleMinimalInvert = findPreference(KEY_QS_TILE_STYLE_MINIMAL_INVERT);
        mQsTileShape = findPreference(KEY_QS_TILE_SHAPE);

        if (mQsTileStyleMinimal != null) {
            mQsTileStyleMinimal.setOnPreferenceChangeListener(this);
            boolean isMinimalEnabled = Settings.System.getInt(resolver,
                    KEY_QS_TILE_STYLE_MINIMAL, 0) == 1;
            updateMinimalStyleDependencies(isMinimalEnabled);
        }
    }

    private void updatePanelStylePrefs(int panelStyle) {
        boolean isClassic = panelStyle == 1;

        if (mQsTileShape != null) {
            mQsTileShape.setVisible(!isClassic);
        }
        if (mQsTileIconShape != null) {
            mQsTileIconShape.setVisible(isClassic);
        }
        if (mQsTileLabelHide != null) {
            mQsTileLabelHide.setVisible(isClassic);
        }
    }

    private void updateMinimalStyleDependencies(boolean isMinimalEnabled) {
        if (mQsTileStyleMinimal == null) return;

        ContentResolver resolver = getContext().getContentResolver();

        if (mQsTileStyleMinimalInvert != null) {
            mQsTileStyleMinimalInvert.setVisible(isMinimalEnabled);
        }

        if (mQsPanelStyle != null) {
            mQsPanelStyle.setEnabled(!isMinimalEnabled);
            if (isMinimalEnabled) {
                mQsPanelStyle.setOnPreferenceChangeListener(null);
                Settings.System.putIntForUser(resolver,
                        Settings.System.QS_PANEL_STYLE, 0,
                        UserHandle.USER_CURRENT);
                mQsPanelStyle.setValue(String.valueOf(0));
                mQsPanelStyle.setOnPreferenceChangeListener(this);
                updatePanelStylePrefs(0);
            }
       }

        if (mDualTargetTileStyle != null) {
            mDualTargetTileStyle.setVisible(!isMinimalEnabled);
        }

        if (mQsTileShape != null) {
            mQsTileShape.setVisible(!isMinimalEnabled);
        }
    }

    private void updateBrightnessSliderStyleDependencies() {
        if (mBrightnessSliderStyle == null) return;

        ContentResolver resolver = getContext().getContentResolver();
        boolean isSliderStyleEnabled = Settings.System.getInt(resolver,
                KEY_BRIGHTNESS_SLIDER_STYLE, 0) == 1;

        if (mBrightnessSliderShape != null) {
            mBrightnessSliderShape.setVisible(!isSliderStyleEnabled);
        }

        if (mShowAutoBrightness != null) {
            boolean automaticAvailable = getContext().getResources().getBoolean(
                    com.android.internal.R.bool.config_automatic_brightness_available);
            if (automaticAvailable) {
                mShowAutoBrightness.setVisible(!isSliderStyleEnabled);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getContext().getContentResolver();

        if (preference == mShowBrightnessSlider) {
            int value = Integer.parseInt((String) newValue);
            mBrightnessSliderPosition.setEnabled(value > 0);
            mVolumeSliderMode.setEnabled(value > 0);
            if (mBrightnessSliderHaptic != null)
                mBrightnessSliderHaptic.setEnabled(value > 0);
            if (mShowAutoBrightness != null)
                mShowAutoBrightness.setEnabled(value > 0);
            mShowRingerMode.setEnabled(value > 0);
            updateBrightnessSliderStyleDependencies();
            return true;
        } else if (preference == mQsPanelStyle) {
            int value = Integer.parseInt((String) newValue);
            updatePanelStylePrefs(value);
            return true;
        } else if (preference == mSingleQsToneEnabled) {
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        } else if (preference == mDualTargetTileStyle) {
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        } else if (preference == mQsTileAlternateColor) {
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        } else if (preference == mQsUseModifiedTileSpacing) {
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        } else if (preference == mQsTileStyleMinimal) {
            updateMinimalStyleDependencies((Boolean) newValue);
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        } else if (preference == mBrightnessSliderStyle) {
            updateBrightnessSliderStyleDependencies();
            SystemUtils.showSystemUiRestartDialog(getActivity());
            return true;
        }
        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.ENABLE_LOCKSCREEN_QUICK_SETTINGS, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_HAPTIC, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_BRIGHTNESS_SLIDER_SHAPE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_FOOTER_SHOW_SETTINGS, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_FOOTER_SHOW_EDIT, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_FOOTER_SHOW_POWER_MENU, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_SHOW_DATA_USAGE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_TILE_HAPTIC, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_TILE_SHAPE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_TILE_ANIMATION_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_TILE_LABEL_HIDE, 0, UserHandle.USER_CURRENT);
        Settings.System.putStringForUser(resolver,
                Settings.System.QS_TILE_ICON_SHAPE, "circle", UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_SHOW_VOLUME_SLIDER, 1, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.QS_SHOW_RINGER_MODE, 1, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.QS_SHOW_BRIGHTNESS_SLIDER, 1, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.QS_BRIGHTNESS_SLIDER_POSITION, 0, UserHandle.USER_CURRENT);
        LineageSettings.Secure.putIntForUser(resolver,
                LineageSettings.Secure.QS_SHOW_AUTO_BRIGHTNESS, 1, UserHandle.USER_CURRENT);
        LayoutSettings.reset(mContext);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.crdroid_settings_quicksettings) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();
                    final ContentResolver resolver = context.getContentResolver();

                    boolean automaticAvailable = res.getBoolean(
                            com.android.internal.R.bool.config_automatic_brightness_available);
                    if (!automaticAvailable) {
                        keys.add(KEY_SHOW_AUTO_BRIGHTNESS);
                    }

                    boolean hapticAvailable = DeviceUtils.hasVibrator(context);
                    if (!hapticAvailable) {
                        keys.add(KEY_BRIGHTNESS_SLIDER_HAPTIC);
                        keys.add(KEY_QS_TILE_HAPTIC);
                    }

                    boolean isMinimalEnabled = Settings.System.getInt(resolver,
                            KEY_QS_TILE_STYLE_MINIMAL, 0) == 1;

                    if (!isMinimalEnabled) {
                        keys.add(KEY_QS_TILE_STYLE_MINIMAL_INVERT);
                    }

                    if (isMinimalEnabled) {
                        keys.add(KEY_QS_TILE_SHAPE);
                        keys.add(KEY_DUAL_TARGET_TILE_STYLE);
                    }

                    boolean isSliderStyleEnabled = Settings.System.getInt(resolver,
                            KEY_BRIGHTNESS_SLIDER_STYLE, 0) == 1;

                    if (isSliderStyleEnabled) {
                        keys.add(KEY_BRIGHTNESS_SLIDER_SHAPE);
                        keys.add(KEY_SHOW_AUTO_BRIGHTNESS);
                    }

                    return keys;
                }
            };
}
