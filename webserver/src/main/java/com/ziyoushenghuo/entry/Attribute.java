package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;


@Entity
@Table(name = "zytc_attribute_value")

public class Attribute extends BasicMessage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attr_value_id", nullable = false)
    private int id;

    @Column(name = "class_id",nullable = false)
    private int classid;

    @Column(name = "type",nullable = false)
    private int type;

    @Column(name = "sort",nullable = false)
    private int sort;

    @Column(name = "value",nullable = false)
    private String value;

    @Column(name = "attr_value_name",nullable = false)
    private String attr_value_name;


    @Column(name = "is_search",nullable = false)
    private int is_search;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttr_value_name() {
        return attr_value_name;
    }

    public void setAttr_value_name(String attr_value_name) {
        this.attr_value_name = attr_value_name;
    }

    public int getIs_search() {
        return is_search;
    }

    public void setIs_search(int is_search) {
        this.is_search = is_search;
    }
}
