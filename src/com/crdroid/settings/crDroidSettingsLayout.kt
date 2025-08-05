/*
 * SPDX-FileCopyrightText: crDroid Android Project
 * SPDX-License-Identifier: GPL-3.0
 */

package com.crdroid.settings

import com.android.internal.logging.nano.MetricsProto.MetricsEvent
import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.android.settingslib.search.SearchIndexable

@SearchIndexable
class crDroidSettingsLayout : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.crdroid_settings

    override fun getMetricsCategory() = MetricsEvent.CRDROID_SETTINGS

    override fun getLogTag() = TAG

    companion object {
        private const val TAG = "crDroidSettingsLayout"

        @JvmField
        val SEARCH_INDEX_DATA_PROVIDER =
            BaseSearchIndexProvider(R.xml.crdroid_settings)
    }
}
