import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

class DataConnect {
    private Connection conn;

    DataConnect() {
    }

    // 建立连接
    boolean connect(String driver, String connectString) {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(connectString, "user", "123");//连接字符串,用户名，口令
            return true;  //连接成功
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    boolean existQuery(String sqlSentence) {
        ResultSet rs = executeQuery(sqlSentence);
        try {
            return (rs != null && rs.next());
        } catch (Exception ex) {

        }
        return false;
    }

    // 执行SQL查询语句, 返回结果集
    ResultSet executeQuery(String sqlSentence) {
        Statement stat;
        ResultSet rs = null;

        try {
            stat = conn.createStatement(); // 获取执行sql语句的对象
            rs = stat.executeQuery(sqlSentence); // 执行sql查询，返回结果集
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rs;
    }

    //执行SQL更新语句,失败返回false，成功则返回true并把影响的记录数保存在cnt中。
    boolean executeUpdate(String sqlSentence) {
        Statement stat;

        int cnt = -1;
        try {
            stat = conn.createStatement();         // 根据连接获取一个执行sql语句的对象
            cnt = stat.executeUpdate(sqlSentence); //执行sql语句,返回所影响行记录的个数
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
        return (cnt >= 0);
    }

    // 显示查询结果（Adapter模式）
    ArrayList<HashMap<String, Object>> getRecords(String querySql, String fields, String types) {
        ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();

        ResultSet rs = executeQuery(querySql.replace("{0}", fields));
        String arrFields[] = fields.split(",");

        try {
            while (rs.next()) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                for (int i = 0; i < arrFields.length; i++) {
                    hm.put(arrFields[i], rs.getObject(arrFields[i]));
                }
                records.add(hm);
                // System.out.println(rs.getString("ID"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return records;
    }

}