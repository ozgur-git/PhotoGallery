package com.example.photogallery;

public class Photos {

    int page;
    int pages;
    int perpage;
    int total;
    Photo[] photo;
    String stat;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Photo[] getPhoto() {
        return photo;
    }

    public void setPhoto(Photo[] photoArray) {
        this.photo = photoArray;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
