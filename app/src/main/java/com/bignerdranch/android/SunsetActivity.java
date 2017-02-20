package com.bignerdranch.android.sunset;

import android.support.v4.app.Fragment;

/**
 * Created by Sameer on 2/2/2017.
 */

public class SunsetActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
