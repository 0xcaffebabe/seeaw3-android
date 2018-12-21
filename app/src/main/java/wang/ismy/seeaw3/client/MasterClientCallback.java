package wang.ismy.seeaw3.client;

import java.util.List;

// 当masterClient收到各种类型的消息时，会根据消息类型回调这个接口相应的方法
public class MasterClientCallback {

    /*以下都为默认实现*/

    // 创建socket连接失败时调用
    public void connectFail(String msg){
        System.out.println("连接失败:"+msg);
    }

    // 接收到终端输出消息时回调
    public void terminalOutput(String msg){
        System.out.print("接收到输出:"+msg);
    }

    // 接收到在线用户列表消息时回调
    public void onLineList(List<String> onLineList){
        System.err.println("收到列表消息:"+onLineList);
    }

    public void onScreenMsg(String screenAddress){
        System.err.println("屏幕图片："+screenAddress);
    }

    public void onPhotoMsg(String s){

    }

}
