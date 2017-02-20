package com.bignerdranch.android.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Sameer on 2/2/2017.
 */

public class SunsetFragment extends Fragment {

    private View mSceneview;
    private View mSunView;
    private View mSkyView;
    private View mSeaView;
    private View mSunReflectionView;


    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private int mHotSunColor;
    private int mNormalSunColor;
    private int mSunReflectionColorNormal;
    private int mSunReflectionColorHot;

    ObjectAnimator pulseAnimator;
    ObjectAnimator pulseAnimatorReflection;

    private static final String TAG = "SunsetFragment";

    //by default set to sunset
    private SUNRISE_SET mSunrise_Set = SUNRISE_SET.SUNSET;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneview = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);
        mSeaView = view.findViewById(R.id.sea);
        mSunReflectionView = view.findViewById(R.id.sunReflection);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initSunReflection();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Resources resources = getResources();
        mBlueSkyColor = ContextCompat.getColor(getContext(), R.color.blue_sky);
        mSunsetSkyColor = ContextCompat.getColor(getContext(), R.color.sunset_sky);
        mNightSkyColor = ContextCompat.getColor(getContext(), R.color.night_sky);
        mHotSunColor = ContextCompat.getColor(getContext(), R.color.hot_sun);
        mNormalSunColor = ContextCompat.getColor(getContext(), R.color.bright_sun);
        mSunReflectionColorNormal = ContextCompat.getColor(getContext(), R.color
                .bright_sun_reflection);
        mSunReflectionColorHot = ContextCompat.getColor(getContext(), R.color.hot_sun_reflection);

        initAnimation();

        mSceneview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pulseAnimator.cancel();
                //pulseAnimatorReflection.cancel();
                Log.d(TAG, "mSunrise_set: " + mSunrise_Set);
                startAnimationForwardOrReverse();
            }
        });

        return view;
    }

    private void startAnimationForwardOrReverse() {
        final float sunYStart = mSunView.getTop();
        final float sunYEnd = (float) (mSeaView.getTop() + 0.2 * mSunView.getHeight());
        Log.d("animation start", "mSunView.getTop(): " + sunYStart + ", mSkyView.getBottom(): " +
                mSkyView.getBottom() + ", mSkyView" +
                ".getHeight(): " +
                sunYEnd);

        float sunYStartReverse = mSunReflectionView.getBottom();
        float sunYEndReverse = (float) -(mSunView.getHeight() + 0.2 * mSunReflectionView.getHeight());
        ObjectAnimator heightAnimatorReflection = ObjectAnimator.ofFloat(mSunReflectionView, "y",
                sunYStartReverse, sunYEndReverse)
                .setDuration(3000);

        //Animator instantiations
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSunrise_Set = mSunrise_Set.SUNSET;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mSunView.getY() == sunYEnd) {
                    mSunrise_Set = (mSunrise_Set == SUNRISE_SET.SUNSET ? SUNRISE_SET.SUNRISE : SUNRISE_SET
                            .SUNSET);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mBlueSkyColor, mSunsetSkyColor).setDuration(3000);
        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mSunsetSkyColor, mNightSkyColor).setDuration(1500);

        //setting interpolators and evaluators
        //heightAnimator.setInterpolator(new AccelerateInterpolator());
        //heightAnimatorReflection.setInterpolator(new DecelerateInterpolator());
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        Drawable sun = ContextCompat.getDrawable(getContext(), R.drawable
                .sun);

        //hotSunAnimator.start();

        //creating the animatorSet
        AnimatorSet sunsetAnimatorSet = new AnimatorSet();
        sunsetAnimatorSet.play(heightAnimator).with(heightAnimatorReflection).with
                (sunsetSkyAnimator).before(nightSkyAnimator);

        //Corresponding reverse animators of the above animators
        ObjectAnimator reverseHeightAnimator = ObjectAnimator.ofFloat(mSunView, "y", sunYEnd,
                sunYStart).setDuration(3000);
        reverseHeightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSunrise_Set = mSunrise_Set.SUNRISE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mSunView.getY() == sunYStart) {
                    mSunrise_Set = (mSunrise_Set == SUNRISE_SET.SUNSET ? SUNRISE_SET.SUNRISE : SUNRISE_SET
                            .SUNSET);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator reverseSunsetAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mSunsetSkyColor, mBlueSkyColor).setDuration(3000);
        ObjectAnimator reverseNightSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mNightSkyColor, mSunsetSkyColor).setDuration(1500);

        ObjectAnimator heightAnimatorReflectionReverse = ObjectAnimator.ofFloat(mSunReflectionView,
                "y",
                sunYEndReverse, sunYStartReverse - (sunYStartReverse - mSunReflectionView
                        .getHeight()))
                .setDuration(3000);
        //setting interpolators and evaluators
        //reverseHeightAnimator.setInterpolator(new DecelerateInterpolator());
        //heightAnimatorReflectionReverse.setInterpolator(new AccelerateInterpolator());
        reverseSunsetAnimator.setEvaluator(new ArgbEvaluator());
        reverseNightSkyAnimator.setEvaluator(new ArgbEvaluator());

        //Corresponding reverse animators of the above animators
        AnimatorSet reverseSunsetAnimatorSet = new AnimatorSet();
        reverseSunsetAnimatorSet.play(reverseHeightAnimator).with(reverseSunsetAnimator).with
                (heightAnimatorReflectionReverse).after
                (reverseNightSkyAnimator);


        startAnimationForwardOrReverse(sunsetAnimatorSet, mSunrise_Set, reverseSunsetAnimatorSet);

    }

    public void startAnimationForwardOrReverse(AnimatorSet forwardAnimatorSet, SUNRISE_SET
            sunrise_set, AnimatorSet reverseAnimatorSet) {
        if (sunrise_set == SUNRISE_SET.SUNSET) {
            forwardAnimatorSet.start();
        } else if (sunrise_set == SUNRISE_SET.SUNRISE) {
            reverseAnimatorSet.start();
        }
    }

    //initial animation
    private void initAnimation() {
        pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(mSunView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat
                        ("scaleY", 1.2f));
        pulseAnimator.setDuration(600);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        //pulseAnimator.start();

        GradientDrawable drawable = (GradientDrawable) mSunView.getBackground();

        if (drawable == null) {
            Log.d(TAG, "Drawable resource not found inside mSunview. Will not be animated.");
        }

        ObjectAnimator hotSunAnimator = ObjectAnimator.ofInt(drawable,
                "color", mNormalSunColor, mHotSunColor).setDuration(600);
        hotSunAnimator.setEvaluator(new ArgbEvaluator());
        hotSunAnimator.setEvaluator(new ArgbEvaluator());
        hotSunAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        hotSunAnimator.setRepeatCount(ObjectAnimator.INFINITE);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(pulseAnimator).with(hotSunAnimator);
        animatorSet.start();

    }

    private void initSunReflection() {
        float yDist = mSeaView.getY() - mSunView.getY();
        float y = yDist - mSunView.getHeight();

        float x = mSunView.getX();

        mSunReflectionView.setX(x);
        mSunReflectionView.setY(y);

        initSunReflectionAnimation();
    }

    private void initSunReflectionAnimation() {
        pulseAnimatorReflection = ObjectAnimator.ofPropertyValuesHolder
                (mSunReflectionView,
                        PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat
                                ("scaleY", 1.2f));
        pulseAnimatorReflection.setDuration(600);
        pulseAnimatorReflection.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimatorReflection.setRepeatMode(ObjectAnimator.REVERSE);

        GradientDrawable drawable = (GradientDrawable) mSunReflectionView.getBackground();

        if (drawable == null) {
            Log.d(TAG, "Drawable resource not found inside mSunview. Will not be animated.");
        }

        ObjectAnimator hotSunAnimator = ObjectAnimator.ofInt(drawable,
                "color", mSunReflectionColorNormal, mSunReflectionColorHot).setDuration(600);
        hotSunAnimator.setEvaluator(new ArgbEvaluator());
        hotSunAnimator.setEvaluator(new ArgbEvaluator());
        hotSunAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        hotSunAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(pulseAnimatorReflection).with(hotSunAnimator);
        animatorSet.start();
    }

}
