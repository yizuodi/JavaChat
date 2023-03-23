import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

// 与客户聊天的方法均在这里，包括所有客户端线程。
// 用ArrayList<<HashMap<String,Object>>>保存这些线程信息。HashMap保存了每个连接的用户名和SlaveSock实例.
class ClientChat {
    private TcpServerChat main;
    private ArrayList<HashMap<String, Object>> sockList;

    ClientChat(TcpServerChat main) {
        this.main = main;
        sockList = new ArrayList<HashMap<String, Object>>();
    }

    // 增加新的连接套接字
    HashMap<String, Object> addSocket(Socket sock) {
        SlaveSock slaveSock = new SlaveSock(sock);
        String msg = slaveSock.read();  // 读取传递的消息
        if (msg != null && msg.length() > 1) {
            String type = msg.substring(0, 1);  // 获取消息类型
            String[] user = msg.substring(1, msg.length() - 2).split("#");  // 获取用户名和密码
            if (type.equals("2")) {
                // 登录
                int loginRes = main.login(user[0], user[1]);
                if (loginRes != 0) {
                    // 登录信息错误
                    slaveSock.write(loginRes + "error\r\n"); // 1:用户名不存在 2:密码错误
                    slaveSock.close();
                    return null;
                } else if (findUserOnline(user[0]) != null) {
                    // 用户已经在线
                    slaveSock.write("3error\r\n");
                    slaveSock.close();
                    return null;
                } else if (!slaveSock.write("0ok\r\n")) {
                    // 写入信息失败
                    return null;
                } else {
                    // 登录成功
                    HashMap<String, Object> hm = addUser(user[0], slaveSock);
                    main.txtUserAppend(user[0] + "\r\n");
                    try {
                        Thread.sleep(200L);  // 等待200毫秒
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    slaveSock.write("3" + main.getOldMsg(10));  // 获取最近10条消息
                    return hm;
                }
            } else if (type.equals("4")) {
                // 注册
                boolean regRes = main.reg(user[0], user[1]);
                if (regRes) {
                    // 注册成功
                    slaveSock.write("0ok\r\n");
                } else {
                    // 注册失败
                    slaveSock.write("5error\r\n");
                }
            }
        }
        slaveSock.close();  // 关闭套接字
        return null;
    }

    // 查找给定在线用户对应的套接字
    SlaveSock findUserOnline(String user) {
        Iterator it = sockList.iterator();
        HashMap<String, Object> hm;
        do {
            if (!it.hasNext()) {
                return null;
            }
            hm = (HashMap) it.next();
        } while (!user.equals(hm.get("user")));
        return (SlaveSock) hm.get("sock");
    }

    // 添加新登录用户对应的套接字
    HashMap<String, Object> addUser(String userName, SlaveSock sock) {
        HashMap<String, Object> hm = new HashMap();
        // 添加用户
        hm.put("user", userName);
        // 添加套接字
        hm.put("sock", sock);
        synchronized (main) {
            sockList.add(hm);
        }
        return hm;
    }

    // 从在线用户列表中删除用户
    boolean removeUser(HashMap<String, Object> hm) {
        String user = (String) hm.get("user");
        SlaveSock sock = (SlaveSock) hm.get("sock");
        sock.close();
        synchronized (main) {
            sockList.remove(hm);
        }
        main.txtUserRemove(user + "\r\n");
        return true;
    }

    // 从客户端获取消息（基于“编程参考”）
    void readFromClient(HashMap<String, Object> hm) {
        (new Thread() {
            public void run() {
                HashMap<String, Object> hmc = hm;
                SlaveSock sock = (SlaveSock) hmc.get("sock");
                while (true) {
                    String msg = sock.read();
                    if (msg == null) {
                        sock.write("0U\r\n");  // 发送关闭连接消息
                        break;
                    }
                    String type = "";
                    try {
                        type = msg.substring(0, 1);
                    } catch (Exception e) {
                        System.out.println("有用户离线。");
                        break;
                    }
                    if (type.equals("0")) {   // 收到关闭连接消息
                        break;
                    }
                    if (type.equals("3")) {   // 收到普通消息，转发给所有已登录用户
                        sendToAll("3" + getNow(1) + "   " + hmc.get("user") + "：" + msg.substring(1));
                        main.insertMsg(hmc.get("user").toString(), msg.substring(1), getNow(0));
                    } else if (type.equals("4"))   // 收到修改密码的消息(未实现)
                        changePass(msg.substring(1));
                }
                removeUser(hmc);  // 从在线用户列表中删除用户
            }  // run
        }).start();
    } // readFrom

    // 获取当前时间（0代表含年月日的格式，1代表不含年月日的格式）
    static String getNow(int type) {
        Calendar cal = Calendar.getInstance();  // 获取当前时间
        SimpleDateFormat sformat = new SimpleDateFormat(type == 0 ? "yyyy/MM/dd HH:mm:ss" : "HH:mm:ss");  // 格式化时间
        return sformat.format(cal.getTime());  // 返回格式化后的时间
    }

    void changePass(String msg) {
        // todo: 实现修改密码
    }

    // 发送消息给所有已登录用户
    void sendToAll(String msg) {
        main.txtReceiveAppend(msg.substring(1));
        Iterator it = sockList.iterator();
        while (it.hasNext()) {
            HashMap<String, Object> hm = (HashMap) it.next();
            SlaveSock sock = (SlaveSock) hm.get("sock");
            sock.write(msg);
        }
    }

    // 关闭所有套接字
    boolean closeAll() {
        Iterator it = sockList.iterator();
        while (it.hasNext()) {
            HashMap<String, Object> hm = (HashMap) it.next();
            SlaveSock sock = (SlaveSock) hm.get("sock");
            sock.write("0U\r\n");  // 发送关闭连接消息
            sock.close();
        }
        return true;
    }
}

