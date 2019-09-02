package com.ziyoushenghuo.entry;





import com.ziyoushenghuo.response.BasicMessage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "zytc_team_follower")
public class TeamFollower extends BasicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Column(name = "follow_user_id",nullable = true)
    private int followUserid;



    @Column(name = "follow_user_nickname",nullable = true)
    private String followUusernickname;

    @Column(name = "follow_user_head_pic",nullable = true)
    private String followUserhead_pic;


    @Column(name = "follow_time",nullable = false)
    private Date followtime;


    @Column(name = "order_id",nullable = true)
    private int orderid;


    @Column(name = "found_id",nullable = true)
    private int foundid;


    @Column(name = "found_user_id",nullable = true)
    private int found_userid;


    @Column(name = "team_id",nullable = true)
    private int teamid;


    @Column(name = "status",nullable = true)
    private int status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFollowUserid() {
        return followUserid;
    }

    public void setFollowUserid(int followUserid) {
        this.followUserid = followUserid;
    }

    public String getFollowUusernickname() {
        return followUusernickname;
    }

    public void setFollowUusernickname(String followUusernickname) {
        this.followUusernickname = followUusernickname;
    }

    public String getFollowUserhead_pic() {
        return followUserhead_pic;
    }

    public void setFollowUserhead_pic(String followUserhead_pic) {
        this.followUserhead_pic = followUserhead_pic;
    }

    public Date getFollowtime() {
        return followtime;
    }

    public void setFollowtime(Date followtime) {
        this.followtime = followtime;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getFoundid() {
        return foundid;
    }

    public void setFoundid(int foundid) {
        this.foundid = foundid;
    }

    public int getFound_userid() {
        return found_userid;
    }

    public void setFound_userid(int found_userid) {
        this.found_userid = found_userid;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
