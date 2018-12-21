package wang.ismy.seeaw3.client;

import com.google.gson.Gson;
import wang.ismy.seeaw3.common.*;
import wang.ismy.seeaw3.dto.Command;
import wang.ismy.seeaw3.dto.Message;
import wang.ismy.seeaw3.util.ThreadUtils;

import java.io.IOException;
import java.net.Socket;

import static wang.ismy.seeaw3.common.Constant.WAIT_TIME;

public abstract class BaseClient implements Messageable {

    private static final int PORT = 2000;
    private static final String HOST_NAME = "ismy.wang";

    private Socket socket;
    private SubListenThread listenThread;
    private LiveThread liveThread;
    private String communicationName;

    public BaseClient() {
        init();
    }



    public void sendMessage(Message message) throws IOException {
        message.sendMessage();
    }

    public void sendMessage(String msg) throws IOException {
        Message message = new Message();
        message.setDestination(new Destination(socket));
        message.setContent(msg);
        message.sendMessage();
    }

    public void sendMessage(Command command) throws IOException {
        Message message = new Message();
        message.setDestination(new Destination(socket));
        message.setContent(new Gson().toJson(command));
        message.sendMessage();
    }

    public void sendMessageIgnoreException(Message message) {
        try {
            message.sendMessage();
        } catch (IOException e) {
            Log.error("客户端发送消息失败:" + e.getMessage() + ",判断与服务器失去连接，" + WAIT_TIME + "ms后重连");
            ThreadUtils.sleep();
            init();
        }
    }

    public void sendMessageIgnoreException(String msg) {
        try {
            Message message = new Message();
            message.setDestination(new Destination(socket));
            message.setContent(msg);
            message.sendMessage();
            Log.info("客户端发送了一条消息:" + message);
        } catch (IOException e) {
            Log.error("客户端发送消息失败:" + e.getMessage() + ",判断与服务器失去连接，" + WAIT_TIME + "ms后重连");
            ThreadUtils.sleep();
            init();
        }
    }


    public void sendMessageIgnoreException(Command command) {
        try {
            Message message = new Message();
            message.setDestination(new Destination(socket));
            message.setContent(new Gson().toJson(command));
            message.sendMessage();
            Log.info("客户端发送了一条消息:" + message);
        } catch (IOException e) {
            Log.error("客户端发送消息失败:" + e.getMessage() + ",判断与服务器失去连接，" + WAIT_TIME + "ms后重连");
            ThreadUtils.sleep();
            init();
        }
    }

    public abstract void receiveMessage(Message message);

    public String getCommunicationName() {
        return communicationName;
    }

    public void setCommunicationName(String communicationName) {
        this.communicationName = communicationName;
    }

    public abstract void connectFail(String msg);


    protected void init() {
        createConnection();
        startListenThread();
        createAndStartLiveThread();
        sendInitCommunication();

    }


    private void createConnection() {

            try {
                socket = new Socket(HOST_NAME, PORT);
                Log.info("客户端创建与服务器的连接成功");

            } catch (IOException e) {
                Log.error("客户端创建与服务器的连接失败，原因:" + e.getMessage() + "," + WAIT_TIME + "ms后重试");
                connectFail(e.getMessage());
                ThreadUtils.sleep();


            }

    }

    private void startListenThread() {
        listenThread = new SubListenThread(new Destination(socket), this);
        listenThread.start();
    }

    private void createAndStartLiveThread() {
        liveThread = new LiveThread();
        liveThread.start();
    }

    private void sendInitCommunication() {
        Command command = new Command();
        command.command("communication")
                .param("content", "init");
        sendMessageIgnoreException(command);
    }

    class LiveThread extends Thread {
        public boolean flag = true;

        @Override
        public void run() {
            while (flag) {

                Command command = new Command();
                command.command("heartBeat")
                        .param("content", "")
                        .param("type", "heartBeat");
                sendMessageIgnoreException(command);
                //每两秒发送一条心跳
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
