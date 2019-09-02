
package com.ziyoushenghuo.weixintemplete;


import com.google.gson.JsonObject;

import java.util.List;

public class Template {

    public static  int TYPE_ORDERID = 0;
    public static int TYPE_FORMID = 1;
    // 消息接收方
    private String toUser;
    // 模板id
    private String templateId;
    // 模板消息详情链接
    private String page;
    // 消息顶部的颜色
    private String form_id;

    private String prepay_id;

    // 消息顶部的颜色
    private String topColor;

    // 消息发送类型，通过订单，或者formid
    private int type;
    // 参数列表
    private List<TemplateParam> templateParamList;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTopColor() {
        return topColor;
    }

    public void setTopColor(String topColor) {
        this.topColor = topColor;
    }


    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public String toJSONold() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append(String.format("\"touser\":\"%s\"", this.toUser)).append(",");
        buffer.append(String.format("\"template_id\":\"%s\"", this.templateId)).append(",");

        if (this.type == this.TYPE_ORDERID)
        {
            buffer.append(String.format("\"form_id\":\"%s\"", this.prepay_id)).append(",");
        }
        else
        {
            buffer.append(String.format("\"form_id\":\"%s\"", this.form_id)).append(",");
        }



        buffer.append(String.format("\"color\":\"%s\"", this.topColor)).append(",");

        buffer.append("\"data\":{");
        TemplateParam param = null;
        for (int i = 0; i < this.templateParamList.size(); i++) {
            param = templateParamList.get(i);
            // 判断是否追加逗号
            if (i < this.templateParamList.size() - 1){

                buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"},", param.getName(), param.getValue(), param.getColor()));
            }else{
                buffer.append(String.format("\"%s\": {\"value\":\"%s\",\"color\":\"%s\"}", param.getName(), param.getValue(), param.getColor()));
            }

        }
        buffer.append("}");
        buffer.append("}");
        return buffer.toString();
    }

    public String toJSON() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("touser",this.toUser);
        jsonObject.addProperty("template_id",this.templateId);

        jsonObject.addProperty("page",this.page);

        jsonObject.addProperty("touser",this.toUser);


        if (this.type == this.TYPE_ORDERID)
        {
            jsonObject.addProperty("form_id",this.prepay_id);
        }
        else
        {
            jsonObject.addProperty("form_id",this.form_id);;
        }
        JsonObject jsonParms = new JsonObject();

        for (int i = 0; i < this.templateParamList.size(); i++) {

            TemplateParam param   = templateParamList.get(i);

            JsonObject jsonParm = new JsonObject();

            jsonParm.addProperty("value",param.getValue());

            jsonParms.add(param.getName(),jsonParm);
            // 判断是否追加逗号


        }
        jsonObject.add("data",jsonParms);


        return jsonObject.toString();
    }

    public List<TemplateParam> getTemplateParamList() {
        return templateParamList;
    }

    public void setTemplateParamList(List<TemplateParam> templateParamList) {
        this.templateParamList = templateParamList;
    }

}
