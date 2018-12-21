package wang.ismy.seeaw3.client;

import android.os.Handler;

import wang.ismy.seeaw3.client.controller.CommunicationMessageChain;
import wang.ismy.seeaw3.client.controller.OnLineClientMessageChain;
import wang.ismy.seeaw3.client.controller.communication.CommunicationNameMessageChain;
import wang.ismy.seeaw3.common.Destination;
import wang.ismy.seeaw3.common.Log;
import wang.ismy.seeaw3.common.MessageChainProcessor;

import wang.ismy.seeaw3.dto.Message;

import java.util.Arrays;
import java.util.List;

//  控制端
public class MasterClient extends BaseClient {


    private MessageChainProcessor processor =
            new MessageChainProcessor(
                    Arrays.asList(new CommunicationMessageChain(this),
                            new CommunicationNameMessageChain(this),
                            new OnLineClientMessageChain(this)
                    )
            );

    private Handler uiHandler;
    private String terminalSession;
    private MasterClientCallback masterClientCallback;

    public MasterClientCallback getMasterClientCallback() {
        return masterClientCallback;
    }

    {
        Log.info("主控端准备就绪");
    }

    public MasterClient(MasterClientCallback masterClientCallback) {
        this.masterClientCallback = masterClientCallback;
        init();
    }

    @Override
    public void connectFail(String msg) {
        masterClientCallback.connectFail(msg);
    }



    @Override
    public void receiveMessage(Message message) {
        processor.processMessage(message);
        Log.info("主控客户端收到消息:" + message);
    }

    @Override
    public void destroyDestination(Destination destination) {
        Log.error("主控与服务器失去了连接，准备重连");
        this.init();
    }

    public void addSession(String session) {
        terminalSession = session;
    }

    public String getSession(int index) {
        return terminalSession;
    }


    public void terminalCreated(String sessionId) {
        android.util.Log.e("ismyismy", "终端创建，:" + sessionId);
    }


    public void terminalOutput(String s) {
        masterClientCallback.terminalOutput(s);

    }

    public void onLineList(List<String> onLineList) {
        masterClientCallback.onLineList(onLineList);

    }

    public void setMasterClientCallback(MasterClientCallback masterClientCallback){
        this.masterClientCallback = masterClientCallback;
    }



}
