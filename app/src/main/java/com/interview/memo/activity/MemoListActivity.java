package com.interview.memo.activity;

import androidx.fragment.app.Fragment;

import com.interview.memo.fragment.MemoListFragment;

public class MemoListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new MemoListFragment();
    }
}
