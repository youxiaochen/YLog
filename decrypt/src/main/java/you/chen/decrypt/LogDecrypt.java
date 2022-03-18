package you.chen.decrypt;

public class LogDecrypt {

    static {
        System.loadLibrary("Decryptlog");
    }


    public static native void decrypt(String logPath, String decryptPath);


    public static native String createEccKey();




}
