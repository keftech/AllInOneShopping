package kef.technology.allinoneshopping;

import java.io.Serializable;

public class ListItem implements Serializable {
    private final String title, url; private final int imageRes; private int num_freq=0;

    public ListItem(String title, int imageRes, String url){
        this.title = title; this.imageRes = imageRes; this.url = url;
    }

    public void setNum_freq(int num_freq) {
        this.num_freq = num_freq;
    }

    public int getNum_freq() {
        return num_freq;
    }

    public String getTitle() {
        return title;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getUrl() {
        return url;
    }
}
