package com.interview.memo.activity;

import androidx.fragment.app.Fragment;

import com.interview.memo.fragment.MemoAddFragment;

public class MemoAddActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment()

        {
            return new MemoAddFragment();
        }


}
