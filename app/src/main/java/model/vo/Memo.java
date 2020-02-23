package model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Memo implements Serializable {
    private UUID _id;
    private String title;
    private String des;
    private String fileDir;
    private ArrayList<String> image_order;
    private Date writtenDate;
    private Date editDate;



    public UUID getUUID()
    {
        return  _id;
    }
    public Memo() {
        _id = UUID.randomUUID();
        this.writtenDate = new Date();
    }

    public String getImgDIr() {
        return ImgDIr;
    }

    public void setImgDIr(String imgDIr) {
        ImgDIr = imgDIr;
    }

    private String ImgDIr;
    public String getId()
    {
        return _id.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public ArrayList<String> getImage_order() {
        return image_order;
    }

    public void setImage_order(ArrayList<String> image_order) {
        this.image_order = image_order;
    }

    public Date getWrittenDate() {
        return writtenDate;
    }

    public void setWrittenDate(Date writtenDate) {
        this.writtenDate = writtenDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Memo)) return false;
        Memo memo = (Memo) o;
        return Objects.equals(title, memo.title) &&
                Objects.equals(des, memo.des) &&
                Objects.equals(fileDir, memo.fileDir) &&
                Objects.equals(image_order, memo.image_order) &&
                Objects.equals(writtenDate, memo.writtenDate) &&
                Objects.equals(editDate, memo.editDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, des, fileDir, image_order, writtenDate, editDate);
    }

    @Override
    public String toString() {
        return "Memo{" +
                ", title='" + title + '\'' +
                ", des='" + des + '\'' +
                ", writtenDate=" + writtenDate +
                ", editDate=" + editDate +
                '}';
    }
}
