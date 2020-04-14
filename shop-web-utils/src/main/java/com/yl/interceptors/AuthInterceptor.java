package com.yl.interceptors;

import com.alibaba.fastjson.JSON;
import com.yl.annotations.LoginRequired;
import com.yl.util.CookieUtil;
import com.yl.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 拦截代码
        // 判断被拦截的请求访问的方法的注解(是否需要拦截)
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        StringBuffer url = request.getRequestURL();
        System.out.println("已拦截请求：          " + url);

        // 是否拦截
        if (methodAnnotation == null) {
            // 只有返回true才会继续向下执行，返回false取消当前请求
            return true;
        }

        String token = "";

        //从Cookie中拿token
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        //有token
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }

        //从请求中拿token
        String newToken = request.getParameter("token");
        //有token
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        // 是否必须登录
        boolean loginSuccess = methodAnnotation.loginSuccess();// 获得该请求是否必登录成功

        // 调用认证中心进行验证
        String success = "fail";
        Map<String, String> successMap = new HashMap<>();
        //判断token是否不为空且长度不为0且不由空白符构成
        //有token
        if (StringUtils.isNotBlank(token)) {
            String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();// 从request中获取ip
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            String successJson = HttpclientUtil.doGet("http://localhost:8011/verify?token=" + token + "&currentIp=" + ip);

            successMap = JSON.parseObject(successJson, Map.class);

            success = successMap.get("status");

        }

        if (loginSuccess) {
            // 必须登录成功才能使用
            if (!success.equals("success")) {
                //没有登录过
                //重定向会passport登录
                //这里可以得到原来要访问的目标服务的url
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://localhost:8011/index?ReturnUrl=" + requestURL);
                return false;
            }

            //上面token经过verify方法校验成功
            // 需要将token携带的用户信息写入
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("nickname", successMap.get("nickname"));
            //验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
            }

        } else {
            // 没有登录也能用，但是必须验证
            if (success.equals("success")) {
                // 需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));

                //验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }

            }
        }


        return true;
    }
}
