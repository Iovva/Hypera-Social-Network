package repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class JdbcUtils {

    String url;
    String user = null;
    String password = null;


    public JdbcUtils(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public JdbcUtils(String url){
        this.url = url;
    }

    private Connection instance=null;

    private Connection getNewConnection(){

        Connection con=null;
        try {
            if (user!=null && password!=null)
                con= DriverManager.getConnection(url,user,password);
            else
                con=DriverManager.getConnection(url);   //sqlite
        } catch (SQLException e) {
            System.out.println("Error getting connection "+e);
        }
        return con;
    }

        public Connection getConnection(){
        try {
            if (instance==null || instance.isClosed())
                instance=getNewConnection();

        } catch (SQLException e) {
            System.out.println("Error DB "+e);
        }
        return instance;
    }
}
