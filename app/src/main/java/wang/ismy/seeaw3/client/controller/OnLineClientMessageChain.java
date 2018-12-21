package wang.ismy.seeaw3.client.controller;

import com.google.gson.Gson;
import wang.ismy.seeaw3.client.BaseClient;
import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.common.Log;
import wang.ismy.seeaw3.common.MessageChain;
import wang.ismy.seeaw3.dto.Command;
import wang.ismy.seeaw3.dto.Message;
import wang.ismy.seeaw3.dto.Result;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OnLineClientMessageChain implements MessageChain {

    private BaseClient client;

    public OnLineClientMessageChain(BaseClient client) {
        this.client = client;
    }


    @Override
    public void processMessage(Message request, Message response) {

        Result result = MessageChain.convertResult(request.getContent());

        if ("onLineClient".equalsIgnoreCase(result.result("type").toString())) {

            List<String> onLineList = new Gson().fromJson(result.result("onLineClientList").toString(), List.class);
            ((MasterClient)client).onLineList(onLineList);
        }
    }

    private void f1(Map<String,String> endpoint){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String to = endpoint.get("name");
                String from = client.getCommunicationName();

                Command command = new Command();
                command.command("communication")
                        .param("to",to)
                        .param("from",from)
                        .param("content","openTerminal");
                client.sendMessageIgnoreException(command);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




            }
        }).start();
    }
}
