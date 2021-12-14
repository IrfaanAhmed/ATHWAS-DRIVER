package com.transferwise.sequencelayout

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import kotlinx.android.synthetic.main.sequence_layout.view.*
import java.lang.Float.isNaN
import java.util.*


/**
 * Vertical step tracker that contains {@link com.transferwise.sequencelayout.SequenceStep}s and animates to the first active step.
 *
 * <pre>
 * &lt;com.transferwise.sequencelayout.SequenceLayout
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      app:progressForegroundColor="?colorAccent"
 *      app:progressBackgroundColor="#ddd"&gt;
 *
 *      &lt;com.transferwise.sequencelayout.SequenceStep ... /&gt;
 *      &lt;com.transferwise.sequencelayout.SequenceStep app:active="true" ... /&gt;
 *      &lt;com.transferwise.sequencelayout.SequenceStep ... /&gt;
 *
 * &lt;/com.transferwise.sequencelayout.SequenceLayout&gt;
 * </pre>
 *
 * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceLayout_progressForegroundColor
 * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceLayout_progressBackgroundColor
 *
 * @see com.transferwise.sequencelayout.SequenceStep
 */
class SequenceLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        inflate(getContext(), R.layout.sequence_layout, this)

        val attributes = getContext().theme.obtainStyledAttributes(attrs, R.styleable.SequenceLayout, 0, R.style.SequenceLayout)
        applyAttributes(attributes)
        attributes.recycle()

        clipToPadding = false
        clipChildren = false

        start()
    }

    @ColorInt
    private var progressBackgroundColor: Int = 0

    @ColorInt
    private var progressForegroundColor: Int = 0


    fun start() {
        removeCallbacks(animateToActive)
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    fun setStyle(@StyleRes defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(defStyleAttr, R.styleable.SequenceLayout)
        applyAttributes(attributes)
        attributes.recycle()
    }

    /**
     * Sets the progress bar color
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceLayout_progressForegroundColor
     */
    fun setProgressForegroundColor(@ColorInt color: Int) {
        this.progressForegroundColor = color
        progressBarForeground.setBackgroundColor(color)
    }

    /**
     * Sets background resource for the dot of each contained step
     *
     * @attr ref com.transferwise.sequencelayout.R.styleable#SequenceLayout_dotBackground
     */
    fun setProgressBackgroundColor(@ColorInt progressBackgroundColor: Int) {
        this.progressBackgroundColor = progressBackgroundColor
        progressBarBackground.setBackgroundColor(progressBackgroundColor)
    }

    /**
     * Removes all contained [com.transferwise.sequencelayout.SequenceStep]s
     */
    fun removeAllSteps() {
        stepsWrapper.removeAllViews()
    }

    /**
     * Replaces all contained [com.transferwise.sequencelayout.SequenceStep]s with those provided and bound by the adapter
     */
    fun <T> setAdapter(adapter: SequenceAdapter<T>) where T : Any {
        stop()
        removeAllSteps()
        val count = adapter.getCount()
        for (i in 0 until count) {
            val item = adapter.getItem(i)
            val view = SequenceStep(context)
            adapter.bindView(view, item)
            addView(view)
        }
        start()
    }

    private fun applyAttributes(attributes: TypedArray) {
        setupProgressForegroundColor(attributes)
        setupProgressBackgroundColor(attributes)
    }

    private fun setupProgressForegroundColor(attributes: TypedArray) {
        setProgressForegroundColor(attributes.getColor(R.styleable.SequenceLayout_progressForegroundColor, 0))
    }

    private fun setupProgressBackgroundColor(attributes: TypedArray) {
        setProgressBackgroundColor(attributes.getColor(R.styleable.SequenceLayout_progressBackgroundColor, 0))
    }

    @Suppress("DEPRECATION")
    private fun setProgressBarHorizontalOffset() {
        val firstAnchor: View = stepsWrapper.getChildAt(0).findViewById(R.id.anchor)

        val config: Configuration = resources.configuration
        val locale: Locale
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.locales.get(0)
        } else {
            config.locale
        }
        if (locale.language == "ar") {
            val location = IntArray(2)
            firstAnchor.getLocationOnScreen(location)
            progressBarWrapper.translationX = (location[0] - 40.toPx() - (progressBarWrapper.measuredWidth / 2f))//(firstAnchor.measuredWidth + 4.toPx() - (progressBarWrapper.measuredWidth / 2f)) + 190.toPx() //TODO dynamic dot size
        } else {
            progressBarWrapper.translationX = (firstAnchor.measuredWidth + 4.toPx() - (progressBarWrapper.measuredWidth / 2f)) //TODO dynamic dot size
        }
    }

    private fun placeDots() {
        dotsWrapper.removeAllViews()
        var firstOffset = 0
        var lastOffset = 0

        stepsWrapper.children().forEachIndexed { i, view ->
            val sequenceStep = view as SequenceStep
            val sequenceStepDot = SequenceStepDot(context)
            sequenceStepDot.setDotBackground(progressForegroundColor, progressBackgroundColor)
            sequenceStepDot.setPulseColor(progressForegroundColor)
            sequenceStepDot.clipChildren = false
            sequenceStepDot.clipToPadding = false
            val layoutParams = LayoutParams(8.toPx(), 8.toPx()) //TODO dynamic dot size
            val totalDotOffset = getRelativeTop(sequenceStep, stepsWrapper) + sequenceStep.paddingTop + sequenceStep.getDotOffset() + 2.toPx() //TODO dynamic dot size
            layoutParams.topMargin = totalDotOffset
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            dotsWrapper.addView(sequenceStepDot, layoutParams)
            if (i == 0) {
                firstOffset = totalDotOffset
            }
            lastOffset = totalDotOffset
        }

        val backgroundLayoutParams = progressBarBackground.layoutParams as MarginLayoutParams
        backgroundLayoutParams.topMargin = firstOffset + 4.toPx()
        backgroundLayoutParams.height = lastOffset - firstOffset
        progressBarBackground.requestLayout()

        val foregroundLayoutParams = progressBarForeground.layoutParams as MarginLayoutParams
        foregroundLayoutParams.topMargin = firstOffset + 4.toPx()
        foregroundLayoutParams.height = lastOffset - firstOffset
        progressBarForeground.requestLayout()
    }

    private val animateToActive = {
        progressBarForeground.visibility = VISIBLE
        progressBarForeground.pivotY = 0f
        progressBarForeground.scaleY = 0f

        val activeStepIndex = stepsWrapper.children().indexOfFirst { it is SequenceStep && it.isActive() }

        if (activeStepIndex != -1) {
            val activeDot = dotsWrapper.getChildAt(activeStepIndex)
            val activeDotTopMargin = (activeDot.layoutParams as LayoutParams).topMargin
            val progressBarForegroundTopMargin = (progressBarForeground.layoutParams as LayoutParams).topMargin
            val scaleEnd = (activeDotTopMargin + (activeDot.measuredHeight / 2) - progressBarForegroundTopMargin) / progressBarBackground.measuredHeight.toFloat()

            progressBarForeground
                .animate()
                .setStartDelay(resources.getInteger(R.integer.sequence_step_duration).toLong())
                .scaleY(scaleEnd)
                .setInterpolator(LinearInterpolator())
                .setDuration(activeStepIndex * resources.getInteger(R.integer.sequence_step_duration).toLong())
                .setUpdateListener {
                    val animatedOffset = progressBarForeground.scaleY * progressBarBackground.measuredHeight
                    dotsWrapper
                        .children()
                        .forEachIndexed { i, view ->
                            if (i > activeStepIndex) {
                                return@forEachIndexed
                            }
                            val dot = view as SequenceStepDot
                            val dotTopMargin = (dot.layoutParams as LayoutParams).topMargin - progressBarForegroundTopMargin - (dot.measuredHeight / 2)
                            if (animatedOffset >= dotTopMargin) {
                                if (i < activeStepIndex && !dot.isEnabled) {
                                    dot.isEnabled = true
                                } else if (i == activeStepIndex && !dot.isActivated) {
                                    dot.isActivated = true
                                }
                            }

                            if (dotsWrapper.childCount == 1 && isNaN(animatedOffset)) {
                                dot.isEnabled = true
                                dot.isActivated = true

                            }
                        }
                }
                .start()
        }
    }

    private fun getRelativeTop(child: View, parent: ViewGroup): Int {
        val offsetViewBounds = Rect()
        child.getDrawingRect(offsetViewBounds)
        parent.offsetDescendantRectToMyCoords(child, offsetViewBounds)
        return offsetViewBounds.top
    }

    private fun stop() {
        removeCallbacks(animateToActive)
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is SequenceStep) {
            if (child.isActive()) {
                child.setPadding(
                    0,
                    if (stepsWrapper.childCount == 0) 0 else resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_top), //no paddingTop if first step is active
                    0,
                    resources.getDimensionPixelSize(R.dimen.sequence_active_step_padding_bottom)
                )
            }
            stepsWrapper.addView(child, params)
            return
        }
        super.addView(child, index, params)
    }

    override fun onGlobalLayout() {
        if (stepsWrapper.childCount > 0) {
            setProgressBarHorizontalOffset()
            placeDots()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            post(animateToActive)
        }
    }
}