import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

// 从套接字的方法，与客户端连接的套接字
class SlaveSock {
    private Socket socket;  // 套接字
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    SlaveSock(Socket socket) {
        this.socket = socket;
        try {
            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            close();
        }
    }

    boolean isConn() {
        return (socket != null);
    }

    boolean write(String msg) {
        boolean res = true;
        try {
            outputToClient.writeUTF(msg);
        } catch (Exception ex) {
            res = false;
        }
        if(!res) {
            try {
                socket.close();
            }
            catch(Exception ex) {
                // ex.printStackTrace();
            }
            finally {
                socket = null;
            }
        }
        return res;
    }

    String read() {
        String msg = "";
        try {
            msg = inputFromClient.readUTF();
        } catch (Exception ex) {
            close();
        }
        return msg;
    }

    boolean close() {
        try {
            inputFromClient.close();
            outputToClient.close();
            socket.close();
            socket = null;
            return true;
        } catch (Exception ex) {
            socket = null;
        }
        return false;
    }
}
