package wang.ismy.seeaw3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import wang.ismy.seeaw3.client.MasterClient;
import wang.ismy.seeaw3.client.MasterClientCallback;
import wang.ismy.seeaw3.dto.Command;
import wang.ismy.seeaw3.dto.Result;

public class StateActivity extends AppCompatActivity {

    private MasterClient masterClient = MainActivity.getGlobalMasterClient();

    private String toName;

    private Handler uiHandler = new Handler();

    private ImageView imageView;
    private Button photoButton;
    private Button screenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        toName = getIntent().getStringExtra("name");
        imageView =new ZZoomImageView(this);
        screenButton=findViewById(R.id.screenButton);

        photoButton=findViewById(R.id.photoButton);
        ((LinearLayout)findViewById(R.id.imgViewContainer)).addView(imageView);
        setTitle(toName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Command command = new Command()
                                    .command("communication")
                                    .param("to",toName)
                                    .param("from",masterClient.getCommunicationName())
                                    .param("content",new Gson().toJson(
                                       new Result()
                                       .msg("success")
                                       .result("type","screen")
                                    ));
                masterClient.sendMessageIgnoreException(command);
            }

        }).start();

        screenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Command command = new Command()
                                .command("communication")
                                .param("to",toName)
                                .param("from",masterClient.getCommunicationName())
                                .param("content",new Gson().toJson(
                                        new Result()
                                                .msg("success")
                                                .result("type","screen")
                                ));
                        masterClient.sendMessageIgnoreException(command);
                    }

                }).start();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Command command = new Command()
                                .command("communication")
                                .param("to",toName)
                                .param("from",masterClient.getCommunicationName())
                                .param("content",new Gson().toJson(
                                        new Result()
                                                .msg("success")
                                                .result("type","photo")
                                ));
                        masterClient.sendMessageIgnoreException(command);
                    }

                }).start();
            }
        });
        masterClient.setMasterClientCallback(new MasterClientCallback(){
            @Override
            public void onScreenMsg(String screenAddress) {
                Bitmap bitmap = getData(screenAddress);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);

                    }
                });

            }

            @Override
            public void onPhotoMsg(String s) {
                Bitmap bitmap = getData(s);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);

                    }
                });
            }
        });


    }

    public Bitmap getData(String path) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bitmap;

    }
}
