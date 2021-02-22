package com.jeestudio.services.admin.config.jwt;

import com.jeestudio.services.admin.dao.cacheFeign.CacheUserFeign;
import com.jeestudio.utils.Global;
import com.jeestudio.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: JWT Util
 * @author: whl
 * @Date: 2019-12-03
 */
@Component
public class SignCache {

    @Value("${login.mode.single}")
    private Boolean loginModeSingle;

    @Autowired
    private CacheUserFeign cacheUserFeign;

    /**
     * Generate signature and save token cache
     */
    public String getTokenSetCache(String username, String userId) {
        String token = JwtUtil.sign(username, userId);
        String clientId = JwtUtil.getClaim(token, "clientId");
        if (loginModeSingle) {
            cacheUserFeign.deleteLike(Global.PREFIX_SHIRO_REFRESH_TOKEN + username + "_" + userId + "_token_*");
        }
        cacheUserFeign.setObjectExpireMinute(Global.PREFIX_SHIRO_REFRESH_TOKEN + username + "_" + userId + "_token_" + token + "_clientId_" + clientId, token, Global.EXRP_MINUTE);
        return token;
    }

    /**
     * Delete current user's token cache
     */
    public void deleteTokenCache(String token, String currentUserName, String currentUserId) {
        String clientId = JwtUtil.getClaim(token, "clientId");
        cacheUserFeign.deleteObject(Global.PREFIX_SHIRO_REFRESH_TOKEN + currentUserName + "_" + currentUserId + "_token_" + token + "_clientId_" + clientId);
    }
}
