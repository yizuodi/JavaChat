import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JRegister extends JFrame {
    private static final long serialVersionUID = 1L;   // 本语句用于序列化，可以不要

    private JTextField txtName;
    private JPasswordField txtPass;
    private JFrame jf;
    private TcpClientChat main;

    JRegister(TcpClientChat main) {
        this.setSize(400, 300); // 设置width和height
        this.setLocationRelativeTo(null); // 居屏幕中间
        this.setLayout(null); // 空布局，可以准确的定位组件在容器中的位置和大小
        this.setTitle("用户注册");
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

        JButton JB1 = new JButton("注册");
        JB1.setBounds(160, 160, 100, 30);
        this.add(JB1);
        ActionListener al = new ActionListener() { // 加上按键事件
            @Override
            public void actionPerformed(ActionEvent e) {
                String nam = txtName.getText().trim();
                String pas = String.valueOf(txtPass.getPassword()).trim();
                if (nam.isEmpty() || pas.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名/密码不能为空！");
                    return;
                } else if (nam.contains("#")) {
                    JOptionPane.showMessageDialog(null, "用户名不能包含'#'。");
                } else if (reg(nam, pas)) {
                    jf.setVisible(false);
                }
            }
        };
        JB1.addActionListener(al);

        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    boolean reg(String userName, String userPass) {
        try {
            if (!main.reg(userName, userPass)) {
                int errorCode = main.getErrorCode();
                if (errorCode == 4) {
                    JOptionPane.showMessageDialog(null, "连接服务器失败。");
                } else if (errorCode == 5) {
                    JOptionPane.showMessageDialog(null, "服务器反馈注册失败。");
                }
                else{
                    JOptionPane.showMessageDialog(null, "未知错误。");
                }
                return false;
            } else {
                JOptionPane.showMessageDialog(null, "注册成功。");
                this.jf.setVisible(false);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}