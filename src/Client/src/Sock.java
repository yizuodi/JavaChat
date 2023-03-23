import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class Sock {
    private String server = "localhost";
    private int port = 8000;
    private Socket socket;

    private DataInputStream fromServer;
    private DataOutputStream toServer;

    void setServerName(String server) {
        this.server = server;
    }

    void setPort(int port) {
        this.port = port;
    }

    String getServerName() {
        return this.server;
    }

    int getPort() {
        return this.port;
    }

    boolean isConn() {
        return (socket != null);
    }


    boolean conn() {
        try {
            socket = new Socket(server, port); // 与服务器 建立连接。
            if (socket != null) {
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return (socket != null);
    }

    boolean write(String msg) {
        boolean res = true;
        try {
            toServer.writeUTF(msg);
        } catch (Exception ex) {
            res = false;
        }
        if (!res) {
            try {
                socket.close();
            } catch (Exception ex) {
                // ex.printStackTrace();
            } finally {
                socket = null;
            }
        }
        return res;
    }

    String read() {
        String msg = "";
        try {
            msg = fromServer.readUTF();
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return msg;
    }

    boolean close() {
        try {
            socket.close();
            socket = null;
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
