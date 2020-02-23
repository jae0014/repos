package com.interview.memo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.interview.memo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import model.vo.Memo;
import model.vo.MemoNoteBook;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MemoDetailFragment extends Fragment {
    private static final String MEMO_UUID = "memo_id";
    private Memo mMemo;



    public static MemoDetailFragment newInstance(UUID amemo) {
        Bundle args = new Bundle();
        args.putSerializable(MEMO_UUID, amemo);
        MemoDetailFragment fragment = new MemoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID amemo = (UUID) getArguments().getSerializable(MEMO_UUID);
        mMemo = MemoNoteBook.get(getActivity()).getSingleMemo(amemo.toString());
    }


    private Button mNextButton;
    private Button mPrevButton;
    private Button mEditButton;
    private Button mDeleteButton;
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mDetailTextView;


    // for image handling
    private HashMap<String,Bitmap> imageList;
    private ArrayList<String> imageOrder;
    private String currentPhotoPath;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getImageFromDir();
        View v =inflater.inflate(R.layout.fragment_detail,container,false);

        mNextButton =  (Button)v.findViewById(R.id.next);
        mPrevButton =  (Button)v.findViewById(R.id.prev);

        mImageView = (ImageView)v.findViewById(R.id.imageView);

        mTitleTextView= (TextView)v.findViewById(R.id.title);
        mDetailTextView=(TextView)v.findViewById(R.id.detial);



        // 세팅
        mTitleTextView.setText(mMemo.getTitle());
        mDetailTextView.setText(mMemo.getDes());

        if(imageOrder != null)
        {
            if(imageOrder.get(0).matches("^(https?|ftp)://.*$"))
            {
                urlImage(currentPhotoPath);
            }
            else
            {
                mImageView.setImageBitmap(imageList.get(currentPhotoPath));
            }
        }


        //이전
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = imageOrder.indexOf(currentPhotoPath) -1;
                if(pos < 0 && imageOrder.size()-1 > 0)
                {
                    currentPhotoPath =imageOrder.get(imageOrder.size()-1);
                    if(currentPhotoPath.matches("^(https?|ftp)://.*$"))
                    {
                        urlImage(currentPhotoPath);
                    }
                    mImageView.setImageBitmap(imageList.get(currentPhotoPath));
                }
                else if(imageOrder.size() != 1 && imageOrder.size()-1 > 0)
                {
                    currentPhotoPath =imageOrder.get(pos);
                    if(currentPhotoPath.matches("^(https?|ftp)://.*$"))
                    {
                        urlImage(currentPhotoPath);
                    }
                    mImageView.setImageBitmap(imageList.get(currentPhotoPath));
                }
            }
        });
        // 다음
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = imageOrder.indexOf(currentPhotoPath) +1;
                if(pos > imageOrder.size()-1)// && imageOrder.size()-1 > 0)
                {
                    currentPhotoPath =imageOrder.get(0);
                    if(currentPhotoPath.matches("^(https?|ftp)://.*$"))
                    {
                        urlImage(currentPhotoPath);
                    }
                    else
                        mImageView.setImageBitmap(imageList.get(currentPhotoPath));
                }
                else if(imageOrder.size() != 1 && imageOrder.size()-1 > 0)
                {
                    currentPhotoPath =imageOrder.get(pos);
                    if(currentPhotoPath.matches("^(https?|ftp)://.*$"))
                    {
                        urlImage(currentPhotoPath);
                    }
                    mImageView.setImageBitmap(imageList.get(currentPhotoPath));
                }
            }
        });
        return v;
    }




    public void  urlImage(String url)
    {
        ImageLoader imgload = ImageLoader.getInstance();

        try {
            new URL(url).toURI();
            currentPhotoPath = url;
            Log.e("URL_TRIED", url);
            imgload.displayImage(url,mImageView);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        }
        catch (Exception e) {
            Log.e("URL_TRIED_FAIL", url);
            Toast toast = Toast.makeText( getActivity().getApplicationContext(), "유효하지 URL 입니다",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
    public void getImageFromDir()
    { imageList = new HashMap<String,Bitmap>();
        imageOrder =  mMemo.getImage_order();
        if(imageOrder != null && mMemo.getImage_order() != null) {
            for (String str : imageOrder) {
                if (!str.matches("^(https?|ftp)://.*$")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(str);
                    imageList.put(str, bitmap);
                }

            }
            if (!mMemo.getImage_order().get(0).matches("^(https?|ftp)://.*$"))
                currentPhotoPath = mMemo.getImage_order().get(0);
        }
    }
}
