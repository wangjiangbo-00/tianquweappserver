package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;

@Entity
@Table(name = "sys_album_picture")
public class GoodImageBasic extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pic_id", nullable = false)
    private int id;

    @Column(name = "pic_cover_big",nullable = false)
    private String imageurl_big;

    @Column(name = "pic_cover_mid",nullable = false)
    private String imageurl_mid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getImageurl_mid() {
        return imageurl_mid;
    }

    public void setImageurl_mid(String imageurl_mid) {
        this.imageurl_mid = imageurl_mid;
    }


    public String getImageurl_big() {
        return imageurl_big;
    }

    public void setImageurl_big(String imageurl_big) {
        this.imageurl_big = imageurl_big;
    }
}
