package model.vo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import file.RWFile;

public class MemoNoteBook {
    private static MemoNoteBook sMemo;
    private ArrayList<Memo> mMemoList;
    private String str;
    public static MemoNoteBook get(Context context)
    {
        if(sMemo == null)
        {
            sMemo = new MemoNoteBook(context);
        }
        return sMemo;
    }

    private MemoNoteBook(Context c)
    {
         mMemoList = new ArrayList<Memo>();
        File storageDir = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getParentFile();
         str = storageDir.getAbsolutePath() + "/memo_obj/";


    }


    public ArrayList<Memo> getMemoList()
    {

        //Log.e("DIR", str);

        mMemoList = new RWFile().readObject(str);
        return mMemoList;
    }

    public Memo getSingleMemo(String id)
    {
        for (Memo m: mMemoList)  {
            if(m.getId().equals(id))
                return m;
        }
        return null;
    }

}
