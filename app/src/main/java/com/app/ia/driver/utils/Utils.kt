/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.ia.driver.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.location.Location
import android.preference.PreferenceManager
import java.text.DateFormat
import java.util.*


object Utils {

    private const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    @JvmStatic
    fun requestingLocationUpdates(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    @JvmStatic
    fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     *
     * @param location The [Location].
     */
    @JvmStatic
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context?): String {
        return "Location Updated " + DateFormat.getDateTimeInstance().format(Date())
    }

    /**
     * schedule job once
     *
     * @param context
     * @param cls
     */
    fun scheduleJob(context: Context, cls: Class<*>?) {
        val serviceComponent = ComponentName(context, cls!!)
        val builder = JobInfo.Builder(0, serviceComponent)
        builder.setMinimumLatency(1 * 1000.toLong()) // wait at least
        builder.setOverrideDeadline(3 * 1000.toLong()) // maximum delay
        //        builder.setPeriodic(5 * 1000);
        builder.setRequiresCharging(false) // we don't care if the device is charging or not
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    /**
     * schedule job always in every 5 seconds, can be parametrized time - ie. pass time in parameter
     *
     * @param context
     * @param cls
     */
    fun scheduleJob1(context: Context, cls: Class<*>?) {
        val serviceComponent = ComponentName(context, cls!!)
        val builder = JobInfo.Builder(0, serviceComponent)
        builder.setPeriodic(5 * 1000.toLong())
        builder.setRequiresCharging(false) // we don't care if the device is charging or not
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

}