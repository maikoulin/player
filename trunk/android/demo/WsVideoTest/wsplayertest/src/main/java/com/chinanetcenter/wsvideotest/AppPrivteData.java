package com.chinanetcenter.wsvideotest;

import android.content.Context;

/**
 * Created by wuym on 2016/11/21.
 */
public class AppPrivteData {
    private String KEY_PLAYING_POSITION = "key_playing_position";

    public AppPrivteData(Context context) {
        SharedPreferencesHelper.init(context);
    }

    public int getPlayingPosition(String uri) {
        return (int)SharedPreferencesHelper.getInstance().getData(uri, -1);
    }

    public void setPlayingPosition(String uri, int position) {
        SharedPreferencesHelper.getInstance().putData(uri, position);
    }
}
