package com.zcyk.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcyk.mapper.TMessageMapper;
import com.zcyk.mapper.UserMapper;
import com.zcyk.mapper.UserMessageMapper;
import com.zcyk.pojo.TMessage;
import com.zcyk.pojo.User;
import com.zcyk.pojo.UserMessage;
import com.zcyk.service.TMessageService;
import com.zcyk.service.UserService;
import com.zcyk.dto.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/9/6 11:14
 */
@Service
public class TMessageServiceImpl implements TMessageService {

    @Autowired
    UserService userService;
    @Autowired
    UserMessageMapper userMessageMapper;
    @Autowired
    TMessageMapper tMessageMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    UserMapper userMapper;

    /**
     * 功能描述：查询登录人的所有消息
     * 开发人员： lyx
     * 创建时间： 2019/9/6 14:27
     */
    public PageInfo<TMessage> selAllMessage(int pageNum, int pageSize, String search) {
        User nowUser =userService.getNowUser(request);

        PageHelper.startPage(pageNum, pageSize);
        List<UserMessage> userMessages = userMessageMapper.selectByUser(nowUser.getId());
        List<TMessage> tMessages = new ArrayList<>();
        for (UserMessage userMessage : userMessages) {
            TMessage tMessage = tMessageMapper.selectByUser(userMessage.getMessage_id(), search);
            tMessages.add(tMessage);
        }
        PageInfo<TMessage> pageInfo = new PageInfo<>(tMessages);
        return pageInfo;
    }


    /**
     * 功能描述：查看单个消息
     * 开发人员： lyx
     * 创建时间： 2019/9/6 14:52
     */
    public TMessage selOneMessage(String id) {
        return tMessageMapper.selectByPrimaryKey(id);
    }


    /**
     * 功能描述：删除消息（个人消息）
     * 开发人员： lyx
     * 创建时间： 2019/9/6 14:54
     */
    public ResultData deleteMessage(List<String> allid) {
        User nowUser =userService.getNowUser(request);

        int i = 0;
        for (String id : allid) {
            i += userMessageMapper.updateById(id, nowUser.getId());
        }
        if (i != allid.size()) {
            return new ResultData().setMsg("失败").setStatus("400");
        }
        return new ResultData().setMsg("成功").setStatus("200");
    }

    /*发送消息*/
    public ResultData sendUser(List<User> users, String Message_id) {
        int i = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());
        for (User user : users) {
            i += userMessageMapper.insert(new UserMessage().setUser_id(user.getId())
                    .setMessage_id(Message_id)
                    .setId(UUID.randomUUID().toString())
                    .setStatus(1)
                    .setUpdateTime(date));
        }
        if (i != users.size()) return new ResultData().setStatus("400").setMsg("失败");
        return new ResultData().setStatus("200").setMsg("成功");
    }

    /**
     * 功能描述：发送消息（群体-->公告）
     * 开发人员： lyx
     * 创建时间： 2019/9/6 15:03
     */
    public ResultData sendMessage(TMessage tMessages, List<String> ids) {
        User nowUser =userService.getNowUser(request);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());

        //创建消息
        tMessages.setCreate_time(dateFormat.format(date))
                .setCreate_user(nowUser.getId())//看是否需要存放姓名
                .setStatus(1)
                .setId(UUID.randomUUID().toString());

        tMessageMapper.insertSelective(tMessages);
        Integer acceptor_type = tMessages.getAcceptor_type();
        switch (acceptor_type) {
            case 1://平台全部用户，靠！很多啊,是否会存在添加失败
                List<User> userList1 = userMapper.selectAll();
                return sendUser(userList1, tMessages.getId());

            case 2://部分企业所有员工 ids则为企业id集合
                List<User> userList2 = new ArrayList<>();
                for (String id : ids) {
                    List<User> list = userMapper.selectAllUser(id);
                    userList2.addAll(list);
                }
                return sendUser(userList2, tMessages.getId());
            case 3://部分企业管理员 ids则为企业id集合
                List<User> userList3 = new ArrayList<>();
                for (String id : ids) {
                    List<User> list = userMapper.selectAllManageUser(id);
                    userList3.addAll(list);
                }
                return sendUser(userList3, tMessages.getId());
            case 4://部分人员
                List<User> userList4 = new ArrayList<>();
                for (String id : ids) {
                    User user = new User().setId(id);
                    userList4.add(user);
                }
                return sendUser(userList4, tMessages.getId());
        }

        return new ResultData().setStatus("200").setMsg("成功");
    }


}