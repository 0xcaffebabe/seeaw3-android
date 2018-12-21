package wang.ismy.seeaw3;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.client.MasterClientCallback;
import wang.ismy.seeaw3.dto.Command;
import wang.ismy.seeaw3.dto.Result;

public class TerminalActivity extends AppCompatActivity {

    private MasterClient masterClient = MainActivity.getGlobalMasterClient();

    private TextView terminalOutput;

    private EditText terminalInput;

    private Button terminalSend;

    private ScrollView scroll;
    private Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0x1) {
                String s = msg.getData().getString("output");
                terminalOutput.append(s);

            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        String name = getIntent().getStringExtra("name");
        setTitle("通信:" + name);

        terminalOutput = findViewById(R.id.terminalOutput);
        terminalInput = findViewById(R.id.terminalInput);
        terminalSend = findViewById(R.id.terminalSend);
        scroll = findViewById(R.id.scroll);
        terminalOutput.setMovementMethod(ScrollingMovementMethod.getInstance());

        terminalOutputTextChangedEvent();
        terminalSendEvent(name);


        resetMasterClientCallback();
        startOpenTerminalThread(name);


    }

    private void startOpenTerminalThread(String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String to = name;
                String from = masterClient.getCommunicationName();

                Command command = new Command();
                command.command("communication")
                        .param("to", to)
                        .param("from", from)
                        .param("content", "openTerminal");
                masterClient.sendMessageIgnoreException(command);
            }
        }).start();
    }

    private void resetMasterClientCallback() {
        masterClient.setMasterClientCallback(new MasterClientCallback() {

            @Override
            public void terminalOutput(String msg) {
                Message message = new Message();
                message.what = 0x1;
                Bundle bundle = new Bundle();
                bundle.putString("output", msg);
                message.setData(bundle);
                uiHandler.sendMessage(message);
            }

        });
    }

    private void terminalSendEvent(String name) {
        terminalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Command command = new Command();
                        command.command("communication")
                                .param("to", name)
                                .param("from", masterClient.getCommunicationName())
                                .param("content", new Gson().toJson(
                                        new Result()
                                                .msg("success")
                                                .result("type", "terminalInput")
                                                .result("sessionId", masterClient.getSession(0))
                                                .result("cmd", terminalInput.getText().toString())

                                ));
                        masterClient.sendMessageIgnoreException(command);
                    }
                }).start();
            }
        });
    }

    private void terminalOutputTextChangedEvent() {
        terminalOutput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                int offset = terminalOutput.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }


        });
    }
}
