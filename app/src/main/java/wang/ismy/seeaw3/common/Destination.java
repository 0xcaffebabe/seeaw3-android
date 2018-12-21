package wang.ismy.seeaw3.common;


import wang.ismy.seeaw3.dto.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;



public class Destination {

    private Socket socket;

    public Destination(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(Message message) throws IOException {
        Log.info("正在发送消息:"+message);

        PrintWriter printWriter =new PrintWriter(socket.getOutputStream());
        printWriter.println(message.getContent());
        printWriter.flush();
    }

    public Socket getSocket() {
        return socket;
    }
}
