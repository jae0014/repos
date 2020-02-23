package file;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.vo.Memo;

public class RWFile {


    public void writeObject( Memo memo)
    {

        FileOutputStream fos;
        ObjectOutputStream objectout = null;
        File filePath = new File(memo.getFileDir());

        File actualFile
                = new File(memo.getFileDir(),memo.getId()+".dat");

        try {
            if(filePath.mkdirs())
            {

                actualFile.createNewFile();
                System.out.println("File has created");
            }
            fos = new FileOutputStream(actualFile);
            objectout = new ObjectOutputStream(fos);
            objectout.writeObject(memo);
            objectout.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public ArrayList<Memo> readObject(String str)
    {

        ArrayList<Memo> list = new ArrayList<Memo>();
        FileInputStream fos;
        ObjectInputStream objin = null;
        File temp = new File(str);
        File[] file = temp.listFiles();
        if(temp.exists()) {
            try {
                for (int j = 0; j < file.length; j++) {
                    fos = new FileInputStream(str + file[j].getName());
                    objin = new ObjectInputStream(fos);
                    Memo me = (Memo) objin.readObject();

                    list.add(me);
                }


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }
    public ArrayList<Memo> readObject(Memo memo)
    {
        ArrayList<Memo> list = null;
        FileInputStream fos;
        ObjectInputStream objin = null;
        File temp = new File(memo.getFileDir());
        File[] file = temp.listFiles();


        try {
            list = new ArrayList<Memo>();
            for (int j = 0; j < file.length; j++) {
                fos = new FileInputStream(memo.getFileDir()+"\\"+file[j].getName());
                objin = new ObjectInputStream(fos);
                Memo me = (Memo)objin.readObject();
                list.add(me);
            }


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

}
