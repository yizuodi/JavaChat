import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JSetting extends JFrame {
    private static final long serialVersionUID = 1L;   // 本语句用于序列化，可以不要
    private JTextField txtServer;
    private JTextField txtPort;
    private JFrame jf;

    JSetting(TcpClientChat main) {
        this.setSize(400, 300); // 设置width和height
        this.setLocationRelativeTo(null); // 居屏幕中间
        this.setLayout(null); // 空布局，可以准确的定位组件在容器中的位置和大小
        this.setTitle("设置");
        jf = this;
        // 注意：JPasswordField用于密码，取值方法String.valueOf(pass.getPassword())
        txtServer = new JTextField(main.getServerName(), 30); // 初值，列数
        JLabel serverLab = new JLabel("服务器:");  //
        serverLab.setBounds(100, 60, 40, 20);  // left,top,width,height
        txtServer.setBounds(160, 60, 120, 20);
        this.add(serverLab);
        this.add(txtServer);

        // 端口
        txtPort = new JTextField("" + main.getPort(), 30); // 初值，列数
        JLabel portLab = new JLabel("端口:");  //
        portLab.setBounds(100, 90, 40, 20);  // left,top,width,height
        txtPort.setBounds(160, 90, 120, 20);
        this.add(portLab);
        this.add(txtPort);

        JButton JB1 = new JButton("设置");
        JB1.setBounds(160, 160, 100, 30);
        this.add(JB1);
        ActionListener al = new ActionListener() { // 加上按键事件
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setServerName(txtServer.getText());
                main.setPort(Integer.parseInt(txtPort.getText()));
                main.setTitle();
                jf.setVisible(false);
            }
        };
        JB1.addActionListener(al);

        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
}