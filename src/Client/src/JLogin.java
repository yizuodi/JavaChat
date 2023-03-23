import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JPasswordField;

class JLogin extends JFrame {
    private static final long serialVersionUID = 1L;   // 本语句用于序列化，可以不要

    private JTextField txtName;
    private JPasswordField txtPass;
    private JFrame jf;
    private TcpClientChat main;

    JLogin(TcpClientChat main) {
        this.setSize(400, 300); // 设置width和height
        this.setLocationRelativeTo(null); // 居屏幕中间
        this.setLayout(null); // 空布局，可以准确的定位组件在容器中的位置和大小
        this.setTitle("用户登录");
        jf = this;
        this.main = main;
        // 注意：JPasswordField用于密码，取值方法String.valueOf(pass.getPassword())
        txtName = new JTextField(main.getUserName(), 30); // 初值，列数
        JLabel nameLab = new JLabel("用户:");  //
        nameLab.setBounds(100, 60, 40, 20);  // left,top,width,height
        txtName.setBounds(160, 60, 120, 20);
        this.add(nameLab);
        this.add(txtName);

        // 密码
        txtPass = new JPasswordField("", 30); // 初值，列数
        JLabel passLab = new JLabel("密码:");  //
        passLab.setBounds(100, 90, 40, 20);  // left,top,width,height
        txtPass.setBounds(160, 90, 120, 20);
        this.add(passLab);
        this.add(txtPass);

        // 文本
        /*
        JTextField txtMsg = new JTextField(main.getMsg(), 30); // 初值，列数
        JLabel msgLab = new JLabel("消息:");  //
        msgLab.setBounds(100, 120, 40, 20);  // left,top,width,height
        txtMsg.setBounds(160, 120, 120, 20);
        this.add(msgLab);
        this.add(txtMsg);
        */

        JButton JB1 = new JButton("登录");
        // JButton JB1 = new JButton("发送");
        JB1.setBounds(160, 160, 100, 30);
        this.add(JB1);
        ActionListener al = new ActionListener() { // 加上按键事件
            @Override
            public void actionPerformed(ActionEvent e) {
                String nam = txtName.getText().trim();
                String pas = String.valueOf(txtPass.getPassword()).trim();
                // String msg = txtMsg.getText().trim();
                if (nam.isEmpty() || pas.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名/密码不能为空！");
                    return;
                }
                else if (conn(nam, pas)) {
                    jf.setVisible(false);
                }
            }
        };
        JB1.addActionListener(al);

        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
    // 连接服务器
    boolean conn(String userName, String userPass) {
        try {
            if (!main.conn(userName, userPass)) {
                int errorCode = main.getErrorCode();
                if (errorCode == 2) {
                    JOptionPane.showMessageDialog(null, "密码错误。");
                }
                else if (errorCode == 3) {
                    JOptionPane.showMessageDialog(null, "当前帐号已在其他地点登录。");
                }
                else if (errorCode == 4) {
                    JOptionPane.showMessageDialog(null, "连接服务器失败。");
                }
                else{
                    JOptionPane.showMessageDialog(null, "用户不存在。");
                }
                return false;
            } else {
                JOptionPane.showMessageDialog(null, "登录成功。");
                main.receiveFromServer();
                this.jf.setVisible(false);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}