import java.util.ArrayList;
import java.util.HashMap;

// 访问数据库的表User
class User {
    private DataConnect dataConn;

    User(DataConnect dataConn) {
        this.dataConn = dataConn;
    }

    // 检查用户是否存在
    boolean checkUserExist(String userName) {
        return dataConn.existQuery("select * from Users where User_Cde='" + userName + "';");
    }

    // 检查用户名密码是否正确。返回值：0-正确，1-用户名不存在，2-密码错误
    int checkUserPass(String userName, String pass) {
        ArrayList<HashMap<String, Object>> al = dataConn.getRecords("select {0} from Users Where User_Cde='" + userName + "'", "User_Pass", "");
        if (al.size() == 0) {
            return 1;
        } else {
            String resPass = (String) ((HashMap) al.get(0)).get("User_Pass");
            return resPass.equals(pass) ? 0 : 2;
        }
    }

    // 用户注册，向数据库中插入数据
    boolean reg(String userName, String pass) {
        if (checkUserExist(userName)) {
            return false;  // 用户已存在
        } else {
            dataConn.executeUpdate("insert into Users(User_Cde,User_Pass) values('" + userName + "','" + pass + "');");
            return checkUserExist(userName);  // 检查是否插入成功
        }
    }
}
