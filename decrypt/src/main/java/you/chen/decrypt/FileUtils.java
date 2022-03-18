package you.chen.decrypt;

import android.content.Context;
import android.os.Environment;

import java.io.File;


/**
 * Created by you on 2016/12/2.
 */

public final class FileUtils {

    static final String FILE_DIR = "ylog";

    public static File getCacheDirPath(Context context) {
        if (isSDCardExist()) {
            String path = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR + File.separator;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
            return directory;
        } else {
            File directory = new File(context.getCacheDir(), FILE_DIR);
            if (!directory.exists()) directory.mkdirs();
            return directory;
        }
    }

    /**
     * SD卡是否存在
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
