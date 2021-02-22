package com.jeestudio.utils;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Description: Jwt token
 * @author: whl
 * @Date: 2019-12-03
 */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token){
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
