package com.ziyoushenghuo.response;


import com.ziyoushenghuo.entry.Customer;
import com.ziyoushenghuo.entry.Goods;
import com.ziyoushenghuo.entry.Order;
import com.ziyoushenghuo.entry.TeamFounder;

import java.util.List;

public class GroupDetailsMessage  extends  BasicMessage
{


    Order order;
    List<Grouper> users;

    TeamFounder teamFounder;

    Goods goods;



    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Grouper> getUsers() {
        return users;
    }

    public void setUsers(List<Grouper> users) {
        this.users = users;
    }

    public TeamFounder getTeamFounder() {
        return teamFounder;
    }

    public void setTeamFounder(TeamFounder teamFounder) {
        this.teamFounder = teamFounder;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }


    public  static class Grouper{

        long join_time;
        int user_id;
        String avatar;
        String nickname;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getJoin_time() {
            return join_time;
        }

        public void setJoin_time(long join_time) {
            this.join_time = join_time;
        }
    }
}
