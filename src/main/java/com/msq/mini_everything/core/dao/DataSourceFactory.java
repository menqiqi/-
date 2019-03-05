package com.msq.mini_everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.msq.mini_everything.config.MiniEverythingConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {

    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory(){}

    public static DataSource dataSource(){
        if (dataSource == null){
            synchronized (DataSourceFactory.class){
                if (dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    //采用的是h2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //获取当前工程路径
                    //JDBC规范中关于H2 jdbc:h2:/filepath -> 存储到本地文件
                    dataSource.setUrl("jdbc:h2:"+ MiniEverythingConfig.getInstance().getH2IndexPath());

                    //Druid数据库连接池的可配置参数
                    dataSource.setValidationQuery("select now()");
                    dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }



    public static void initDatabase(){
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句
        //try-with-resources  关闭资源，比在finally块中写简洁许多
        try (InputStream in = DataSourceFactory.class.getClassLoader()
                .getResourceAsStream("mini_everything.sql")){
            if (in == null){
                throw new RuntimeException("Not read initialize database script please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();


            try( BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line = null;
                while ((line = reader.readLine()) != null){
                    if (!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }

            //3.通过数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();
            //JDBC
            //3.1获取数据库的连接
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3执行SQL语句
            statement.execute();

            connection.close();
            statement.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
