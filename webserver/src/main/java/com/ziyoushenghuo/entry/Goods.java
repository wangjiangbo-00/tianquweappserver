package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zytc_goods")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "goods.all",
                attributeNodes = {//attributeNodes 来定义需要懒加载的属性
                        @NamedAttributeNode("shop")//无延伸

                }
                )
})
public class Goods extends BasicMessage {

    public static final int GOODS_STATE_NORMAL = 0;
    public static final int GOODS_STATE_OK = 1;
    public static final int GOODS_STATE_APPLY= 2;
    public static final int GOODS_STATE_REFUSE= 3;
    public static final int GOODS_STATE_FORBIT = 4;
    public static final int GOODS_STATE_FORBIT_APPLY = 5;
    public static final int GOODS_STATE_FORBIT_REFUSE = 6;
    public static final int GOODS_TYPE_NORMAL = 0;
    public static final int GOODS_TYPE_GIFT = 1;


    public void CoverWithEdit(GoodsEdit goodsEdit)
    {
        catid = goodsEdit.getCatid();
        goodslicense = goodsEdit.getGoodslicense();
        goodsname = goodsEdit.getGoodsname();
        imgarr = goodsEdit.getImgarr();
        weight = goodsEdit.getWeight();
        group_price = goodsEdit.getGroup_price();
        price = goodsEdit.getPrice();
        description =goodsEdit.getDescription();
        introduction = goodsEdit.getIntroduction();



        sort = goodsEdit.getSort();


        group_number = goodsEdit.getGroup_number();

        feeid = goodsEdit.getFeeid();

    }



    public int getFeeid() {
        return feeid;
    }

    public void setFeeid(int feeid) {
        this.feeid = feeid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id", nullable = false)
    private int id;

    @Column(name = "category_id",nullable = false)
    private int catid;

    @Column(name = "code",nullable = false)
    private String goodslicense;


    @Column(name = "goods_spec_format",nullable = false)
    private String goods_spec_format;

    @Column(name = "goods_name",nullable = false)
    private String goodsname;

    @Column(name = "img_id_array",nullable = false)
    private String imgarr;

    @Column(name = "goods_weight",nullable = false)
    private int weight;



    @Column(name = "group_price",nullable = false)
    private double group_price;

    @Column(name = "price",nullable = false)
    private double price;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "introduction",nullable = false)
    private String introduction;


    @Column(name = "shortdesp",nullable = false)
    private String shortdesp;

    @Column(name = "pictureurl",nullable = true)
    private String pictureurl;

    @Column(name = "bannerurl",nullable = true)
    private String bannerurl;

    @Column(name = "type",nullable = false)
    private int type;


    @Column(name = "stock",nullable = true)
    private int stock;

    @Column(name = "create_time",nullable = false)
    private Date createtime;



    @Column(name = "real_sales",nullable = false)
    private int sell_count;


    @Column(name = "sort",nullable = false)
    private int sort;

    @Column(name = "openselflift",nullable = false)
    private int openselflift;


    @Column(name = "selfliftreturn",nullable = false)
    private int selfliftreturn;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shop_id")
    private  Shop shop;

    @Column(name = "group_number",nullable = false)
    private int group_number;

    @Column(name = "shop_state",nullable = false)
    private int shopstate;

    @Column(name = "state",nullable = false)
    private int state;

    @Column(name = "shipping_fee_id",nullable = false)
    private int feeid;


    @Column(name = "formid",nullable = true)
    private String formid;


    @Column(name = "extra",nullable = true)
    private String extra;

    @Transient
    private List<String> gallery;

    @Transient
    private List<GoodsSku> goodsSkuList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getGoodslicense() {
        return goodslicense;
    }

    public void setGoodslicense(String goodslicense) {
        this.goodslicense = goodslicense;
    }

    public String getGoodsname() {
        return goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }


    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }


    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getImgarr() {
        return imgarr;
    }

    public void setImgarr(String imgarr) {
        this.imgarr = imgarr;
    }

    public double getGroup_price() {
        return group_price;
    }

    public void setGroup_price(double group_price) {
        this.group_price = group_price;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public int getOpenselflift() {
        return openselflift;
    }

    public void setOpenselflift(int openselflift) {
        this.openselflift = openselflift;
    }

    public int getSelfliftreturn() {
        return selfliftreturn;
    }

    public void setSelfliftreturn(int selfliftreturn) {
        this.selfliftreturn = selfliftreturn;
    }

    public int getGroup_number() {
        return group_number;
    }

    public void setGroup_number(int group_number) {
        this.group_number = group_number;
    }

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    public int getShopstate() {
        return shopstate;
    }

    public void setShopstate(int shopstate) {
        this.shopstate = shopstate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSell_count() {
        return sell_count;
    }

    public void setSell_count(int sell_count) {
        this.sell_count = sell_count;
    }

    public List<GoodsSku> getGoodsSkuList() {
        return goodsSkuList;
    }

    public void setGoodsSkuList(List<GoodsSku> goodsSkuList) {
        this.goodsSkuList = goodsSkuList;
    }

    public String getGoods_spec_format() {
        return goods_spec_format;
    }

    public void setGoods_spec_format(String goods_spec_format) {
        this.goods_spec_format = goods_spec_format;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getBannerurl() {
        return bannerurl;
    }

    public void setBannerurl(String bannerurl) {
        this.bannerurl = bannerurl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getShortdesp() {
        return shortdesp;
    }

    public void setShortdesp(String shortdesp) {
        this.shortdesp = shortdesp;
    }


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }


    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
