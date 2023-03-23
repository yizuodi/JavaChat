import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// 访问数据库的表Msg
class Msg {
    private DataConnect dataConn;

    Msg(DataConnect dataConn) {
        this.dataConn = dataConn;
    }

    // 插入一条消息
    boolean insertMsg(String user, String msg, String time) {
        dataConn.executeUpdate(String.format("insert into Msg(User_Cde,Msg,WriteTime) values('%s','%s','%s');", user, msg, time));
        return true;
    }

    // 读取最近的count条消息
    String getOldMsg(int count) {
        String res = "";
        ArrayList<HashMap<String, Object>> al = dataConn.getRecords(String.format("select top %d * from Msg order by ID desc", count), "User_Cde,Msg,WriteTime", "");
        // 将al中查询结果倒序存储到al2中
        ArrayList<HashMap<String, Object>> al2 = new ArrayList<>();
        for (int i = al.size() - 1; i >= 0; i--) {
            al2.add(al.get(i));
        }
        HashMap hmc;  // 一条消息
        String tim;  // 消息的时间
        for (Iterator it = al2.iterator(); it.hasNext(); res = res + tim + "   " + hmc.get("User_Cde") + "#" + hmc.get("Msg")) {
            hmc = (HashMap) it.next();
            tim = hmc.get("WriteTime").toString();
            // tim = tim.substring(tim.length() - 10, tim.length() - 2);
            tim = tim.substring(11, 19);
        }
        return res;  // 返回最后count条消息拼接成的字符串
    }
}
