package com.avocadochif.library

import android.animation.Animator

class AnimationListener(
    private val onAnimationStart: () -> Unit,
    private val onAnimationEnd: () -> Unit,
    private val onAnimationCancel: () -> Unit
) : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator?) {
        onAnimationStart()
    }

    override fun onAnimationEnd(animation: Animator?) {
        onAnimationEnd()
    }

    override fun onAnimationCancel(animation: Animator?) {
        onAnimationCancel()
    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

}