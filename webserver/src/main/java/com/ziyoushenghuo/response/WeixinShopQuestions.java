package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.*;

import java.util.List;

public class WeixinShopQuestions extends  BasicMessage{

      int catid;
      String catname;
      String logo;
      List<Question> questionList;

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
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

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
