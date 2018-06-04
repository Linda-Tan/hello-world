package com.junliang.spring.zuulfilter;

import com.alibaba.fastjson.JSON;
import com.junliang.spring.constant.ResponseCode;
import com.junliang.spring.pojo.bean.UserInfo;
import com.junliang.spring.pojo.vo.BaseResponse;
import com.junliang.spring.util.RSAHelper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessFilter extends ZuulFilter {

    @Value("${zuul.prefix}")
    private String zuulPrefix;
    @Value("${zuul.ignore-url}")
    private String ignoreUrl;
    @Value("${myProps.jwt.token-header}")
    private String jwtTokenHeader;
    //@Value("${jwt.pub-key.path}")
    @Value("${myProps.jwt.rsa-public-key-file-path}")
    private String pubKeyPath;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (isIgnorePath(request.getRequestURI()))
            return null;
        String token = request.getHeader(jwtTokenHeader);
        if(StringUtils.isBlank(token)) {
            log.warn("access token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }
        try {
            //校验token合法性
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(RSAHelper.getBase64RSAPublicKey(pubKeyPath)).parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            UserInfo userInfo = new UserInfo();
            userInfo.setId(body.getId());
            userInfo.setName(body.getSubject());
            log.info("parser token : {}", body);
        } catch (Exception e) {
            ctx.setResponseBody(JSON.toJSONString(new BaseResponse(ResponseCode.EX_OTHER_CODE, "Token error or Token is Expired!")));
            log.error(e.getMessage());
        }
        return null;
    }

    private boolean isIgnorePath(String path) {
        for (String url : ignoreUrl.split(",")) {
            if (path.substring(zuulPrefix.length()).startsWith(url)) {
                return true;
            }
        }
        return false;
    }

}
