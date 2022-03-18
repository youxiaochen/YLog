package you.chen.decrypt;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);

        String[] pers = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, pers, 1);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt1) {
            String key = LogDecrypt.createEccKey();
            Log.i("youxiaochen", key);
        } else if (v.getId() == R.id.bt2) {
            new Thread(){
                @Override
                public void run() {
                    decrypt();
                }
            }.start();
        }
    }

    private void decrypt() {
        File dir = FileUtils.getCacheDirPath(this);
        File outDir = new File(FileUtils.getCacheDirPath(this), "outlogs");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File[] fs = dir.listFiles();
        int number = 0;
        if (fs != null && fs.length > 0) {
            for (File f : fs) {
                if (!f.isFile()) {
                    continue;
                }
                String fn = f.getName();
                if (fn.endsWith("_debug.log")) {//实际开发中最好不要将debug与release日志放一起的
                    continue;
                }
                if(fn.endsWith(".log")) {
                    LogDecrypt.decrypt(f.getAbsolutePath(), new File(outDir, "decrypt" + number + ".txt").getAbsolutePath());
                    number++;
                }
            }
        }
    }
}
