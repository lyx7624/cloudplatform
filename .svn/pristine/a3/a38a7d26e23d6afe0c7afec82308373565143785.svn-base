package com.zcyk.service;

import com.zcyk.dto.LoginUser;
import com.zcyk.pojo.TToken;

import java.util.Map;

/**
* 功能描述: 用户token
* 版本信息: Copyright (c)2019
* 公司信息: 智辰云科
* 开发人员: lyx
* 版本日志: 1.0
* 创建日期: 2020-4-15 10:03
*/
public interface TokenService {

	TToken saveToken(LoginUser loginUser);

	void refresh(LoginUser loginUser);

	LoginUser getLoginUser(String token);

	boolean deleteToken(String token);


	String createJWTToken(LoginUser loginUser);
	Map<String, Object> getMapFromJWT(String jwt);

}