package wang.ismy.seeaw3.client.controller;

import wang.ismy.seeaw3.client.BaseClient;
import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.common.Log;
import wang.ismy.seeaw3.common.MessageChain;

import wang.ismy.seeaw3.dto.Message;
import wang.ismy.seeaw3.dto.Result;


public class CommunicationMessageChain implements MessageChain {


    private MasterClient client;

    public CommunicationMessageChain(MasterClient client) {
        this.client = client;
    }

    @Override
    public void processMessage(Message request, Message response) {

        Result result = MessageChain.convertResult(request.getContent());

        if (isSuccessMessage(result)) {
            if (isCommunicationMessage(result)) {
                Result subResult = MessageChain.convertResult(result.getResult().get("content").toString());
                // 以下是主控端所要做的
                if (isTerminalCreatedCommunication(subResult)) {
                    ((MasterClient) client).addSession(subResult.getResult().get("content").toString());
                }
                if (isTerminalOutputCommunication(subResult)) {
                    String s = subResult.getResult().get("content").toString();
                    ((MasterClient) client).terminalOutput(s);
                }

                if ("screen".equalsIgnoreCase(subResult.getResult().get("type").toString())) {

                    client.getMasterClientCallback().onScreenMsg(subResult.result("url").toString());
                }

                if ("photo".equalsIgnoreCase(subResult.getResult().get("type").toString())) {

                    client.getMasterClientCallback().onScreenMsg(subResult.result("url").toString());
                }
            }
        } else {
            Log.error("收到一条失败信息:" + request);
        }
    }

    private boolean isTerminalOutputCommunication(Result subResult) {
        return "terminalOutput".equalsIgnoreCase(subResult.getResult().get("type").toString());
    }

    private boolean isTerminalCreatedCommunication(Result subResult) {
        return "terminalCreated".equalsIgnoreCase(subResult.getResult().get("type").toString());
    }


    private boolean isCommunicationMessage(Result result) {
        try {
            return "communication".equalsIgnoreCase(result.getResult().get("type").toString());
        } catch (Exception e) {
            return false;
        }

    }

    private boolean isSuccessMessage(Result result) {
        return "success".equalsIgnoreCase(result.getMsg());
    }

}
