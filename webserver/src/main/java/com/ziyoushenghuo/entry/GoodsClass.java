package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods_class")

public class GoodsClass extends BasicMessage {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false)
    private int id;

    @Column(name = "class_name",nullable = false)
    private String class_name;

    @Column(name = "spec_id_array",nullable = false)
    private String spec_id_array;

    @Column(name = "is_use",nullable = false)
    private int is_use;

    @Column(name = "sort",nullable = false)
    private int sort;

    @Column(name = "create_time",nullable = false)
    private Date createtime;

    @Column(name = "modify_time",nullable = false)
    private Date modifytime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSpec_id_array() {
        return spec_id_array;
    }

    public void setSpec_id_array(String spec_id_array) {
        this.spec_id_array = spec_id_array;
    }

    public int getIs_use() {
        return is_use;
    }

    public void setIs_use(int is_use) {
        this.is_use = is_use;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }
}
