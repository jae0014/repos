package com.interview.memo.activity;

import androidx.fragment.app.Fragment;

import com.interview.memo.fragment.MemoEditorFragment;

public class MemoEditActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment()

        {
            return new MemoEditorFragment();
        }


}
