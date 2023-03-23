import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TcpClientChat extends JFrame{
	/*
	private final int LEFT_POS = 40;
	private final int INPUT_WIDTH = 360;
	*/
	private String userName = "张三";
	//private String pass = "123";
	private TcpClientChat main;
	//private String msg = "Hello, world!";
	private JTextField txtSend;
	private JTextArea txtReceive;
	private Sock sock = null;  // 代理模式：代理Sock实例
	private int errorCode = 0;  // 错误代码 0：正常 1：连接失败 2：发送失败 3：接收失败

	// 设定用户名
	void setUserName(String userName) {
		this.userName = userName;
		setTitle();
	}

	// 获取用户名
	String getUserName() {
		return userName;
	}

	// 设定主机名
	void setServerName(String server) {
		sock.setServerName(server);
	}

	// 获取主机名
	String getServerName() {
		return sock.getServerName();
	}

	// 设定端口号
	void setPort(int port) {
		sock.setPort(port);
	}

	// 获取端口号
	int getPort() {
		return sock.getPort();
	}

	// 获取错误代码
	int getErrorCode() {
		return errorCode;
	}

	// 建立连接，并返回是否连接成功
	boolean conn() {
		return sock.conn();
	}

	// 返回是否连接
	boolean isConn() {
		return sock.isConn();
	}

	// 套接字读取数据
	String read() {
		return sock.read();
	}

	// 套接字发送数据
	boolean write(String msg) {
		return sock.write(msg);
	}

	// 关闭套接字
	boolean disconn() {
		write("0U\r\n");  //要求服务器关闭连接
		try {
			Thread.sleep(200);  // 等待服务器关闭连接
		} catch(Exception ex) {
		}
		return close();
	}

	// 关闭套接字
	boolean close() {
		return sock.close();
	}
	// （使用多线程方法）从服务器获取消息
	void receiveFromServer(){
		(new Thread(){
			@Override
			public void run() {
				while(sock != null && isConn()) {
					// 当设置了套接字且当前正在连接状态时
					try{
						String msg = read();  // 获取消息
						String type = msg.substring(0, 1);  // 获取响应码
						if(type.equals("3")){  // 3-收到普通消息. 其他：1-登录成功消息
							addMsg(msg.substring(1));  // 输出消息
						} else if (type.equals("0") && close()) {  // 0-关闭连接消息
							// 服务器关闭连接
							setTitle();
						}

					} catch (Exception ex) {
					}
				}
			}
		}).start();

	}

	// 与服务器连接登录
	boolean conn(String userName, String userPass) {
		if (conn()){
			txtSend.setEnabled(true);
			write("2" + userName + "#" + userPass + "\r\n");  // 2代表这是登录请求
			String type = read().substring(0, 1);
			if (!type.equals("0")) {  // 返回值不为0，表示登录失败
				errorCode = Integer.parseInt(type);  // 保存错误代码
				disconn();
				setTitle();
				return false;
			} else {
				errorCode = 0;  // 0代表没有错误，登录成功
				clearMsg();  // 登录后清屏，以便显示历史消息
				setUserName(userName);
				setTitle();
				return true;
			}
		} else{
			errorCode = 4; // 4代表连接服务器失败
			setTitle();
			return false;
		}
	}

	// 注册
	boolean reg(String userName, String userPass) {
		if (conn()){
			txtSend.setEnabled(true);
			write("4" + userName + "#" + userPass + "\r\n");  // 2代表这是注册请求
			String type = read().substring(0, 1);
			if (!type.equals("0")) {  // 返回值不为0，表示注册失败
				errorCode = Integer.parseInt(type);  // 保存错误代码
				disconn();
				setTitle();
				return false;
			} else {
				errorCode = 0;  // 0代表没有错误，注册成功
				disconn();
				return true;
			}
		} else{
			errorCode = 4; // 4代表连接服务器失败
			return false;
		}
	}

	// 在消息框中添加消息
	void addMsg(String msg) {
		if (msg != null && !msg.equals("")) {
			txtReceive.append(msg);
		}
	}

	// 清除消息展示框中的消息
	void clearMsg() {
		txtReceive.setText("");
	}

	// 退出
	void exit() {
		sock.close();  // 先关闭socket
		System.exit(0);  // 退出系统。不会触发windowClosing事件
	}

	// 设置主窗口标题
	void setTitle() {
		String title = getServerName() + ":" + getPort() + "@" + userName;
		if (isConn()) {
			this.setTitle("已登录 -- " + title);
		} else {
			this.setTitle("未登录 -- " + title);
		}
	}

	TcpClientChat() {
		this.setSize(600, 500);           // 设置主窗口的width和height
		this.setLocationRelativeTo(null); // 居屏幕中间
		this.setLayout(null);             // 空布局，可以定位组件在容器中的位置和大小
		this.setJMenuBar(addMenu(this));
		main = this;
		this.txtSend = new JTextField("Hello, world!",30);
		JLabel sendLab = new JLabel("输入:");
		JLabel receiveLab = new JLabel("收到:");
		sendLab.setBounds(50, 50, 40, 20);
		this.txtSend.setBounds(100, 50, 360, 20);
		receiveLab.setBounds(50, 80, 40, 20);
		this.txtReceive = new JTextArea();
		this.add(sendLab);
		this.add(txtSend);
		this.add(receiveLab);

		JScrollPane jsp = new JScrollPane(this.txtReceive);
		jsp.setBounds(100, 80, 360, 300);
		jsp.setVerticalScrollBarPolicy(22);
		this.add(jsp);

		JButton JB1 = new JButton("发送");
		JB1.setBounds(480, 50, 80, 20);
		this.add(JB1);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!sock.isConn()){
					setTitle();
					JOptionPane.showMessageDialog(null, "请先登录。");
				} else if(!write("3"+txtSend.getText()+"\r\n")){
					setTitle();
					JOptionPane.showMessageDialog(null, "服务器连接错误，发送失败。");
				}
			}
		};
		JB1.addActionListener(al);
		this.sock = new Sock();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("触发windowClosing事件");	// 点击窗口右上角X关闭时的事件
				try {
					disconn();
				} catch(Exception ex) {

				}
			}
		});
		setTitle();
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 使窗口右上角X关闭有效
	}

	// 添加菜单
	JMenuBar addMenu(JFrame jf) {
		JMenuBar jmenu = new JMenuBar();   // 创建菜单
		jf.setJMenuBar(jmenu);    // 不能设定位置，会自动放在最上部

		// 添加菜单
		JMenu menu1 = new JMenu(" 操作");
		JMenu menu2 = new JMenu(" 帮助");
		JMenuItem item1 = new JMenuItem(" 用户登录");
		JMenuItem item2 = new JMenuItem(" 取消登录");
		JMenuItem item3 = new JMenuItem(" 用户注册");
		JMenuItem item4 = new JMenuItem(" 设置");
		JMenuItem item5 = new JMenuItem(" 退出");
		JMenuItem item6 = new JMenuItem(" 关于");

		// 添加菜单项至菜单上
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);
		menu1.add(item4);
		menu1.add(item5);

		menu2.add(item6);

		jmenu.add(menu1);
		jmenu.add(menu2);

		ActionListener al = new ActionListener() { // 定义菜单点击事件
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if (" 用户登录".equals(str)) {
					if (sock.isConn()) {
						JOptionPane.showMessageDialog(null, "请先取消登录。");
					}
					else{
						new JLogin(main);
					}
				} else if (" 取消登录".equals(str)) {
					if (disconn()) {
						setTitle();
						clearMsg();
						JOptionPane.showMessageDialog(null, "取消登录成功。");
					}
					else{
						JOptionPane.showMessageDialog(null, "请先登录。");
					}
				} else if (" 用户注册".equals(str)) {
					if (sock.isConn()) {
						JOptionPane.showMessageDialog(null, "请先取消登录。");
					}
					else{
						new JRegister(main);
					}
				} else if (" 设置".equals(str)) {
					if (sock.isConn()) {
						JOptionPane.showMessageDialog(null, "请先取消登录。");
					}
					else{
						new JSetting(main);
					}
				} else if (" 退出".equals(str)) {
					exit();
				} else if(" 关于".equals(str)){
					JOptionPane.showMessageDialog(null, "www.yizuodi.cn");
				}
			}
		};

		item1.addActionListener(al);   // 菜单项加上点击事件
		item2.addActionListener(al);
		item3.addActionListener(al);
		item4.addActionListener(al);
		item5.addActionListener(al);
		item6.addActionListener(al);

		return jmenu;
	}

	/*
	String getNow() {
		Calendar cal= Calendar.getInstance();
		return String.format("%tT", cal) + "   ";
	}

	void send_msg(String userName, String msg) throws Exception{
		// 用户名+"#"+消息
		String str1 = userName + "#" + msg + "\r\n";
		Socket socket = new Socket("localhost", 8000); // 与服务器 建立连接。
		DataInputStream fromServer = new DataInputStream(socket.getInputStream());  // 建立连接输入字节流
		DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());  // 建立连接输出字节流（到服务器）
		toServer.writeUTF(str1);  // 输出UTF-8编码的字符（到服务器）
		String str2 = fromServer.readUTF();   // 从输入缓冲区读入字符，没有则阻塞***
		txtReceive.append(str2);  // 输出字符串
		socket.close(); // 关闭连接
	}

	void setMsg(String msg) {
		this.msg = msg;
	}
	String getMsg() {
		return this.msg;
	}
	*/
	public static void main(String[] args) { //throws 出错后把错误处理抛给上层
		new TcpClientChat();
	}
}
