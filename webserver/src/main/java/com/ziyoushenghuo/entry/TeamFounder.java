package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_group")


@NamedEntityGraphs({
        @NamedEntityGraph(name = "group.all",
                attributeNodes = {//attributeNodes 来定义需要懒加载的属性
                        @NamedAttributeNode("goodsCover"), @NamedAttributeNode("shopCover")//无延伸

                }
        )

})
public class TeamFounder extends BasicMessage {

    public static final int GROUP_STATUS_NORMAL = 0;
    public static final int GROUP_STATUS_OK = 1;
    public static final int GROUP_STATUS_FAIL = 2;

    public static final int GROUP_TYPE_USER = 1;
    public static final int GROUP_TYPE_PLATFORM = 2;

    public static final int GROUP_STATUS_VOID = 0;
    public static final int GROUP_STATUS_APPLY = 1;
    public static final int GROUP_STATUS_APPLY_OK = 2;
    public static final int GROUP_STATUS_APPLY_FAIL = 3;
    public static final int GROUP_STATUS_CAN_SHOW = 4;
    public static final int GROUP_STATUS_START = 5;
    public static final int GROUP_STATUS_END = 6; //等待收礼者填写
    public static final int GROUP_STATUS_CLOSE = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_order_id", nullable = false)
    private int id;

    @Column(name = "require_num",nullable = true)
    private int requirenum;

    @Column(name = "people",nullable = true)
    private int people;

    @Column(name = "status",nullable = true)
    private int status;


    @Column(name = "create_time",nullable = true)
    private Date createtime;

    @Column(name = "expire_time",nullable = true)
    private Date expiretime;

    @Column(name = "success_time",nullable = true)
    private Date successtime;


    @Column(name = "owner_id",nullable = true)
    private int ownerid;

    @OneToOne
    @JoinColumn(name="goodsid")
    private  GoodsCover goodsCover;

    @Column(name = "nickname",nullable = true)
    private String nickname;

    @Column(name = "headpic",nullable = true)
    private String headpic;

    @Column(name = "start_time",nullable = true)
    private Date starttime;

    @Column(name = "stage_format",nullable = true)
    private String stage_format;

    @OneToOne
    @JoinColumn(name="shop_id")
    @NotFound(action= NotFoundAction.IGNORE)
    private  ShopCover shopCover;

    @Column(name = "name",nullable = true)
    private String name;

    @Column(name = "groupresult",nullable = true)
    private int groupresult;

    @Column(name = "type",nullable = true)
    private int type;

    @Column(name = "qrpic")
    private String qrpic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequirenum() {
        return requirenum;
    }

    public void setRequirenum(int requirenum) {
        this.requirenum = requirenum;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGroupresult() {
        return groupresult;
    }

    public void setGroupresult(int groupresult) {
        this.groupresult = groupresult;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(Date expiretime) {
        this.expiretime = expiretime;
    }

    public Date getSuccesstime() {
        return successtime;
    }

    public void setSuccesstime(Date successtime) {
        this.successtime = successtime;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }


    public String getStage_format() {
        return stage_format;
    }

    public void setStage_format(String stage_format) {
        this.stage_format = stage_format;
    }


    public GoodsCover getGoodsCover() {
        return goodsCover;
    }

    public void setGoodsCover(GoodsCover goodsCover) {
        this.goodsCover = goodsCover;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ShopCover getShopCover() {
        return shopCover;
    }

    public void setShopCover(ShopCover shopCover) {
        this.shopCover = shopCover;
    }


    public String getQrpic() {
        return qrpic;
    }

    public void setQrpic(String qrpic) {
        this.qrpic = qrpic;
    }
}
