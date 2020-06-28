package com.zcyk.filter;

import com.zcyk.dto.ResultData;
import com.zcyk.mapper.BimVipMapper;
import com.zcyk.pojo.BimVip;
import com.zcyk.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/9/2 15:21
 */

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2019/9/2 15:21
 */
//@WebFilter(urlPatterns = "/bim/*", filterName = "bimfilter")
//@Order(2)
public class BimVipFilter implements Filter
{


    @Autowired
    BimVipMapper bimVipMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String project_id = req.getParameter("project_id");
        if(StringUtils.isNotBlank(project_id)){//有项目的激活码 0 1 3
            //比较这个token是不是对应这个项目的
            BimVip bimVip = bimVipMapper.selectOneVip(project_id);
            if(bimVip!=null && bimVip.getStatus()==1){//验证码正确
                chain.doFilter(req, res);
            }else if(bimVip!=null && bimVip.getStatus()==0){
                ResponseUtil.responseJson(req, res, 200, new ResultData("477", "未激活", null));
            }
            ResponseUtil.responseJson(req, res, 200, new ResultData("476", "未购买", null));
        }else {
            ResponseUtil.responseJson(req, res, 200, new ResultData("476", "未购买", null));
        }
    }
}

