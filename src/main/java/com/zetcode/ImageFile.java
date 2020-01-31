package com.zetcode;

import java.io.File;
import java.nio.file.Path;

public class ImageFile {

    private String fileName;
    private Path path;
    private String blog;
    private String tags;
    private String likes;
    private String URI;
    private String fileNameId;
    private File file;
    public ImageFile() {
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    public ImageFile(String fileName, Path path) {
        this.fileName = fileName;
        this.path = path;
        this.file = new File(String.valueOf(path));
    }

    public ImageFile(Path path) {
        this.path = path;
        this.fileName = path.getFileName().toString();
        this.file = new File(String.valueOf(path));
    }

    public Long getLastModified(){
        return this.file.lastModified();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void showPath(){ System.out.println(this.path.toString());}

    public void setURI(String URI){this.URI = URI;}

    public String getURI(){return this.URI;}


    public void setBlog(String blog){this.blog = blog;}

    public String getBlog(){return this.blog;}

    public void setFileNameId(String fileNameId){this.fileNameId = fileNameId;}

    public String getFileNameId(){return this.fileNameId;}
}
