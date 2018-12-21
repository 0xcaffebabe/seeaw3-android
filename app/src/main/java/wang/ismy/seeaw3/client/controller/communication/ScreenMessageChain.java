package wang.ismy.seeaw3.client.controller.communication;

import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.common.MessageChain;
import wang.ismy.seeaw3.dto.Message;
import wang.ismy.seeaw3.dto.Result;

public class ScreenMessageChain implements MessageChain {

    private MasterClient masterClient;

    public ScreenMessageChain(MasterClient masterClient) {

        this.masterClient = masterClient;
    }


    @Override
    public void processMessage(Message request, Message response) {
        Result result = MessageChain.convertResult(request.getContent());

        if (isCommunicationMessage(result)){

            Result subResult = MessageChain.convertResult(result.result("content").toString());

                if ("screen".equals(subResult.result("type").toString())){

                }


        }
    }

    private boolean isCommunicationMessage(Result result) {
        try {
            return "communication".equalsIgnoreCase(result.getResult().get("type").toString());
        } catch (Exception e) {
            return false;
        }

    }
}
