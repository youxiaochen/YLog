package you.chen.ylog;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;


/**
 * Created by you on 2016/12/2.
 */

public final class FileUtils {

    static final String FILE_DIR = "ylog";

    /**
     * 使用时一定要完整路径,文件夹有创建,并确保有权限,如/storage/emulated/0/Android/data/com.you.log/cache/ylog/
     * 10.0时要考虑文件分区时的情况, 推荐使用getExternalCacheDir的方式储存,防止用户手动删除了mmap文件
     * @param context
     * @return
     */
    public static File getCacheDirPath(Context context) {
        File directory;
        if (isSDCardExist()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                directory = new File(context.getExternalCacheDir(), FILE_DIR);
            } else {
                directory = new File(Environment.getExternalStorageDirectory(), FILE_DIR);
            }
        } else {
            directory = new File(context.getCacheDir(), FILE_DIR);
        }
        if (!directory.exists()) directory.mkdirs();
        return directory;
    }

    /**
     * SD卡是否存在
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
