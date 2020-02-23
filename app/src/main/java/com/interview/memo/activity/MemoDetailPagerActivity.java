package com.interview.memo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.interview.memo.R;
import com.interview.memo.fragment.MemoDetailFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import model.vo.Memo;
import model.vo.MemoNoteBook;

public class MemoDetailPagerActivity extends AppCompatActivity
{
    private static final String MEMO_VIEW_ID = "com.interview.memo.model.vo.Memo";
    private ViewPager mViewPager;
    private ArrayList<Memo> memo_list;
    private Memo mMemo;


    public static Intent newIntent(Context pageContent, UUID userID)
    {
        Intent intent = new Intent(pageContent, MemoDetailPagerActivity.class);
        intent.putExtra(MEMO_VIEW_ID, userID);
        return intent;
    }

    private Button mEditButton;
    private Button mDeleteButton;
    @Override
    protected void onCreate(Bundle saveInstance)
    {
        super.onCreate(saveInstance);
        setContentView(R.layout.view_layout_pager);

        mViewPager = (ViewPager) findViewById(R.id.view_memo_pager);
        memo_list =  MemoNoteBook.get(this).getMemoList();
        mDeleteButton = (Button) findViewById(R.id.delete);
        mEditButton  = (Button) findViewById(R.id.edit);
        UUID memoID = (UUID)getIntent().getSerializableExtra(MEMO_VIEW_ID);
        final String curr = memoID.toString();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Memo mMemo = memo_list.get(position);
                return MemoDetailFragment.newInstance(mMemo.getUUID());
            }
            @Override
            public int getCount()
            {
                return memo_list.size();
            }
        });

        for (int i = 0; i < memo_list.size(); i++) {
            if (memo_list.get(i).getUUID().equals(memoID)) {
                mViewPager.setCurrentItem(i);
                mMemo = memo_list.get(i);
                break;
            }
        }
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MemoDetailPagerActivity.this);
                builder.setMessage("메모를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e("메머","메모를 삭제 시도합니다");

                                        if(mMemo.getImage_order() !=null && mMemo.getImgDIr() !=null)
                                        {
                                            deleteAllFiles(mMemo);
                                        }
                                        File dir = new File(mMemo.getFileDir()+"/"+mMemo.getId()+".dat");
                                        dir.delete();
                                        finish();
                                    }
                                }
                        ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MemoDetailPagerActivity.this, MemoEditActivity.class);
                intent.putExtra("current", curr);
                startActivity(intent);
                finish();
            }
        });

    }


    public void  deleteAllFiles(Memo memo)
    {
        if(memo.getImgDIr() != null) {
            File dir = new File(memo.getImgDIr());

            if(dir.exists())
            {   Log.e("DIR",dir.getAbsolutePath());
                for(File file : dir.listFiles())
                {
                    Log.e("file",file.getAbsolutePath());
                    if(file !=null)
                    {
                        file.delete();

                    }
                }
                dir.delete();
            }
        }
    }


}
