/*
 * Copyright (C) 2025 crDroid Android Project
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
package com.crdroid.settings.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ServiceManager
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AlertDialog

import com.android.internal.statusbar.IStatusBarService
import com.android.settings.R
import com.android.internal.util.crdroid.Utils

object SystemUtils {

    private const val TAG = "SystemUtils"

    @JvmStatic
    fun showSystemUiRestartDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.systemui_restart_title)
            .setMessage(R.string.systemui_restart_message)
            .setPositiveButton(R.string.systemui_restart_yes) { _, _ ->
                restartSystemUI(context)
            }
            .setNegativeButton(R.string.systemui_restart_not_now, null)
            .show()
    }

    @JvmStatic
    fun restartSystemUI(context: Context) {
        Toast.makeText(
            context,
            R.string.systemui_restart_process,
            Toast.LENGTH_LONG
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            Utils.restartSystemUI()
        }, 2000) // 2-second delay
    }

    @JvmStatic
    fun showRebootDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.reboot_required_title)
            .setMessage(R.string.reboot_required_message)
            .setPositiveButton(R.string.reboot_device) { _, _ ->
                rebootDevice(context)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    @JvmStatic
    fun rebootDevice(context: Context) {
        try {
            val binder = ServiceManager.getService("statusbar")
            val statusBarService = IStatusBarService.Stub.asInterface(binder)
            statusBarService.reboot(false, "settings_change")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reboot device via statusbar service", e)
        }
    }
} 
