package com.katey2658.ems.controller;

import com.katey2658.ems.common.SessionScanTask;
import com.sun.jmx.snmp.tasks.Task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;


/**
 * Created by 11456 on 2016/11/23.
 */
public class SessionScannListener implements HttpSessionListener,ServletContextListener {

    /**
     * 定义一个集存储服务器创建的HttpSession
     * 但是LinkedList不是一个线程安全的集合，所以使用Collections.synchronized(List<T> list)方法将LinkedList包装成一个线程安全的list集合
     */
    private List<HttpSession> sessionList=null;

    //定义一个对象锁，用这把锁来保证往集合添加新的session和遍历session这两个操作达到同步
    private final Object lock=new Object();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        /*
        思考题：在web应用程序启动的时候应该做一些什么事情

              ：是否应该在这里启动一个守护线程
         */
        System.out.print("Web应用程序启动");
        //启动一个定时任务，每隔30秒检查一次
        new Timer().schedule(new SessionScanTask(sessionList,lock),30);
        //初始化一个线程安全的list
        sessionList=Collections.synchronizedList(new LinkedList<HttpSession>());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        /*
        思考题：在程序关闭的时候应该做一些什么事情
               关闭守护线程
         */

        System.out.print("Web应用程序关闭");
    }
    /**
     * 在session创建的时候锁触发的事件
     * @param httpSessionEvent
     */
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        //先获取所创建session的引用
        HttpSession session=httpSessionEvent.getSession();
        System.out.println("session:"+session.getId()+"被创建");

        //先获取一个同步锁，然后将这个新创建的session放到sessionList
        synchronized (lock){
            sessionList.add(session);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        //先获取所创建session的引用
        HttpSession session=httpSessionEvent.getSession();
        System.out.println("session:"+session.getId()+"被销毁");

        /*
        思考题：考虑一个问题，对锁资源的争抢会导致性能和体验上不足
         */
        //先获取一个同步锁，然后将这个新创建的session移出sessionList
        synchronized (lock){
            sessionList.remove(session);
        }
    }
}
