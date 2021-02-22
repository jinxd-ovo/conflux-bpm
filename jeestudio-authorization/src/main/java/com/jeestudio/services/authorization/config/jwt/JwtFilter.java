package com.jeestudio.services.authorization.config.jwt;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.jeestudio.services.authorization.CacheManager.feign.CacheFeignImpl;
import com.jeestudio.utils.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description: Jwt filter
 * @author: whl
 * @Date: 219-12-03
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private CacheFeignImpl cacheFeign;

    public JwtFilter() {
        this.cacheFeign = new CacheFeignImpl();
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginAttempt(request, response)) {
            try {
                this.executeLogin(request, response);
            } catch (Exception e) {
                String msg_en = "Token expired or incorrect, " + e.getMessage();
                String msg = "Token过期或不正确";
                Throwable throwable = e.getCause();
                if (throwable instanceof SignatureVerificationException) {
                    msg_en = "Incorrect token";
                    msg = "Token不正确";
                } else if (e.getMessage().contains("Token expired")) {
                    if (this.refreshToken(request, response)) {
                        return true;
                    } else {
                        msg_en = "Token Expired";
                        msg = "Token过期或不正确";
                    }
                } else {
                    if (throwable != null) {
                        msg_en = throwable.getMessage();
                        msg = throwable.getMessage();
                    }
                }
                this.response401(response, msg, msg_en);
                return false;
            }
            return true;
        } else {
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            String httpMethod = httpServletRequest.getMethod();
            String requestURI = httpServletRequest.getRequestURI();
            logger.info("Current request {} Authorization Attribute(Token) Empty Request type {}", requestURI, httpMethod);
            this.response401(response, "请先登录", "Please login first");
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String token = httpRequest.getHeader("token");
        return token != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("token", httpRequest.getHeader("token"));
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "token");
        JwtToken token = new JwtToken(httpRequest.getHeader("token"));
        Subject su = this.getSubject(request, response);
        su.login(token);
        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String token = httpRequest.getHeader("token");
        String username = JwtUtil.getClaim(token, Global.USERNAME);
        String userId = JwtUtil.getClaim(token, Global.USERID);
        String clientId = JwtUtil.getClaim(token, "clientId");
        String key = Global.PREFIX_SHIRO_REFRESH_TOKEN + username + "_" + userId + "_token_" + token + "_clientId_" + clientId;
        boolean exist = cacheFeign.exist(key);
        if (exist) {
            Object redisToken = cacheFeign.getString(key);
            String t = redisToken.toString();
            if (t.equals(token)) {
                //new token
                String token_ = JwtUtil.sign(username, userId);
                JwtToken jwtToken = new JwtToken(token_);
                String clientId_ = JwtUtil.getClaim(token_, "clientId");
                //30 minute
                cacheFeign.setObjectExpireMinute(Global.PREFIX_SHIRO_REFRESH_TOKEN + username + "_" + userId + "_token_" + token_ + "_clientId_" + clientId_, token_, Global.EXRP_MINUTE);
                this.getSubject(request, response).login(jwtToken);
                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                httpServletResponse.setHeader("token", token_);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "token");
                //1 minute delay
                cacheFeign.setObjectExpireMinute(Global.PREFIX_SHIRO_REFRESH_TOKEN + username + "_" + userId + "_token_" + token + "_clientId_" + clientId, token, 1L);
                return true;
            }
        }
        return false;
    }

    private void response401(ServletResponse response, String msg, String msg_en) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String data = JsonConvertUtil.objectToJson(new ResultJson(1001, msg, msg_en, null));
            out.append(data);
        } catch (IOException e) {
            logger.error("Return Response IOException:" + e.getMessage());
        }
    }
}
