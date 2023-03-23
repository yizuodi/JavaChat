import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.HashMap;

// 主窗口，也是其他实例(ClientChat, MasterSock, SlaveSock)的代理。
public class TcpServerChat extends JFrame {
    private final String DRIVER = "com.hxtt.sql.access.AccessDriver";
    private final String CONN_STRING = "jdbc:Access:///./msg.mdb"; // .为当前目录，即本项目的根目录。
    private DataConnect dataConn;
    private MasterSock masterSock; // 建立监听套接字
    private ClientChat clientChat;
    private JTextArea txtReceive;
    private JTextArea txtUser;
    private TcpServerChat main;
    private User users;
    private Msg msgs;

    // 设置标题
    void setTitle() {
        if (isBound()) {
            this.setTitle("服务器：已绑定端口" + getPort());
        } else {
            this.setTitle("服务器：未绑定");
        }
    }

    // 获取端口号
    int getPort() {
        return masterSock.getPort();
    }

    // 循环接收请求
    void accept() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    Socket socket = masterSock.accept();
                    HashMap<String, Object> hm = null;
                    if (socket != null) {
                        hm = clientChat.addSocket(socket);
                        if (hm != null) {
                            clientChat.readFromClient(hm);
                        }
                    }
                }
            }
        }.start();
    }

    // 绑定端口
    boolean bind(int port) {
        if (!masterSock.bind(port)) {
            return false;
        }
        setTitle();
        return true;
    }

    // 解除绑定
    boolean unbind() {
        if (!(masterSock.unbind()) || !(clientChat.closeAll())) {
            return false;
        }
        setTitle();
        return true;
    }

    // 判断是否绑定端口
    boolean isBound() {
        return masterSock.isBound();
    }

    // 请求登录
    int login(String userName, String password) {
        return users.checkUserPass(userName, password);
    }

    // 请求注册
    boolean reg(String userName, String password) {
        return users.reg(userName, password);
    }

    // 获取count条旧消息
    String getOldMsg(int count) {
        return msgs.getOldMsg(count);
    }

    // 向在线用户展示框中添加用户名
    void txtUserAppend(String userName) {
        txtUser.append(userName);
    }

    // 从在线用户展示框移除用户名
    void txtUserRemove(String userName) {
        // 输出
        String tmp = txtUser.getText();
        txtUser.setText(tmp.replaceFirst(userName, ""));
    }

    // 向数据库中添加消息
    void insertMsg(String userName, String msg, String time) {
        msgs.insertMsg(userName, msg, time);
    }

    // 向接收消息展示框中添加消息
    void txtReceiveAppend(String msg) {
        txtReceive.append(msg);
    }

    // 退出
    void exit() {
        unbind();
        this.setVisible(false);
        System.exit(0);
    }

    TcpServerChat() {
        masterSock = new MasterSock();
        clientChat = new ClientChat(this);
        dataConn = new DataConnect();
        if (!dataConn.connect(DRIVER, CONN_STRING)) {
            System.out.println("Connect Error!");
            exit();
        }
        setTitle();  // 设置标题
        main = this;
        this.setSize(600, 500); // 设置width和height
        this.setLocationRelativeTo(null); // 居屏幕中间
        this.setLayout(null); // 空布局，可以准确的定位组件在容器中的位置和大小
        txtReceive = new JTextArea();
        this.txtReceive.setEditable(false);
        JScrollPane jsp = new JScrollPane(txtReceive);
        jsp.setBounds(50, 50, 300, 300);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        jsp.setAutoscrolls(true);
        this.add(jsp);
        txtUser = new JTextArea();
        this.txtUser.setEditable(false);
        JScrollPane jsp2 = new JScrollPane(txtUser);
        jsp2.setBounds(400, 50, 100, 300);
        jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        jsp2.setAutoscrolls(true);
        this.add(jsp2);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("触发windowClosing事件");

                try {
                    unbind();
                } catch (Exception var3) {
                }

            }
        });

        addMenu();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        users = new User(dataConn);
        msgs = new Msg(dataConn);

        //showMessages();
		/*while (true) {
			try{
				ServerSocket serverSocket = new ServerSocket(8000); // 建立监听套接字
				Socket socket = serverSocket.accept(); // 建立连接套接字. 从请求队列中取客户端的连接请求，没有则阻塞***
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());// 建立连接输入字节流
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());// 建立连接输出字节流
				String str = inputFromClient.readUTF(); // 读入UTF-8编码的字符. 从输入缓冲区读入字符，没有则阻塞***
				String[] s = str.split("#");
				// outputToClient.writeUTF(getNow() + "  " + s[0] + ":  " + s[1]); // 输出UTF-8编码的字符
				// 应答改为从数据库中读取上一条消息
				ResultSet rs = dataConn.executeQuery("select * from msg order by id desc");
				rs.next();
				String time = rs.getString("WriteTime").substring(11, 19);
				outputToClient.writeUTF(time + "  " + rs.getString("User_Cde") + ":  " + rs.getString("Msg"));
				//addMessages(s[0],s[1]);
				socket.close(); // 关闭连接
				serverSocket.close(); // 关闭监听
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 */
    }

    // 添加菜单
    JMenuBar addMenu() {
        JMenuBar jmenu = new JMenuBar();   // 创建菜单
        this.setJMenuBar(jmenu);    // 不能设定位置，会自动放在最上部

        // 添加菜单
        JMenu menu1 = new JMenu(" 操作");
        JMenu menu2 = new JMenu(" 帮助");
        JMenuItem item1 = new JMenuItem(" 绑定端口");
        JMenuItem item2 = new JMenuItem(" 解除绑定");
        JMenuItem item3 = new JMenuItem(" 退出");
        JMenuItem item4 = new JMenuItem(" 关于");

        // 添加菜单项至菜单上
        menu1.add(item1);
        menu1.add(item2);
        menu1.add(item3);

        menu2.add(item4);

        jmenu.add(menu1);
        jmenu.add(menu2);

        ActionListener al = new ActionListener() { // 定义菜单点击事件
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = e.getActionCommand();
                if (" 绑定端口".equals(str)) {
                    if (isBound()) {
                        JOptionPane.showMessageDialog(null, "端口已绑定，无法操作绑定。", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        new JBindPort(main);
                    }
                } else if (" 解除绑定".equals(str)) {
                    if (!isBound()) {
                        JOptionPane.showMessageDialog(null, "端口未绑定，无法操作解除。", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        if (unbind()) {
                            JOptionPane.showMessageDialog(null, "解除绑定成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "解除绑定失败。", "提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else if (" 退出".equals(str)) {
                    exit();
                } else if (" 关于".equals(str)) {
                    JOptionPane.showMessageDialog(null, "www.yizuodi.cn");
                }
            }
        };

        item1.addActionListener(al);   // 菜单项加上点击事件
        item2.addActionListener(al);
        item3.addActionListener(al);
        item4.addActionListener(al);

        return jmenu;
    }

    /*
    void showMessages() {
        txtReceive.setText("");
        ResultSet rs = dataConn.executeQuery("select User_Cde,Msg,WriteTime From Msg order by ID");

        try {
            while(rs.next()) {
                //txtaReceive.append(rs.getString("User_Cde")+ " " + rs.getString("Msg") + " " + rs.getString("WriteTime") + "\r\n");  // 加到末尾
                // 规范时间格式为时分秒
                String time = rs.getString("WriteTime").substring(11, 19);
                txtReceive.append(time + " " + rs.getString("User_Cde")+ " " + rs.getString("Msg"));  // 加到末尾
            }
        }
        catch(Exception ex) {

        }
    }

    void addMessages(String userName, String msg) {
        String values = String.format("'%s','%s','%s'", userName, msg, getNow());
        try {
            dataConn.executeUpdate("insert into Msg(User_Cde,Msg,WriteTime) values("
                    + values +")");
        }
        catch(Exception ex) {

        }
        if(dataConn.existQuery("select User_Cde from Msg where User_Cde='"+userName+"'"))
            showMessages();
    }

    static String getNow() {
        Calendar cal= Calendar.getInstance();  // 获取当前时间
        SimpleDateFormat sformat  =   new  SimpleDateFormat("HH:mm:ss");  // 格式化时间
        return sformat.format(cal.getTime());  // 返回格式化后的时间
    }
    */
    public static void main(String[] args) throws Exception {
        new TcpServerChat();
    }
}