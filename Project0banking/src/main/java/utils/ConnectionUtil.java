package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {

    public static Connection getConnection() throws SQLException, IOException {
        /*
        First we want to load the properties from application.properties. We want to avoid hard coding our credentials
        in the repo.

        We use the class loader to gain access to "resources" which are files on the classpath

        We use a Properties object which can parse our key/value pairs. We ask for the key, we get the value.

         */

    InputStream inputStream = ConnectionUtil.class.getClassLoader().getResourceAsStream("application.properties");
    Properties props = new Properties();
    props.load(inputStream);

        /*
        Now that we have our properties we can use these to establish a connection
         */
//        Class.forName("org.postgresql.jdbc");
    Connection conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));



        /*
        Now that we have a connection to the database we can use it to create statements to execute.
         */
//        String sql = "INSERT INTO test VALUES (1, 'kplummer')";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//
//        pstmt.executeUpdate();

    return conn;
}

}
