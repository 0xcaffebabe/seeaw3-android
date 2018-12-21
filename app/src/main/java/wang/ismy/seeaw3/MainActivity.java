package wang.ismy.seeaw3;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.client.MasterClientCallback;
import wang.ismy.seeaw3.dto.Command;

public class MainActivity extends AppCompatActivity {

    private MasterClient masterClient;
    public static MasterClient globalMasterClient;

    private Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0x2) {
                List<String> onlineList = msg.getData().getStringArrayList("onLineList");
                renderClientList(onlineList);
            }

            return true;
        }
    });


    private ListView clientListView;

    private Button exitButton;

    private Button refreshButton;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exitButton = findViewById(R.id.exitButton);
        clientListView = findViewById(R.id.clientListView);
        refreshButton = findViewById(R.id.refreshButton);
        createMastClient();
        refreshButtonEvent();
        exitButtonEvent();
    }

    private void refreshButtonEvent() {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Command command = new Command();
                        command.command("onLineClient")
                                .param("token", "715711877");
                        masterClient.sendMessageIgnoreException(command);
                    }
                }).start();
            }
        });
    }

    private void createMastClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                masterClient = new MasterClient(getMasterClientCallback());
                sendInitCommunication();
                sendOnLineListCommand();
                globalMasterClient = masterClient;



            }
        }).start();
    }

    private MasterClientCallback getMasterClientCallback(){
        return new MasterClientCallback() {
            @Override
            public void connectFail(String msg) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "连接服务器失败:" + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLineList(List<String> onLineList) {
                android.os.Message msg = new android.os.Message();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("onLineList", (ArrayList<String>) onLineList);
                msg.what = 0x2;
                msg.setData(bundle);
                uiHandler.sendMessage(msg);
                android.util.Log.e("ismyismy", "在线用户:" + onLineList);
            }

        };
    }

    private void sendInitCommunication(){
        Command command = new Command();
        command.command("communication")
                .param("content", "init");
        masterClient.sendMessageIgnoreException(command);
    }

    private void sendOnLineListCommand(){
        Command command = new Command();
        command.command("onLineClient")
                .param("token", token);
        masterClient.sendMessageIgnoreException(command);
    }

    private void exitButtonEvent(){
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }

    private void renderClientList(List<String> onLineList){

        clientListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return onLineList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(MainActivity.this);
                Map<String, String> endpoint = new Gson().fromJson(onLineList.get(position), Map.class);
                textView.setWidth(500);
                String s = "名称:" + endpoint.get("name") + ",ip:" + endpoint.get("ip");
                if (endpoint.get("name").equals(masterClient.getCommunicationName())) {
                    s += "(本机)";
                }
                Button controlButton = new Button(MainActivity.this);
                controlButton.setText("操控");
                controlButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.putExtra("name", endpoint.get("name"));
                        intent.setClass(MainActivity.this, TerminalActivity.class);
                        startActivity(intent);
                    }
                });
                Button stateButton = new Button(MainActivity.this);
                controlButton.setWidth(125);
                stateButton.setText("状态");
                if (s.contains("本机")){
                    controlButton.setEnabled(false);
                    stateButton.setEnabled(false);
                }
                stateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("name", endpoint.get("name"));
                        intent.setClass(MainActivity.this, StateActivity.class);
                        startActivity(intent);
                    }
                });
                textView.setText(s);
                linearLayout.addView(textView);
                linearLayout.addView(controlButton);
                linearLayout.addView(stateButton);
                return linearLayout;
            }
        });
    }

    public static MasterClient getGlobalMasterClient() {
        return globalMasterClient;
    }

}
