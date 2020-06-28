package com.zcyk.util;

import com.zcyk.dto.LoginUser;
import com.zcyk.service.serviceImpl.TokenServiceDbImpl;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * 开发人员: xlyx
 * 创建日期: 2020-4-15 9:16
 */

public class JwtUtil {


    private static final String JWT_SECRET = "(ZHI:)_$^11244^%$_(CHEN:)_@@++--(YUN:)_++++_.sds_(KE:)";

    private static Key KEY = null;
    private static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";
    private static final String EXPIRE_TIME = "EXPIRE_TIME";


    /**
     * 功能描述：从令牌中获取到信息集合
     * 开发人员： lyx
     * 创建时间： 2019/8/1 17:35
     */
    public static Map<String, Object> getMapFromJWT(String jwt) {
        if ("null".equals(jwt) || StringUtils.isBlank(jwt)) {
            return null;
        }
        return Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwt).getBody();
//        Map<String, Object> map = new HashMap<>();
//        map.put("LOGIN_USER_KEY",jwt);
//        map.put("EXPIRE_TIME",System.currentTimeMillis()*10);
//        return map;

    }

    /**
     * 功能描述：根据user生成jwttoken
     * 开发人员： lyx
     * 创建时间： 2019/8/1 17:39
     */
    public static String createJWTToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(LOGIN_USER_KEY, loginUser.getId());// 放入id
        claims.put(EXPIRE_TIME, loginUser.getExpireTime());// 过期时间
        //生成token
        String jwtToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, getKeyInstance())
                .compact();

        return jwtToken;
//        return loginUser.getId();
    }

    /**
     * 功能描述：secret生成秘钥
     * 开发人员： lyx
     * 创建时间： 2019/8/1 17:29
     */
    private static Key getKeyInstance() {
        if (KEY == null) {
            synchronized (TokenServiceDbImpl.class) {
                if (KEY == null) {// 双重锁，不用redis可以不加
                    /*加盐*/
                    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWT_SECRET);
                    KEY = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
                }
            }
        }

        return KEY;
    }

}