package com.javgon.wakeme.CustomAnimation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by javgon on 7/3/2017.
 */

public class JAnimations {

    public static void doBounceInAnimation(View targetView, int delay) {

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(targetView, "scaleX", 0, 1.2f, 1);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(targetView, "scaleY", 0, 1.2f, 1);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setInterpolator(new EasingInterpolator(Ease.BOUNCE_OUT));
        animatorSet.setDuration(600+delay);
        animatorSet.start();

        if (animatorSet.isStarted()){
            targetView.setVisibility(View.VISIBLE);
        }

    }

    public static void doBounceOutInAnimation(View targetView, int delay) {

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(targetView, "scaleX", 1, 1.2f, 1);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(targetView, "scaleY", 1, 1.2f, 1);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setInterpolator(new EasingInterpolator(Ease.BOUNCE_OUT));
        animatorSet.setDuration(500+delay);
        animatorSet.start();


    }
    public static void doBounceOutAnimation(final View targetView, int delay){

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(targetView, "scaleX", 1, 1.2f, 0);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(targetView, "scaleY", 1, 1.2f, 0);

        objectAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                targetView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                targetView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setInterpolator(new EasingInterpolator(Ease.BOUNCE_OUT));
        animatorSet.setDuration(600+delay);
        animatorSet.start();

    }
}
