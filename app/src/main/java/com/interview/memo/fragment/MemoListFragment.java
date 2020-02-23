package com.interview.memo.fragment;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import model.vo.MemoNoteBook;
import com.interview.memo.R;
import com.interview.memo.activity.MemoAddActivity;
import com.interview.memo.activity.MemoDetailPagerActivity;
import com.interview.memo.activity.MemoEditActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import model.vo.Memo;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MemoListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MemoAdapter memoAdapter;
    private AppCompatButton mAddMemoButton;
    private boolean addNote = false;
    private boolean editNote = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // recycler layoutfile and its recyler

        View v = inflater.inflate(R.layout.activity_memo_listview, container, false);

        mRecyclerView =(RecyclerView)v.findViewById(R.id.memoList_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddMemoButton = (AppCompatButton)v.findViewById(R.id.add_memo);
        mAddMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MemoAddActivity.class);
                startActivity(i);


            }
        });
        // third-party 라이브러리입니다.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        ImageLoader.getInstance().init(config);
        updateUI();
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI();
    }


    private void updateUI()
    {
        MemoNoteBook notebook = MemoNoteBook.get(getActivity());
        ArrayList<Memo> list = notebook.getMemoList();

        memoAdapter = new MemoAdapter(list);
        mRecyclerView.setAdapter(memoAdapter);
    }
    private class MemoAdapter extends RecyclerView.Adapter<MemoHolder> {

        private ArrayList<Memo> mMemo;

        public MemoAdapter( ArrayList<Memo> memos) {
            mMemo = memos;
        }

        @Override
        public MemoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new MemoHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MemoHolder holder, int position) {
            Memo memo = mMemo.get(position);
            holder.bind(memo ,position);
        }

        @Override
        public int getItemCount() {
            return mMemo.size();
        }
    }


    private class MemoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Memo mMemo;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mDetailView;
        private ImageView mThumbnail;
        private Button mDeleteButton;
        private Button mEditButton;

        public MemoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_memo, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mDetailView= (TextView) itemView.findViewById(R.id.detail);
            mDateTextView = (TextView) itemView.findViewById(R.id.date);
            mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mDeleteButton = (Button)itemView.findViewById(R.id.delete);
            mEditButton = (Button)itemView.findViewById(R.id.edit);
        }


        public void  urlImage(String url , String currentPhotoPath)
        {
            ImageLoader imgload = ImageLoader.getInstance();

            try {
                new URL(url).toURI();
                currentPhotoPath = url;
                imgload.displayImage(url,mThumbnail);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            catch (Exception e) {
                Log.e("URL_TRIED_FAIL_IN_LIST", url);

            }
        }
        public void bind(Memo memo ,int pos)
        {
            mMemo = memo;
            final int curr = pos;
            mTitleTextView.setText(mMemo.getTitle());
            mDetailView.setText(mMemo.getDes());
            mDetailView.setMaxLines(1);

            if(mMemo.getImage_order() != null)
            {   // 첫 이미지를 썸네일로 지정 이떄 첫 이미지는 사용자가 이미지를 넣는 순서이다

                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if(mMemo.getImage_order().get(0).matches("^(https?|ftp)://.*$")) {
                    String currentPhotoPath = mMemo.getImage_order().get(0);
                    urlImage(mMemo.getImage_order().get(0),currentPhotoPath);
                }
                else if(storageDir.isDirectory())
                {

                    Log.e("ListFragment",mMemo.getImage_order().get(0));
                    Bitmap bitmap = BitmapFactory.decodeFile(mMemo.getImage_order().get(0));
                    mThumbnail.setImageBitmap(bitmap);

                }
            }
            if(mMemo.getEditDate() == null) {
                mDateTextView.setText(mMemo.getWrittenDate().toString());
            }
            else
            {
                mDateTextView.setText(mMemo.getEditDate().toString());
            }
           mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
                                        Log.e("DIR",dir.getAbsolutePath());
                                        updateUI();
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
                Intent intent = new Intent(getActivity(), MemoEditActivity.class);



                intent.putExtra("current",mMemo.getId());
                startActivity(intent);

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
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mMemo.getTitle() + " 선택하셨습니다.", Toast.LENGTH_SHORT)
                    .show();
            Intent intent = MemoDetailPagerActivity.newIntent(getActivity(),mMemo.getUUID());
            startActivity(intent);
        }
    }
}
