package com.ziyoushenghuo.asyntask;


import com.ziyoushenghuo.common.AppCommon;
import com.ziyoushenghuo.entry.*;
import com.ziyoushenghuo.redis.RedisKeyConstant;
import com.ziyoushenghuo.redis.RedisUtils;
import com.ziyoushenghuo.repository.SuperGroupAppointmentRepository;
import com.ziyoushenghuo.service.CustomerService;
import com.ziyoushenghuo.service.TeamFounderService;
import com.ziyoushenghuo.weixintemplete.WeixinTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;

/* 超级团购消息发送
  @author 王江波
  @version V1.0
*/
@Component
public class GroupAppointmentTemplateMsg {

    private static Logger logger = LoggerFactory.getLogger(GroupAppointmentTemplateMsg.class);
    @Autowired
    SuperGroupAppointmentRepository superGroupAppointmentRepository;

    @Autowired
    TeamFounderService teamFounderService;

    @Autowired
    CustomerService customerService;

    @Autowired
    private WeixinTemplate weixinTemplate;

    @Autowired
    private RedisUtils redisUtils;


    @Async
    public void sendAppointmentTempleMsg(TeamFounder teamFounder) throws InterruptedException {

        try {

            if(teamFounder == null )
            {
                return;
            }

            List<SuperGroupAppointment>superGroupAppointments = superGroupAppointmentRepository.findAllByGroupidAndStatus(teamFounder.getId(),SuperGroupAppointment.PARTICIPATE_ACTION_APPOINTMENT);

            for(SuperGroupAppointment superGroupAppointment:superGroupAppointments)
            {
                try
                {
                    String access_token = (String) redisUtils.get(RedisKeyConstant.TOKEN_ZXTC_WEIXIN_VALUE);
                    Customer customer = customerService.GetOpenIdById(superGroupAppointment.getUserid());

                    if(customer!=null)
                    {
                        weixinTemplate.sendGroupAppointment(access_token,teamFounder.getName(),customer,teamFounder.getId(),teamFounder.getGoodsCover().getId(),superGroupAppointment.getFormid(),false);
                    }
                }catch (Exception e) {
                    if (e.getMessage() != null) {
                        int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                        logger.warn(e.getMessage().substring(0, len));
                    } else {

                        logger.warn(e.toString());
                    }
                }
            }

            superGroupAppointmentRepository.deleteAllByGroupid(teamFounder.getId());

        } catch (Exception e) {
            if (e.getMessage() != null) {
                int len = e.getMessage().length() > AppCommon.DEFAULT_EXCEPTION_LEN ? AppCommon.DEFAULT_EXCEPTION_LEN : e.getMessage().length();

                logger.warn(e.getMessage().substring(0, len));
            } else {

                logger.warn(e.toString());
            }
        }
    }

}
