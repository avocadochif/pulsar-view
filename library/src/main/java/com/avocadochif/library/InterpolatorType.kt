package com.avocadochif.library

import android.view.animation.*

enum class InterpolatorType(val interpolator: Interpolator) {

    LINEAR(LinearInterpolator()),

    ACCELERATE(AccelerateInterpolator()),

    DECELERATE(DecelerateInterpolator()),

    ACCELERATE_DECELERATE(AccelerateDecelerateInterpolator())

}