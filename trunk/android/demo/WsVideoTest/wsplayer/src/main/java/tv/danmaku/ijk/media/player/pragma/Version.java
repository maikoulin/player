package tv.danmaku.ijk.media.player.pragma;

import android.util.Log;

/**
 * Created by wuym on 2016/12/6.
 */
public class Version {
    private static final String VersionName = "WsPlayer tag: r30755";
    private static final String VersionCode = "V1.5.0";

    public static String getVersionName() {
        return VersionName;
    }

    public static String getVersionCode() {
        return VersionCode;
    }

    public static void dumpVersion() {
        Log.i("IJKMEDIA", "WsPlayer current version: " + VersionName + " " + VersionCode);
    }
}
