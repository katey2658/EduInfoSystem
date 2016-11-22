package com.katey2658.ems.util;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by 11456 on 2016/11/21.
 */
public class DBConnectionPool implements DataSource {

    /**
     * 使用LinkedList集合来存放数据库来连接
     * 由于要频繁读写List集合，所以这里使用LinkedList存储数据量连接
     */
    private static LinkedList<Connection> connectionList=new LinkedList<>();


    /**
     *根据数据库配置文件来进行创建和初始化连接池
     */
    static {
        InputStream in=DBConnectionPool.class.getClassLoader().getResourceAsStream("dbconfig.properties");
        //在静态代码中加载db.properties数据库配置
        Properties properties=new Properties();

        try {
            properties.load(in);

            String driver=properties.getProperty("driver");
            String url=properties.getProperty("url");
            String userName=properties.getProperty("username");
            String password=properties.getProperty("password");

            int connectionPoolInitSize=Integer.parseInt(properties.getProperty("connectionPoolInitSize"));

            //加载数据库驱动
            Class.forName(driver);

            for(int i=0;i<connectionPoolInitSize;i++){
                //创建配置文件中设置的连接池大小连接
                Connection connection= DriverManager.getConnection(url,userName,password);
                //将创建好的连接对象放进对应的连接集合中，list集合就是一个存放了数据库连接的连接池
                connectionList.add(connection);
                System.out.print("数据库连接工作结束!"+connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //做一些工作，暂时还没有想到，哈哈
        }
    }


    /**
     * 获取数据库连接对象
     * @return 数据库连接对象
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {

        //如果连接池中可用的connection对象个数大于0，就从池中取出一个，然后将连接对象减少一个
        if(connectionList.size()>0){
            final Connection connection=connectionList.removeFirst();
            System.out.print("当前的连接池大小是!"+connectionList.size());

            //返回Connection的代理对象
            return (Connection) Proxy.newProxyInstance(DBConnectionPool.class.getClassLoader(),
                    connection.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (!method.getName().equals("close")){
                                return method.invoke(connection,args);
                            }else{
                                //如果调用的是Connection对象的close方法，就把conn还给数据库连接池
                                connectionList.add(connection);
                                System.out.println(connection+"：已经被还给数据库连接池");
                                System.out.print("当前的连接池大小是!"+connectionList.size());
                                return null;
                            }
                        }
                    });
        }else{
            throw new RuntimeException("数据库忙！,稍等");
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
