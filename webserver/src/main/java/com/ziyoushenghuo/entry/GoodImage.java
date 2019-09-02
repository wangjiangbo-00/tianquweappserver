package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;

@Entity
@Table(name = "sys_album_picture")
public class GoodImage extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pic_id", nullable = false)
    private int id;


    @Column(name = "pic_cover",nullable = false)
    private String imageurl;



    @Column(name = "pic_cover_mid",nullable = false)
    private String imageurl_mid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImageurl_mid() {
        return imageurl_mid;
    }

    public void setImageurl_mid(String imageurl_mid) {
        this.imageurl_mid = imageurl_mid;
    }

}
