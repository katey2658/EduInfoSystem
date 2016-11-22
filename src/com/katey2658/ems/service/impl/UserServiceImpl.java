package com.katey2658.ems.service.impl;

import com.katey2658.ems.entity.User;
import com.katey2658.ems.entity.UserInfo;
import com.katey2658.ems.service.UserService;

import java.util.Collection;

/**
 * Created by 11456 on 2016/11/21.
 */
public class UserServiceImpl implements UserService {


    @Override
    public int signIn(String userId, String userPwd) {
        return 0;
    }

    @Override
    public int signInByMail(String userMail, String userPwd) {
        return 0;
    }

    @Override
    public int signUp(User user) {
        return 0;
    }

    @Override
    public int logOut(String userId) {
        return 0;
    }

    @Override
    public int closeUser(String userId) {
        return 0;
    }

    @Override
    public Collection<User> showUsersByPage(int page) {
        return null;
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        return null;
    }

    @Override
    public int updateUserInfo(UserInfo userInfo) {
        return 0;
    }

    @Override
    public User findUserById(String userId) {
        return null;
    }

    @Override
    public int resetUserPwd(String userId, String oldUserPwd, String newUserPwd) {
        return 0;
    }

    @Override
    public User resetUserMail(String userId, String oldUserMail, String newUserMail) {
        return null;
    }

}
