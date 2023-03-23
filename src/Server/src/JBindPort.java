import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

// 用于输入端口号并绑定它的子窗口，要求用对话框显示绑定是否成功的消息。
class JBindPort extends JFrame {
    private static final long serialVersionUID = 1L;   // 本语句用于序列化，可以不要
    private JTextField txtPort;
    private JFrame jf;
    private TcpServerChat main;

    JBindPort(TcpServerChat main) {
        this.setSize(400, 300); // 设置width和height
        this.setLocationRelativeTo(null); // 居屏幕中间
        this.setLayout(null); // 空布局，可以准确的定位组件在容器中的位置和大小
        this.setTitle("选择端口");
        jf = this;
        this.main = main;

        // 端口
        txtPort = new JTextField("" + main.getPort(), 30); // 初值，列数
        JLabel portLab = new JLabel("端口:");  //
        portLab.setBounds(100, 90, 40, 20);  // left,top,width,height
        txtPort.setBounds(160, 90, 120, 20);
        this.add(portLab);
        this.add(txtPort);

        JButton JB1 = new JButton("绑定");
        JB1.setBounds(160, 160, 100, 30);
        this.add(JB1);
        ActionListener al = new ActionListener() { // 加上按键事件
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(txtPort.getText());
                try {
                    if (main.bind(port)) {
                        JOptionPane.showMessageDialog(null, "绑定成功。");
                        main.accept();
                        jf.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "绑定失败。");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                main.setTitle();
            }
        };
        JB1.addActionListener(al);

        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
}