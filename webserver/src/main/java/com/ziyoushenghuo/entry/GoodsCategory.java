package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;

@Entity
@Table(name = "zytc_goods_category")
public class GoodsCategory extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private int id;

    @Column(name = "category_name",nullable = false)
    private String name;



    @Column(name = "category_pic",nullable = false)
    private String poster;


    @Column(name = "is_visible",nullable = false)
    private int visible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }
}
