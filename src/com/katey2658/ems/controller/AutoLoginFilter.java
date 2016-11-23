package com.katey2658.ems.controller;

import com.katey2658.ems.common.StringEncryption;
import com.katey2658.ems.entity.User;
import com.katey2658.ems.service.UserService;
import com.katey2658.ems.service.impl.UserServiceImpl;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @Description  处理用户自动登录的过滤器
 * Created by 11456 on 2016/11/23.
 */
public class AutoLoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //获取请求和响应对象
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;

        //如果已经登录，就直接放行
        if (request.getSession().getAttribute("user")!=null){
            filterChain.doFilter(request,response);
            return;
        }


        //@userName用户名  @userPwd 密码
        String userId=null;
        String userPwd=null;
        //获取用户请求中的cookie,然后获取用户名和密码
        Cookie[] cookies=request.getCookies();
        for (Cookie cookie:cookies){
            if (cookie!=null&&cookie.getName().endsWith("userid")){
                userId=cookie.getValue();
            }else if(cookie!=null&&cookie.getName().endsWith("userpwd")){
                userPwd=cookie.getValue();
            }
        }

        //如用户名和密码都不为空，就检验账号密码
        if(userId!=null&&userPwd!=null){
            //获取用户服务对象
            UserService userService=new UserServiceImpl();
            //检验账号是否存在，存在就获取对象信息放到session中,然后放行
            if(userService.signIn(userId, StringEncryption.getEncryptionString(userPwd))==0){

                User user=userService.findUserById(userId);

                request.getSession().setAttribute("user",user);

                filterChain.doFilter(request,response);
            }
        }
        //用户还没有登录，交给login.jsp处理
        request.getRequestDispatcher("/login.jsp").forward(request,response);
    }

    @Override
    public void destroy() {
    }
}
