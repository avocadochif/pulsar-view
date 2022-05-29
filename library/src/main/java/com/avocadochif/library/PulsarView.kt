package com.avocadochif.library

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import kotlin.math.min

class PulsarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PulsarView)

    private val count: Int by lazy(LazyThreadSafetyMode.NONE) {
        //by default 0 items will be shown
        attributes.getInteger(R.styleable.PulsarView_pulsar_count, 0)
    }

    private val duration: Long by lazy(LazyThreadSafetyMode.NONE) {
        //by default duration of animation is 1000ms
        attributes.getInteger(R.styleable.PulsarView_pulsar_duration, 1000).toLong()
    }

    private val repeat: Int by lazy(LazyThreadSafetyMode.NONE) {
        //by default repeat count is -1, means that animation will be infinity
        attributes.getInteger(R.styleable.PulsarView_pulsar_repeat, -1)
    }

    private val color: Int by lazy(LazyThreadSafetyMode.NONE) {
        //by default color of circles is white
        attributes.getColor(R.styleable.PulsarView_pulsar_color, Color.rgb(255, 255, 255))
    }

    private val isAutoStart: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        //by default animation should be started manual
        attributes.getBoolean(R.styleable.PulsarView_pulsar_auto_start, false)
    }

    private val interpolator: Interpolator by lazy(LazyThreadSafetyMode.NONE) {
        //by default using LinearInterpolator
        InterpolatorType.values()[attributes.getInteger(R.styleable.PulsarView_pulsar_interpolator, 0)].interpolator
    }

    private val views: MutableList<View> = mutableListOf()
    private val setOfAnimators = AnimatorSet()
    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var isStarted: Boolean = false

    private val listener = AnimationListener(
        onAnimationStart = { isStarted = true },
        onAnimationEnd = { isStarted = false },
        onAnimationCancel = { isStarted = false }
    )

    init {
        buildPulseViews()
        if (isAutoStart) startAnimations()
    }

    fun start() {
        startAnimations()
    }

    fun stop() {
        stopAnimations()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

        centerX = width * 0.5f
        centerY = height * 0.5f
        radius = min(width, height) * 0.5f

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun buildPulseViews() {
        val animators: MutableList<Animator> = mutableListOf()
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        for (index in 0 until count) {
            val delay = index * duration / count
            val view = PulseView(context).apply {
                scaleX = 0f
                scaleY = 0f
                alpha = 1f
            }.also {
                addView(it, index, layoutParams)
            }.also {
                views.add(it)
            }

            ObjectAnimator.ofFloat(view, "ScaleX", 0f, 1f).apply {
                repeatCount = repeat
                repeatMode = ObjectAnimator.RESTART
                startDelay = delay
            }.also {
                animators.add(it)
            }

            ObjectAnimator.ofFloat(view, "ScaleY", 0f, 1f).apply {
                repeatCount = repeat
                repeatMode = ObjectAnimator.RESTART
                startDelay = delay
            }.also {
                animators.add(it)
            }

            ObjectAnimator.ofFloat(view, "Alpha", 1f, 0f).apply {
                repeatCount = repeat
                repeatMode = ObjectAnimator.RESTART
                startDelay = delay
            }.also {
                animators.add(it)
            }
        }

        setOfAnimators.apply {
            playTogether(animators)
            interpolator = this@PulsarView.interpolator
            duration = this@PulsarView.duration
            addListener(this@PulsarView.listener)
        }
    }

    private fun startAnimations() {
        when (isStarted) {
            true -> return
            false -> setOfAnimators.start()
        }
    }

    private fun stopAnimations() {
        when (isStarted) {
            true -> setOfAnimators.end()
            false -> return
        }
    }

    private inner class PulseView(context: Context) : View(context) {

        private val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = this@PulsarView.color
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }

    }

}