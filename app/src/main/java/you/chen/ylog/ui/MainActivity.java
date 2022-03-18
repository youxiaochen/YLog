package you.chen.ylog.ui;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import you.chen.ylog.R;
import you.chen.ylog.log.LogUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.bt5).setOnClickListener(this);
        findViewById(R.id.bt6).setOnClickListener(this);

        String[] pers = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, pers, 1);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:

                break;
            case R.id.bt2:
                test();
                break;
            case R.id.bt3:
                LogUtils.flush();
                break;
            case R.id.bt4:
                LogUtils.release();
                break;
            case R.id.bt5:
                String logPath = new File(FileUtils.getCacheDirPath(this), "testLog.log").getAbsolutePath();
                String outPath = new File(FileUtils.getCacheDirPath(this), "outLog.log").getAbsolutePath();
                break;
            case R.id.bt6:
                LogUtils.i("youxiaochen", "haha");
                break;
        }
    }

    private void test() {
        final String[] as = {"िन्दी或हिंदिन्दी或हिंदिन्दी或हिंद this is test", "한국어ी한국어ी 한국어ी 한국어ी  this is test",
                "这是一个测试的数据用来测试日志性能 this is test", "this is test log datas so this is test"};
        for (int i = 0; i < 4; i++) {
            final String title = as[i];
            final int th = i;
            new Thread(){
                @Override
                public void run() {

                    for (int j = 0; j < 1000; j++) {
                        String log = title + j * 1000;

                        LogUtils.i("youxiaochen", log);

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("youxiaochen", "thread " + th + " over ");
                }
            }.start();
        }
    }
}
