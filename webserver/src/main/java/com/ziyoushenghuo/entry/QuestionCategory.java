package com.ziyoushenghuo.entry;

import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;


@Entity
@Table(name = "sys_question_category")
public class QuestionCategory extends BasicMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "catname")
    private String catname;

    @Column(name = "logo")
    private String logo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
