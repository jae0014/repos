package com.interview.memo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.interview.memo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import file.RWFile;
import model.vo.Memo;
import model.vo.MemoNoteBook;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class MemoEditorFragment extends Fragment {


    static final int REQUEST_GALLERY_PHOTO = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private boolean saveCondition = false;

    private String currentPhotoPath;


    private String currentImageVIew;
    private RWFile rwFile;
    private Memo mMemo;
    // Text Edit Field
    private EditText mTitleField;
    private EditText mDetailField;
    private EditText mURLField;
    //spinner
    private Spinner spinner;
    // Button
    private Button mCameraButton;
    private Button mGalleryButton;
    private Button mURLButton;
    private Button mPrevButton;
    private Button mNextButton;
    private Button mSaveButton;
    private Button mDeletePicButton;
    private Button mGetimageButton;
    private Button mDeleteMemo;
    // Image Viewer
    private ImageView mImageView;

    private HashMap<String,Bitmap> imageList;
    private ArrayList<String> imageOrder;
    private ArrayList<String> imageAdded;
    private String currPos;


    private LinearLayout linearLayout;


    public static MemoEditorFragment newInstance() {
        return new MemoEditorFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //https://github.com/nostra13/Android-Universal-Image-Loader
        // URL  이미지를  다운 및 VIEW 뿌려줄게 하는 라이브러리.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext())
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        ImageLoader.getInstance().init(config);
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        currPos = bundle.getString("current");
        MemoNoteBook note = MemoNoteBook.get(getActivity());

        mMemo = note.getSingleMemo(currPos);
        rwFile = new RWFile();


        imageList = new HashMap<String,Bitmap>();
        imageOrder = mMemo.getImage_order();
        if(imageOrder == null)
            imageOrder = new ArrayList<String>();
        imageAdded= new ArrayList<String>();
        rwFile = new RWFile();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        View v =inflater.inflate(R.layout.fragment_memo_edit,container,false);
        getImageFromDir();
        // Text Edit Field
        mTitleField = (EditText)v.findViewById(R.id.memo_title);
        mDetailField= (EditText)v.findViewById(R.id.detail);
        mURLField = (EditText)v.findViewById(R.id.url_text);
        // Button
        mPrevButton= (Button)v.findViewById(R.id.img_prev_btn);
        mNextButton= (Button)v.findViewById(R.id.img_next_btn);
        mDeletePicButton = (Button)v.findViewById(R.id.erase_pic);
        mGalleryButton= (Button)v.findViewById(R.id.gallery_button);
        mURLButton= (Button)v.findViewById(R.id.url_img_button);
        mCameraButton= (Button)v.findViewById(R.id.camera_button);
        mDeleteMemo = (Button)v.findViewById(R.id.delete_memo);
        // Image Viewer
        mImageView= (ImageView) v.findViewById(R.id.imagePreView);
        mGetimageButton = (Button)v.findViewById(R.id.img_req);
        mSaveButton = (Button)v.findViewById(R.id.memo_save);
        linearLayout = (LinearLayout)v.findViewById(R.id.hidden_lay) ;

        //URL add button
        mURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("URL-BTN", "button pressed");

                imageOrder.add(mURLField.getText().toString());
                urlImage(mURLField.getText().toString());


            }
        });
        // 갤러리 버튼
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchGalleryInternt();
                // ArrayList<Memo> list = readObject();

            }
        });
        // 카메라
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // 이전
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
        // 저장
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCondition = true;
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getParentFile();

                String str = storageDir.getAbsolutePath() + "/memo_obj/";

                Log.e("자바 저장 dat 저장 로케이션",str );

                String title = mTitleField.getText().toString();
                String detail = mDetailField.getText().toString();
                mMemo.setTitle(title);
                mMemo.setDes(detail);
                mMemo.setFileDir(str);
                if(imageOrder.size() > 0) {
                    mMemo.setImage_order(imageOrder);
                    Log.e("이미지 오러 저장","저장합니다");
                }
                else
                {
                    mMemo.setImage_order(null);
                    mMemo.setImgDIr(null);
                }
                Log.e("mMemo",mMemo.toString());
                rwFile.writeObject(mMemo);
                getActivity().finish();
            }
        });



        mDeletePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = createDialog("사진을 삭제하시겠습니까?" , 2);
                d.show();
            }
        });

        mGetimageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                if(currentPhotoPath!= null){
                    Log.e("CurrentPath in Edit", currentPhotoPath);
                    if (!currentPhotoPath.matches("^(https?|ftp)://.*$") ) {
                        mImageView.setImageBitmap(imageList.get(currentPhotoPath));
                    }else if(currentPhotoPath.matches("^(https?|ftp)://.*$") )
                    {
                        urlImage(currentPhotoPath);
                    }}
                mGetimageButton.setVisibility(View.GONE);
            }
        });
        mDeleteMemo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Dialog d = createDialog("메로를 삭제하시겠습니까?" , 1);
                d.show();
            }
        });

        mTitleField.setText(mMemo.getTitle());
        mDetailField.setText(mMemo.getDes());

        return v;
    }





    public void getImageFromDir()
    {

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

    public Dialog createDialog(String msg ,int type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(type == 1) {
            builder.setMessage(msg)
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteAllFiles();

                                    getActivity().finish();
                                }
                            }
                    ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        else
        {

            // 이미지 삭제
            builder.setMessage(msg)
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    imageOrder.remove(currentPhotoPath);
                                    if (currentPhotoPath != null || imageOrder.size()>0) {



                                        if(imageOrder.size() == 0)
                                        {
                                            mImageView.setImageBitmap(null);
                                            currentPhotoPath = null;
                                        }
                                        else if (imageOrder.get(imageOrder.size() - 1).matches("^(https?|ftp)://.*$")) {

                                            urlImage(imageOrder.get(imageOrder.size() - 1));
                                            currentPhotoPath = imageOrder.get(imageOrder.size() - 1);

                                        } else {

                                            imageList.remove(currentPhotoPath);
                                            File file = new File(currentPhotoPath);
                                            if(imageOrder.size() > 0)
                                            {
                                                mImageView.setImageBitmap(imageList.get(imageOrder.get(imageOrder.size() - 1)));
                                                currentPhotoPath = imageOrder.get(imageOrder.size() - 1);
                                                if(file !=null)
                                                {
                                                    file.delete();
                                                    currentPhotoPath = imageOrder.get(imageOrder.size() - 1);
                                                }
                                            }

                                        }

                                    }




                                }


                            }
                    ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        return  builder.create();
    }
    // Picture intent op
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                        "com.interview.memo.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }


    public void  urlImage(String url)
    {
        ImageLoader imgload = ImageLoader.getInstance();

        try {
            new URL(url).toURI();
            currentPhotoPath = url;
            mURLField.setText("");
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("DEBUGED_SUEESSS", "RC: " + requestCode + " " + "RC: " + resultCode);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data !=null) {
            galleryAddPic();
        }
        else if ( requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK)
        {

            try {
                Uri selectedImage = data.getData();

                File f = createImageFile();
                // 이미지를 불러온다
                InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                // 이미지 저장
                OutputStream out = new FileOutputStream(f);


                //o
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);


                imageList.put(currentPhotoPath,bitmap);
                imageOrder.add(currentPhotoPath);
                imageAdded.add(currentPhotoPath);
                mImageView.setImageBitmap(imageList.get(currentPhotoPath));

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    // on back pressed handle this
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(!saveCondition)
            deleteAddedFiles();
    }

    public void deleteAddedFiles()
    {
        Log.e("addedDelete","begin");

            for (String added: imageAdded ) {


            if(imageOrder.contains(added))
            {
                Log.e("addedDelete",added);
                File dir = new File(added);
                if(dir.exists())
                {    Log.e("addedDelete existsdir",added);
                    dir.delete();
                }
            }

        }
    }
    public void  deleteAllFiles()
    {
        if(mMemo.getImgDIr() != null) {
            File dir = new File(mMemo.getImgDIr());
            if(dir.exists())
            {
                for(File file : dir.listFiles())
                {
                    if(file !=null)
                    {
                        file.delete();
                    }
                }
                dir.delete();
            }
        }
        File memoObj = new File(mMemo.getFileDir()+"//" +mMemo.getId()+".dat");
        if(memoObj!= null)
            memoObj.delete();

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Bitmap imageBitmap = null;
            imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
            imageList.put(currentPhotoPath,imageBitmap);
            imageOrder.add(currentPhotoPath);
            imageAdded.add(currentPhotoPath);

            mImageView.setImageBitmap(imageList.get(currentPhotoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    private String tempimgDir;

    private File createImageFile() throws IOException {
        // Create an image file name
        //Log.d("FileCreate","Debug Tried");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        rwFile = new RWFile();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File newStorageDir = new File(storageDir.getAbsolutePath() + "//"+ mMemo.getId());
        tempimgDir = storageDir.getAbsolutePath() + "/";

        //Log.i("new StorageDir",newStorageDir.getAbsolutePath());
        newStorageDir.mkdirs();
        String temp = newStorageDir.getAbsolutePath();
        // 다이렉토리 폴더 Path 저장
        mMemo.setImgDIr(temp);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                newStorageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        currentPhotoPath = image.getAbsolutePath();
        Log.d("현재 주소는요",currentPhotoPath);
        return image;
    }

    private void dispatchGalleryInternt()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY_PHOTO );
    }
}
