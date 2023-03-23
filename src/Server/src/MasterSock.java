import java.net.ServerSocket;
import java.net.Socket;

// 主套接字的方法
class MasterSock {
    private ServerSocket serverSocket;  // 服务器套接字
    private int port = 8000;  // 端口号

    MasterSock() {
    }

    // 获取端口号
    int getPort() {
        return port;
    }

    // 绑定端口号
    boolean bind(int port) {
        try {
            port = port;
            serverSocket = new ServerSocket(port);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // 解绑端口号
    boolean unbind() {
        try {
            serverSocket.close();
            serverSocket = null;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // 接受客户端的连接
    Socket accept() {
        try {
            return serverSocket.accept();
        } catch (Exception ex) {
            return null;
        }
    }

    // 判断是否绑定端口
    boolean isBound() {
        return serverSocket != null;
    }
}
