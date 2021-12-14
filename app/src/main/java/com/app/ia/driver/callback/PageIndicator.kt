package com.app.ia.driver.callback

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

interface PageIndicator : OnPageChangeListener {
    /**
     * Bind the indicator to a ViewPager.
     *
     * @param view viewpager view
     */
    fun setViewPager(view: ViewPager?)

    /**
     * Bind the indicator to a ViewPager.
     *
     * @param view view pager view
     * @param initialPosition initial position
     */
    fun setViewPager(view: ViewPager?, initialPosition: Int)

    /**
     *
     * Set the current page of both the ViewPager and indicator.
     *
     *
     * This **must** be used if you need to set the page before
     * the views are drawn on screen (e.g., default start page).
     *
     * @param item current page
     */
    fun setCurrentItem(item: Int)

    /**
     * Set a page change listener which will receive forwarded events.
     *
     * @param listener page change listener
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener?)

    /**
     * Notify the indicator that the fragment list has changed.
     */
    fun notifyDataSetChanged()
}