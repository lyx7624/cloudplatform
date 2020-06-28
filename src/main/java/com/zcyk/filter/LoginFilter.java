package com.zcyk.filter;

import com.zcyk.dto.ResultData;
import com.zcyk.dto.LoginUserMap;
import com.zcyk.service.TokenService;
import com.zcyk.util.JwtUtil;
import com.zcyk.util.ResponseUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


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
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
@Order(1)
public class LoginFilter implements Filter {


    /*不拦截的接口*/
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/getCode", "/login", "/getCodePic", "/singin", "/addCompany", "/forgetPwd","/FileDownload","/OfficeServer","/temp/"
                    ,"/ProcessInfo/getPDF","/ProcessInfo/savePDF","/bim"
            )));

    @Value("${allowedOriginsIP}"+"/")
    String allowedOriginsIP;




    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String referer = req.getHeader("Referer");
        String user_token = req.getHeader("user_token");
        Map<String, Object> mapFromJWT = JwtUtil.getMapFromJWT(user_token);
        Long expire_time = MapUtils.getLong(mapFromJWT, "EXPIRE_TIME");//过期时间，暂时没用，用的有效期的map
        String login_user_key = MapUtils.getString(mapFromJWT, "LOGIN_USER_KEY");

        String path = req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");

        Integer inLoginUsers = LoginUserMap.isInLoginUsers(login_user_key, user_token);//用户登录容器校验


        if(!req.getMethod().equals("OPTIONS")){//预请求不验证权限
            if(ALLOWED_PATHS.contains(path) || path.contains("/temp/") || path.contains("/bim/")){//相关接口不拦截，访问临时文件不拦截
                chain.doFilter(req, res);
            }else if(/*!allowedOriginsIP.equals(referer) ||*/ StringUtils.isBlank(user_token)) {//请求没有带上token或者非前端请求
                res.setStatus(555);
                res.sendRedirect(allowedOriginsIP + "#/userLogin");//跳转到登录页面

//            }else if(System.currentTimeMillis()> expire_time){
//                ResponseUtil.responseJson(req,res,555,new ResultData("555","登录过期",null));
            }else if(0!=inLoginUsers) {//登录容器里面没的 账号和token  记录
                if(1==inLoginUsers){
                    ResponseUtil.responseJson(req, res, 555, new ResultData("555", "异地登录", null));
                }else {
                    ResponseUtil.responseJson(req, res, 555, new ResultData("555", "登陆过期", null));
                }

            }else {
                chain.doFilter(req, res);
            }
        }else{
            chain.doFilter(req, res);
        }


    }
}

