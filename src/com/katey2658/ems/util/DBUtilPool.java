package com.katey2658.ems.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 11456 on 2016/11/22.
 */
public class DBUtilPool {

    /**
     * 实例化数据库连接池
     */
    private static DBConnectionPool pool=new DBConnectionPool();

    /**
     * 从数据库连接池中获取连接对象
     * @return connection
     */
    public static Connection getConnection(){
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放数据库连接对象资源
     * @param connection 连接对象
     * @param statement
     * @param resultSet
     */
    public static void release(Connection connection, Statement statement, ResultSet resultSet){
        if (resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
