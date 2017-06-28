package com.chinanetcenter.wsvideotest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;

import android.annotation.TargetApi;
import android.text.format.DateFormat;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SurfaceViewTestActivity extends Activity implements IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener, OnClickListener,KeyEvent.Callback {
    private static final String TAG = "SurfaceViewTestActivity";
    /**
     *
     */
    private SurfaceView surfaceView;

    private ImageView imageView;

    /**
     * surfaceView播放控制
     */
    private SurfaceHolder surfaceHolder;

    /**
     * 播放控制条
     */
    private SeekBar seekBar;

    /**
     * 暂停播放按钮
     */
    private Button playButton;

    /**
     * 重新播放按钮
     */
    private Button replayButton;

    /**
     * 截图按钮
     */
    private Button screenShotButton;

    /**
     * switch player
     */
    private Button switchPlayerButton;

    /**
     * switch codec
     */
    private Button switchCodecButton;

    /**
     * 改变视频大小button
     */
    private Button videoSizeButton;

    /**
     * 加载进度显示条
     */
    private CircleProgressBar progressBar;

    /**
     * 播放视频
     */
    private IMediaPlayer mediaPlayer;

    /**
     * 记录当前播放的位置
     */
    private int playPosition = -1;

    /**
     * seekBar是否自动拖动
     */
    //private boolean seekBarAutoFlag = false;

    /**
     * 视频时间显示
     */
    private TextView vedioTiemTextView;

    /**
     * 播放总时间
     */
    private String videoTimeString;

    private long videoTimeLong;

    /**
     * 播放路径
     */
    private String pathString;

    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;
    private int surfaceWidth, surfaceHeight;
    private TableLayout mHudView;
    private InfoHudViewHolder mHudViewHolder;
    private ArrayList<Uri> mUris = new ArrayList<Uri>();
    private Handler mHandler;
    private Handler mWorkerHandler;
    private HandlerThread mHandlerThread;
    private int mIndex = 0;
    private static final int PLAY_VIDEO = 1;
    private static final int UPDATE_SEEKBAR = 2;
    private static final int DRAW_BLACK = 3;
    private static final int TIMEOUT_RECONNECT = 4;

    private Date mCurDate = null;
    private Date mEndDate = null;

    private String mPrevVideoSize = null;
    private boolean mFirstCreated = false;
    private  boolean mQuit = false;
    private AppPrivteData mAppData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view_test);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mHudViewHolder = new InfoHudViewHolder(this, mHudView);
        mAppData = new AppPrivteData(this);
        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        mHandlerThread = new HandlerThread("handler_thread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
        mWorkerHandler = new MyHandler();

        initUris();
        initViews();

        mFirstCreated = true;
    }

    private void initUris() {
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504320/da1e58a0ea44bb1cc7404d86238775b0.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510313/b8aedc0502399006760ad56cb58abc12.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501433/live.m3u8"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0031_H264_HEAAC_%e4%b8%8d%e8%83%bd%e6%92%ad%e6%94%be.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501445/playlist.m3u8?timestart=1481515200&duration=7200&token=20161212184123&key=3acbb0cc78c73493a4eab8b82edf471c52ca9f6a"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501445/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510313/b8aedc0502399006760ad56cb58abc12.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508242/1ee2ff07aa0fc2306a95e7b721bfdd61.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_506342/4bcaf2f04a32fff666d4e3c6fdbd4b58.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510109/1e4a0cb393e6df5e9d116965ab782e1d.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508207/e0314b9e77692dc88dd090737a009123.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504320/da1e58a0ea44bb1cc7404d86238775b0.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510111/c21e7f730a32949cd729891270843b63.rmvb"));

//        mUris.add(Uri.parse("file:///storage/emulated/legacy/Movie/1480924111.ts"));
//        mUris.add(Uri.parse("file:///storage/emulated/legacy/Movie/1480924121.ts"));
//        mUris.add(Uri.parse("file:///storage/emulated/legacy/Movie/1480924131.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501506/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501505/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501502/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501399/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501454/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501501/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501458/live.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501455/live.m3u8"));

//        mUris.add(Uri.parse("http://10.8.72.86:9123/10-WMV/WMV043_2560-720%e5%bf%8d%e8%80%85%e9%be%99%e5%89%91%e4%bc%a02_2560x720_VC1_29fps_10.2M_WMA.wmv"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/%e4%b8%bd%e6%b1%259F%e8%258A%ad%e6%af%2594KTV.wmv"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/RM047_RV40_920x1080_29.9fps_6137K_Cooker.rmvb"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501432/1480577374.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501432/live.m3u8"));
//        mUris.add(Uri.parse("http://211.156.185.112/test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501396/live.m3u8?token=20161202134644&key=fac7b129a6e854b17e0541d3ec8259dba59fac4c"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501503/live.m3u8?token=20161129164542&key=912ebada9c5893bcf4f99022bff19a317c7140e7"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501503/1480664968.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501501/live.m3u8?token=20161129162055&key=28e32ea290be78f23fb70b7be035e6a0ae91b3f2"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508111/866cb0703cbafc5f8edc828271099f67.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508053/2aca6a0f676b754c15e72f7e8339b58e.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.87:8080/3840x2178_H264_AAC.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.87:8080/1280x738_H264.AAC.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.87:8080/720x578_h264_mpeg.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/10-WMV/WMV043_2560-720%e5%bf%8d%e8%80%85%e9%be%99%e5%89%91%e4%bc%a02_2560x720_VC1_29fps_10.2M_WMA.wmv"));
//        mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/45.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/RM047_RV40_920x1080_29.9fps_6137K_Cooker.rmvb"));

//        mUris.add(Uri.parse("http://10.8.72.87:8080/4k_05.mov"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/16afab58ba8cdace0abdc2a924004c61.mov"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508151/62019b46bed8ba70ba8166d04de92a1e.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A075_%e7%88%b1%e4%b8%bd%e4%b8%9d%e6%a2%a6%e6%b8%b8%e4%bb%99%e5%a2%83%e9%a2%84%e5%91%8a%e7%89%87_MS.MPEG-4_1920X1080_25fps_11.3Mbps_MPEG.avi"));
//        mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/20.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/be960cacf6d6e81ce1b9f78de61529a6.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/c43d74131f85939c91e72c5f48906701.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/cb60cbeaeabe51ecfc47328b9098ed7d.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/d8dd4b545e954a4323e6d95d6b8145fd.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/fc220271c3a8e6aabe52dfe4d8ef06fa.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/c43d74131f85939c91e72c5f48906701.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/c43d74131f85939c91e72c5f48906701.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/c43d74131f85939c91e72c5f48906701.ts"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501445/playlist.m3u8?timestart=1479866400&duration=7200"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510111/c21e7f730a32949cd729891270843b63.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510113/cb3091cfc390098b27012825e8f46e20.rmvb"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/RM063_%e5%b0%8f%e9%a9%ac%e7%8e%8b_RV40_640x360_%2030fps_352K_Cooker.rmvb"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/RM070_%e6%9d%8e%e5%ad%9d%e5%88%a9D_RV40_800x452_%2029.9fps_1540K_AAC.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_506318/fdd23526716ad52c344304145beb7c44.mkv"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_506319/20175ab342640d10a96a420719722136.mkv"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/%e4%bb%98%e7%ac%9b%e7%94%9f%2526%e4%bb%bb%e9%9d%99-%e7%88%b1%e5%88%b0CHUN%e6%bd%ae%e6%bb%9agun%e6%9d%a5.MKV"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/be960cacf6d6e81ce1b9f78de61529a6.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/c43d74131f85939c91e72c5f48906701.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/cb60cbeaeabe51ecfc47328b9098ed7d.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/d8dd4b545e954a4323e6d95d6b8145fd.ts"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/fc220271c3a8e6aabe52dfe4d8ef06fa.ts"));
//        mUris.add(Uri.parse("http://211.156.185.112/test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510113/cb3091cfc390098b27012825e8f46e20.rmvb?token=20161111111556&key=ce6097d7c76cc43dd573f3f1197496d64c7c1a18"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A075_%e7%88%b1%e4%b8%bd%e4%b8%9d%e6%a2%a6%e6%b8%b8%e4%bb%99%e5%a2%83%e9%a2%84%e5%91%8a%e7%89%87_MS.MPEG-4_1920X1080_25fps_11.3Mbps_MPEG.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/%e8%b5%a4%e5%a3%81%e4%b8%8a-2009.x264.720P.DTS.BDRiP-CHD/%e8%b5%a4%e5%a3%81II.2009_H264_1280x544%282.35%29_24fps_4988Kbps_DTS.mkv"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514619/live.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514619/playlist.m3u8?timestart=1478596200&duration=600"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/vod/entry_514935/b601f2140c30d2bafdd2066a65eda53a.mp4"));
//        mUris.add(Uri.parse("http://211.156.185.98/CloudVideo/live/entry_501394/playlist.m3u8?timestart=1478503200&duration=600"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501450/playlist.m3u8?timestart=1477872000&duration=7200&token=20161031112100&key=c1c7d76857a349db3fed037983253dd32a662321"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501435/playlist.m3u8?timestart=1477864800&duration=7200"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501435/playlist.m3u8?timestart=1477864800&duration=7200"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/%e7%9c%9f%e5%ae%9e%e7%9a%84%e8%b0%8e%e8%a8%80BD.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508007/e79ec0379e5158912035b699d649106a.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A046_Taylor.Swift%5bOur.Song%5d_DivX4_608x456%281.33%29_25fps_2107Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://211.156.185.112/test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501433/live.m3u8"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A053_WonderGirls_Nobody%20%e8%88%9e%e8%b9%88%e6%95%99%e7%a8%8b_DIVX4_600X480_30fps_1378Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A066_%e5%86%b0%e6%b2%b3%e4%b8%96%e7%ba%aaII_DivX5_608x336%281.778%29_30fps_2064Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A078_%e8%94%a1%e5%a6%8d-%e4%b8%a4%e4%b8%aa%e4%ba%ba_DivX4_640x480%281.33%29_30fps_1823Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A050_VideoDemo_1_XVID_800X480_24fps_1261Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A058_%e3%80%8a%e5%8f%98%e5%bd%a2%e9%87%91%e5%88%9a%e3%80%8b%e9%ab%98%e6%b8%85%e6%99%b0%e6%9c%80%e6%96%b0%e9%a2%84%e5%91%8a%e7%89%87%e4%b8%8b%e8%bd%bd_AVC_720P_29.9fps_2448Kbps_AAC.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A006_Hyundai_DIVX5_1280x720_3806kbps_30fps_mp3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A008_Doritos%20crunch%20power_DivX5_720p%281.778%29_30fps_4712Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A028_HR_block_Divx5_720p%281.778%29_30fps_4797Kbps_MP3.avi"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510182/5cce245a035355eda9646f9b34c6a46e.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504364/da17fe1e9cf3b6375aacea6812680eb2.avi"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504365/38062b2de4fce0ce1906ff74e2a73c08.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504362/94e596fe2a7a52389988ba792e4fbe00.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504361/31aa1094c12720b1ee6db01b1d043139.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504360/104f02f473209285e6a8dadc99320172.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504356/a0d095a8b2f1ad781c46c16531c44bfd.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A058_%e3%80%8a%e5%8f%98%e5%bd%a2%e9%87%91%e5%88%9a%e3%80%8b%e9%ab%98%e6%b8%85%e6%99%b0%e6%9c%80%e6%96%b0%e9%a2%84%e5%91%8a%e7%89%87%e4%b8%8b%e8%bd%bd_AVC_720P_29.9fps_2448Kbps_AAC.avi"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508454/c4763c0a1535c92a60f03a6e256dd9fc.rmvb"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A046_Taylor.Swift%5bOur.Song%5d_DivX4_608x456%281.33%29_25fps_2107Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A075_%e7%88%b1%e4%b8%bd%e4%b8%9d%e6%a2%a6%e6%b8%b8%e4%bb%99%e5%a2%83%e9%a2%84%e5%91%8a%e7%89%87_MS.MPEG-4_1920X1080_25fps_11.3Mbps_MPEG.avi?token=20161025103540&key=e6b1738dcaf29af0b3f1af5140ee23a451259ea0"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A075_%e7%88%b1%e4%b8%bd%e4%b8%9d%e6%a2%a6%e6%b8%b8%e4%bb%99%e5%a2%83%e9%a2%84%e5%91%8a%e7%89%87_MS.MPEG-4_1920X1080_25fps_11.3Mbps_MPEG.avi"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504361/31aa1094c12720b1ee6db01b1d043139.mp4"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508371/34a90479f5ba1f1e9ebe57c7387aebce.mkv"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/%e5%259B%bd%e7%be%258E%e4%bc%2581%e4%b8%259A%e5%ae%a3%e4%bc%a0%e7%2589%2587.mp4"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514619/live.m3u8?token=20161009155618&key=39583c64e7a703b28e1b55f1946728a3cae04d34"));
        //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/6cf2dd5d339436b890f1f6c17c130516.ts?token=20160926134812"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0029_bshi-01_MPEG2_1080i%281.778%29_20Mbps_29.970%20fps_AAC.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0010_%e6%83%8a%e8%89%b3%e7%9e%ac%e9%97%b4%e9%9b%86%e9%94%a6_MPEG2_1080i%281.778%29_65Mbps_25fps_MPEG1.ts"));
//
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/6cf2dd5d339436b890f1f6c17c130516.ts?token=20160926134812&key=bbe040526da33ef4c1ab8b4cd2e04f1dba22075e"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/8a6e3c3df799d161e81d2ee7d50d6883.ts?token=20160926134822&key=4539a9049cfcf4a8a60370a5d8074f8fce89c7b6"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/8e57a6e2e9aaff3dd3d43ececfc3fc6d.ts?token=20160926134828&key=f9f441e6bbd42258a9482fa8418482993bbe51a2"));
//        mUris.add(Uri.parse("http://211.156.185.107:10000/ott/yunshi/9cb476c53fe8911bde3f2ce65dcda079.ts?token=20160926134836&key=6e0c27f271cf7adb3cc521668f9f07dc1f1f2074"));
//
//
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501433/playlist.m3u8?timestart=1473055200&duration=7200"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501395/playlist.m3u8?timestart=1473049980&duration=14400"));

//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A040_Shakira.feat.Wyclef.Jean.-.%5bHips.Dont.Lie%5d.LIVE.at.2010.FIFA.World.Cup.Kick-off.Celebration.Concert.%281080P%29.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A021_Colbie.Caillat%5bBubbly%5d_H264_640X480_30fps_1816Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A045_Taylor.Swift%5bOur.Song%5d_XVID_608X456_25fps_1576Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A058_%e3%80%8a%e5%8f%98%e5%bd%a2%e9%87%91%e5%88%9a%e3%80%8b%e9%ab%98%e6%b8%85%e6%99%b0%e6%9c%80%e6%96%b0%e9%a2%84%e5%91%8a%e7%89%87%e4%b8%8b%e8%bd%bd_AVC_720P_29.9fps_2448Kbps_AAC.avi"));


//        mUris.add(Uri.parse("http://10.8.72.86:9123/07-RM-RMVB/RM003_03_RV40_800x452_23.9fps_418K_Cooker.rmvb?token=20160923163925&key=10b95e4a628a449b299d7c318693e88e0b6a5909"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0031_H264_HEAAC_%e4%b8%8d%e8%83%bd%e6%92%ad%e6%94%be.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0026_BenQ.HD.Demo_MPEG2_1080ii_23.5Mbps_29.970%20fps_MPEG2.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0012_Fascination.Nature.-.Seven.Seasons.%28SONY.Demo%29_MPEG2_1080i_18.2Mbps_25fps_MPEG2.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0004_bshi-01_MPEG2_1080i%281.778%29_21.6Mbps_29.970fps_AAC.ts"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0010_%e6%83%8a%e8%89%b3%e7%9e%ac%e9%97%b4%e9%9b%86%e9%94%a6_MPEG2_1080i%281.778%29_65Mbps_25fps_MPEG1.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508151/62019b46bed8ba70ba8166d04de92a1e.mp4"));


//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A080_%e9%9f%a9%e5%9b%bd%e7%be%8e%e5%b0%91%e5%a5%b3_AVC_1920X1080P_24fps_10.2Mbps_mp3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A067_%e5%8d%93%e4%be%9d%e5%a9%b7.-.%5b%e5%88%9d%e6%81%8b%5d_XVID_696X468_29.9fps_31454Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A068_%e5%8d%93%e4%be%9d%e5%a9%b7.-.%5b%e8%8a%b3%e8%8d%89%e6%97%a0%e6%83%85%5d_MPEG-4_720x472_29fps_4172kbps_MP3.avi"));
//         mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A040_Shakira.feat.Wyclef.Jean.-.%5bHips.Dont.Lie%5d.LIVE.at.2010.FIFA.World.Cup.Kick-off.Celebration.Concert.%281080P%29.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A058_%e3%80%8a%e5%8f%98%e5%bd%a2%e9%87%91%e5%88%9a%e3%80%8b%e9%ab%98%e6%b8%85%e6%99%b0%e6%9c%80%e6%96%b0%e9%a2%84%e5%91%8a%e7%89%87%e4%b8%8b%e8%bd%bd_AVC_720P_29.9fps_2448Kbps_AAC.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A038_NU_ABO%e5%8f%8c%e8%af%ad%e7%b2%be%e7%be%8e%e7%89%b9%e6%95%881080P%e8%b6%85%e9%ab%98%e6%b8%85_AVC_1080P_24fps_8000Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://121.10.240.43:10000/ott/yunshi/%e5%259B%bd%e7%be%258E%e4%bc%2581%e4%b8%259A%e5%ae%a3%e4%bc%a0%e7%2589%2587.mp4"));
//        mUris.add(Uri.parse("http://121.10.240.43:10000/ott/yunshi/83662c6583a4f852a71d71c9dce51dd1.VOB"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508371/34a90479f5ba1f1e9ebe57c7387aebce.mkv"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504280/bc86a8dc4c91a549c7b23a08e2cab674.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504281/68696186f04acf06440d5f18afd36b90.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504282/591f6996ff3c32bed1a826baa572f3e5.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504286/0dc690c818b52e879d539c4c6873d8ee.mp4"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510111/c21e7f730a32949cd729891270843b63.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510113/cb3091cfc390098b27012825e8f46e20.rmvb"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508052/2ba2f2fb00a6edd505bbde29b64e6ad5.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_503882/b2e73b8a6ed8c5b3096dddb36579a6cd.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/MP4/01.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/MP4/mpeg4.mp4"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/MKV011_%e9%bb%84%e9%a3%9e%e9%b8%bf%e4%b9%8b%e9%93%81%e9%b8%a1%e6%96%97%e8%9c%88%e8%9a%a3B_AVC_800x528_25fps_735K__AAC.mkv"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/MKV013_2011%e6%98%a5%e8%8a%82%e7%89%88__%e5%a3%b9%e5%91%a8%e7%ab%8b%e6%b3%a2%e7%a7%80%ef%bc%88%e4%ba%8c%ef%bc%89-3e%e5%b8%9d%e5%9b%bd%e5%bd%95%e5%88%b6_AVC_720X528_25fps_801%20Kbps_AAC.mkv"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/MKV023_HDTV%20-AKB48-%e6%a1%9c%e3%81%ae%e8%8a%b1%e3%81%b3%e3%82%89%e3%81%9f%e3%81%a1_MPEG2_1440X1080_29.9fps_17.2Mbps%20_AAC.mkv"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/06-MKV/MKV025_SONY%e6%bc%94%e7%a4%ba%e7%a2%9f_%e6%b5%b7%e5%ba%95%e6%a3%ae%e6%9e%97_MPEG-4_1280x720_29fps_4169Kbps_ACC.mkv"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_506328/5f3c10ac37211dcf475a8784d8c1950a.mkv"));
//
//
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A021_Colbie.Caillat%5bBubbly%5d_H264_640X480_30fps_1816Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A045_Taylor.Swift%5bOur.Song%5d_XVID_608X456_25fps_1576Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A045_Taylor.Swift%5bOur.Song%5d_XVID_608X456_25fps_1576Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A080_%e9%9f%a9%e5%9b%bd%e7%be%8e%e5%b0%91%e5%a5%b3_AVC_1920X1080P_24fps_10.2Mbps_mp3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A046_Taylor.Swift%5bOur.Song%5d_DivX4_608x456%281.33%29_25fps_2107Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A053_WonderGirls_Nobody%20%e8%88%9e%e8%b9%88%e6%95%99%e7%a8%8b_DIVX4_600X480_30fps_1378Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A066_%e5%86%b0%e6%b2%b3%e4%b8%96%e7%ba%aaII_DivX5_608x336%281.778%29_30fps_2064Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A078_%e8%94%a1%e5%a6%8d-%e4%b8%a4%e4%b8%aa%e4%ba%ba_DivX4_640x480%281.33%29_30fps_1823Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A050_VideoDemo_1_XVID_800X480_24fps_1261Kbps_MP3.avi"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501454/playlist.m3u8?timestart=1472709600&duration=7200"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501460/playlist.m3u8?timestart=1472709600&duration=7200"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514506/live.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514506/playlist.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514507/live.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/live/entry_514507/playlist.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/vod/entry_514512/f7e0128272af5c455db92819d0bdbfbc.m3u8"));
//        mUris.add(Uri.parse("http://172.16.1.87/CloudVideo/vod/entry_514549/a108a0623aeccea4c0bb1bc8e897c5c4.mp4"));
//        mUris.add(Uri.parse("file:///storage/sdcard0/Music/marry_me.mp3"));

//        mUris.add(Uri.parse("http://121.10.240.43:10000/ott/yunshi/%e5%259B%bd%e7%be%258E%e4%bc%2581%e4%b8%259A%e5%ae%a3%e4%bc%a0%e7%2589%2587.mp4"));
//        mUris.add(Uri.parse("http://121.10.240.43:10000/ott/yunshi/83662c6583a4f852a71d71c9dce51dd1.VOB"));
//
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508371/34a90479f5ba1f1e9ebe57c7387aebce.mkv"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504280/bc86a8dc4c91a549c7b23a08e2cab674.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504281/68696186f04acf06440d5f18afd36b90.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504282/591f6996ff3c32bed1a826baa572f3e5.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504286/0dc690c818b52e879d539c4c6873d8ee.mp4"));

//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A040_Shakira.feat.Wyclef.Jean.-.%5bHips.Dont.Lie%5d.LIVE.at.2010.FIFA.World.Cup.Kick-off.Celebration.Concert.%281080P%29.avi"));
//        mUris.add(Uri.parse("http://10.8.72.86:9123/01-AVI/A058_%e3%80%8a%e5%8f%98%e5%bd%a2%e9%87%91%e5%88%9a%e3%80%8b%e9%ab%98%e6%b8%85%e6%99%b0%e6%9c%80%e6%96%b0%e9%a2%84%e5%91%8a%e7%89%87%e4%b8%8b%e8%bd%bd_AVC_720P_29.9fps_2448Kbps_AAC.avi"));
        //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508055/843d023d9c4c8b0595e7c1a10406d9a3.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508055/843d023d9c4c8b0595e7c1a10406d9a3.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504316/75f8347270473e99806fa45d92aa39ae.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510111/c21e7f730a32949cd729891270843b63.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com:10000/ott/yunshi/6cf2dd5d339436b890f1f6c17c130516.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com:10000/ott/yunshi/8a6e3c3df799d161e81d2ee7d50d6883.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com:10000/ott/yunshi/8e57a6e2e9aaff3dd3d43ececfc3fc6d.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510112/52583c7fc6f84c563d98f75284a4c0ec.rmvb"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510113/cb3091cfc390098b27012825e8f46e20.rmvb"));

//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504293/ce44b382935558c6853c7fe1c1a5f0fa.mp4"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504317/29da21afb1f4a7f0b360d5384527cf41.mkv"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504354/18d69b41863b4449baa1ef803570c26c.m2ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504355/f12ea879aaa47d60425239c2a58a357c.mp4"));
//        mUris.add(Uri.parse("http://devimages.apple.com/samplecode/adDemo/ad.m3u8"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_503552/6324e8a6d5a8658bf5590541c7a3470a.mpg"));
                //Uri uri = Uri.parse("http://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv");
        //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510108/07985c2bb75a082c578ae7c08bd18739.mp4"));
        //mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/43.ts"));
//        mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_510108/07985c2bb75a082c578ae7c08bd18739.mp4"));
//                mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/32.ts"));
//                mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/33.ts"));
//                mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/34.ts"));
//                mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/35.ts"));
                //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_506351/6c993106a4ed31df078a926e8056df9d.mp4"));
                //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508007/e79ec0379e5158912035b699d649106a.mp4"));
                //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504354/18d69b41863b4449baa1ef803570c26c.m2ts"));
                //mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_508252/0f466237dfecc9c49a27c6514cdd48b8.mp4"));
                //mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0031_H264_HEAAC_%e4%b8%8d%e8%83%bd%e6%92%ad%e6%94%be.ts"));//毛刺
                //mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/T0029_bshi-01_MPEG2_1080i%281.778%29_20Mbps_29.970%20fps_AAC.ts"));//duration
//                mUris.add(Uri.parse("http://10.8.72.86:9123/09-TS/ts/01.ts"));//not sync
//                mUris.add(Uri.parse("http://10.8.72.44:8080/demo/file/ts/21.ts"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_506303/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501474/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501471/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501469/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501450/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501442/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501454/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501394/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501433/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501459/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501471/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501456/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501458/live.m3u8"));
                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/live/entry_501452/live.m3u8"));
//                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504293/ce44b382935558c6853c7fe1c1a5f0fa.mp4"));
//                mUris.add(Uri.parse("http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504294/ef0d697b665c813ce1ab81426965894f.mp4"));
                //http://test.pant.ott.chinanetcenter.com/CloudVideo/vod/entry_504294/ef0d697b665c813ce1ab81426965894f.mp4?token=20160729071327&key=815afe6214d694569ac7524949996abcea800452
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/ce44b382935558c6853c7fe1c1a5f0fa.mp4"));
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/29da21afb1f4a7f0b360d5384527cf41.mkv"));
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/18d69b41863b4449baa1ef803570c26c.m2ts"));
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/f12ea879aaa47d60425239c2a58a357c.mp4"));
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/ad.m3u8"));
                //mUris.add(Uri.parse("file:///storage/emulated/legacy/Movies/6324e8a6d5a8658bf5590541c7a3470a.mpg"));
    }

    public void initViews() {
        String path = null;
        // 初始化控件
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        imageView = (ImageView) findViewById(R.id.imageView2);
        progressBar = (CircleProgressBar) findViewById(R.id.circle_progressbar);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        playButton = (Button) findViewById(R.id.button_play);
        replayButton = (Button) findViewById(R.id.button_replay);
        vedioTiemTextView = (TextView) findViewById(R.id.textView_showTime);
        screenShotButton = (Button) findViewById(R.id.button_screenShot);
        videoSizeButton = (Button) findViewById(R.id.button_videoSize);
        switchPlayerButton = (Button) findViewById(R.id.button_switchplayer);
        switchCodecButton = (Button) findViewById(R.id.button_switchcodec);
        // 设置surfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置surface回调
        surfaceHolder.addCallback(new SurfaceCallback());
        // 设置拖动监听事件
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
        // 设置按钮监听事件
        // 重新播放
        replayButton.setOnClickListener(SurfaceViewTestActivity.this);
        // 暂停和播放
        playButton.setOnClickListener(SurfaceViewTestActivity.this);
        // 截图按钮
        screenShotButton.setOnClickListener(SurfaceViewTestActivity.this);
        // 视频大小
        videoSizeButton.setOnClickListener(SurfaceViewTestActivity.this);
        //
        switchPlayerButton.setOnClickListener(SurfaceViewTestActivity.this);
        //
        switchCodecButton.setOnClickListener(SurfaceViewTestActivity.this);

    }

    // SurfaceView的callBack
    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged width: " + width + " height: " + height);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建
            // 设置播放资源
            Log.d(TAG, "##########surfaceCreated###########");
            if (mFirstCreated) {
                playVideo();
                //mFirstCreated = false;
            }
            surfaceWidth = surfaceView.getWidth();
            surfaceHeight = surfaceView.getHeight();
            Log.d(TAG, "surfaceCreated surfaceWidth = " + surfaceWidth + " surfaceHeight = " + surfaceHeight);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            Log.d(TAG, "##########surfaceDestroyed###########");
            release();
        }
    }

    private void release() {
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.setDisplay(null);
            mHandler.removeMessages(UPDATE_SEEKBAR);
            //mediaPlayer.reset();
            Date startDate = new Date(System.currentTimeMillis());
            mediaPlayer.release();
            Date endDate = new Date(System.currentTimeMillis());
            long diff = endDate.getTime() - startDate.getTime();
            Log.d(TAG, "[wym] reset time diff: " + diff);
            mediaPlayer = null;
        }
//        surfaceView.requestLayout();
//        surfaceView.invalidate();
    }

    private void switchCodec() {
        String playerText = switchPlayerButton.getText().toString();
        if (!playerText.equals("WsPlayer")) {
            return;
        }
        playerText = switchCodecButton.getText().toString();
        if (playerText.equals("MediaCodec")) {
            switchCodecButton.setText("AVCodec");
        } else {
            switchCodecButton.setText("MediaCodec");
        }
        String uri = mUris.get(mIndex).getPath();
//        String suffix = uri.substring(uri.lastIndexOf(".") + 1);
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            if (mediaPlayer.getDuration() > 0) {
                int playPosition = (int) mediaPlayer.getCurrentPosition();
                mAppData.setPlayingPosition(uri, playPosition);
                Log.d(TAG, "switchCodec store pos: " +  playPosition);
            }
        }
        release();
        surfaceView.setVisibility(View.VISIBLE);
        mHandler.removeMessages(UPDATE_SEEKBAR);
        //mHandler.sendEmptyMessage(DRAW_BLACK);
        mWorkerHandler.sendEmptyMessage(PLAY_VIDEO);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void switchPlayer() {
        String playerText = switchPlayerButton.getText().toString();
        if (playerText.equals("WsPlayer")) {
            switchPlayerButton.setText("MediaPlayer");
        } else {
            switchPlayerButton.setText("WsPlayer");
        }
        String uri = mUris.get(mIndex).getPath();
//        String suffix = uri.substring(uri.lastIndexOf(".") + 1);
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            if (mediaPlayer.getDuration() > 0) {
                int playPosition = (int) mediaPlayer.getCurrentPosition();
                mAppData.setPlayingPosition(uri, playPosition);
                Log.d(TAG, "switchPlayer store pos: " +  playPosition);
            }
        }
        release();
        surfaceView.setVisibility(View.VISIBLE);
        mHandler.removeMessages(UPDATE_SEEKBAR);
        //mHandler.sendEmptyMessage(DRAW_BLACK);
        mWorkerHandler.sendEmptyMessage(PLAY_VIDEO);
        progressBar.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void switchProgram(boolean black) {
//        Canvas c = null;
//        try{
//            synchronized (surfaceHolder) {
//
//                c = surfaceHolder.lockCanvas(null);
//                c.drawColor(Color.BLACK);
//                //通过它来控制帧数执行一次绘制后休息50ms
//                //Thread.sleep(50);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            surfaceHolder.unlockCanvasAndPost(c);
//        }
        release();
        if (black) {
            //surfaceView.setVisibility(View.INVISIBLE);
        }
        if (!isDestroyed()) {
            //surfaceView.setVisibility(View.VISIBLE);
            mHandler.removeMessages(UPDATE_SEEKBAR);
            //mHandler.sendEmptyMessage(DRAW_BLACK);
            mWorkerHandler.sendEmptyMessage(PLAY_VIDEO);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        //if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            //do something here
        //    return true;
        //}

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN /*|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT*/) {
//            if (null != mediaPlayer) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                }
//                mediaPlayer.setDisplay(null);
//                //mediaPlayer.reset();
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }
//
//            surfaceView.requestLayout();
//            surfaceView.invalidate();
//            //surfaceView.setVisibility(View.INVISIBLE);
//            mHandler.removeMessages(UPDATE_SEEKBAR);
//            mHandler.sendEmptyMessage(DRAW_BLACK);
//            progressBar.setVisibility(View.VISIBLE);
            mIndex++;
            mIndex = mIndex%mUris.size();
            switchProgram(true);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mIndex == 0)
                mIndex = mUris.size() - 1;
            else
                mIndex--;
            switchProgram(true);
        }

        return super.onKeyDown(keyCode, event);
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    Log.d(TAG, "onVideoSizeChanged width: " + width + " height: " + height);
                    Log.d(TAG, "onVideoSizeChanged getVideoWidth: " + mp.getVideoWidth() + " getVideoHeight: " +  mp.getVideoHeight());
                   /* width = mp.getVideoWidth();
                    height = mp.getVideoHeight();
                    //mVideoSarNum = mp.getVideoSarNum();
                    //mVideoSarDen = mp.getVideoSarDen();
                    if (width > screenWidth || height > screenHeight) {
                        // 计算出宽高的倍数
                        float vWidth = (float) width / (float) screenWidth;
                        float vHeight = (float) height / (float) screenHeight;
                        // 获取最大的倍数值，按大数值进行缩放
                        float max = Math.max(vWidth, vHeight);
                        // 计算出缩放大小,取接近的正值
                        width = (int) Math.ceil((float) width / max);
                        height = (int) Math.ceil((float) height / max);
                    }
                    // 设置SurfaceView的大小并居中显示
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                            height);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    surfaceView.setLayoutParams(layoutParams);*/
                    // 获取视频的宽度和高度
//                    int width = mediaPlayer.getVideoWidth();
//                    int height = mediaPlayer.getVideoHeight();
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    //if (mOnInfoListener != null) {
                    //    mOnInfoListener.onInfo(mp, arg1, arg2);
                    //}
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            progressBar.setVisibility(View.GONE);
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            //mVideoRotationDegree = arg2;
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            //if (mRenderView != null)
                            //    mRenderView.setVideoRotation(arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                    }
                    return true;
                }
            };

    /**
     * 播放视频
     */
    public void playVideo() {
        // 初始化MediaPlayer
        mCurDate = new Date(System.currentTimeMillis());
        //IMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        String playerText = switchPlayerButton.getText().toString();
        if (playerText.equals("WsPlayer")) {
            mediaPlayer = new IjkMediaPlayer();
        } else {
            mediaPlayer = new AndroidMediaPlayer();
        }
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        Log.d(TAG, "######### playVideo #########");
        //mediaPlayer.reset();

        if (mediaPlayer instanceof IjkMediaPlayer) {
            initializeIjkMediaPlayer((IjkMediaPlayer)mediaPlayer);
        }

        //mediaPlayer = ijkMediaPlayer;
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
        // 设置Seek complete监听
        mediaPlayer.setOnSeekCompleteListener(this);

        mediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
        mediaPlayer.setOnInfoListener(mInfoListener);

        try {
            // mediaPlayer.reset();
            //mediaPlayer.setDataSource(pathString);
            Log.d(TAG, "before mIndex = " + mIndex + " mUris.size" + mUris.size());
            mIndex = mIndex%mUris.size();
            mediaPlayer.setDataSource(this, mUris.get(mIndex));
            Log.d(TAG, "after mIndex = " + mIndex + " mUris.size" + mUris.size());
            // 判断外部设备SD卡是否存在
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                // 存在获取外部文件路径
//                pathString = Environment.getExternalStorageDirectory().getPath();
//            } else {
//                // 不存在获取内部存储
//                pathString = Environment.getDataDirectory().getPath();
//            }
//            Log.d(TAG, "pathString = " + pathString);
//            mediaPlayer.setDataSource(pathString + "/Movies/18d69b41863b4449baa1ef803570c26c.m2ts");
//            mediaPlayer.setDataSource(pathString + "/Movies/6324e8a6d5a8658bf5590541c7a3470a.mpg");
            // mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
            // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
            mediaPlayer.prepareAsync();
            if (mHudViewHolder != null && mediaPlayer instanceof IjkMediaPlayer)
                mHudViewHolder.setMediaPlayer(mediaPlayer);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
        }
    }
    
    private void initializeIjkMediaPlayer(IjkMediaPlayer ijkMediaPlayer) {
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        String playerText = switchCodecButton.getText().toString();
        if (playerText.equals("MediaCodec")) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);

            //} else {
            //	mediaPlayer.setOption(IMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
            //}
        //} else {
        //	mediaPlayer.setOption(IMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        //}
        //if (mSettings.getUsingOpenSLES()) {
        //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        //} else {
        //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        //}
        //    <item>fcc-rv16</item>
        //    <item>fcc-rv24</item>
        //    <item>fcc-rv32</item>
        //    <item>fcc-yv12</item>
        //    <item>fcc-_es2</item>
        //String pixelFormat = mSettings.getPixelFormat();
        //if (TextUtils.isEmpty(pixelFormat)) {
        //	mediaPlayer.setOption(IMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IMediaPlayer.SDL_FCC_RV32);
        //} else {
        //	mediaPlayer.setOption(IMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
        //}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "overlay-format fcc-_es2");
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format","fcc-_es2");
        } else {
            Log.d(TAG, "overlay-format IjkMediaPlayer.SDL_FCC_RV32");
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format",
                    IjkMediaPlayer.SDL_FCC_RV32);
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", -1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        //ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "threads", 8);

    }

    /**
     * 视频加载完毕监听
     *
     * @param mp
     */
    @Override
    public void onPrepared(IMediaPlayer mp) {
        Log.d(TAG, "######### onPrepared #########");
        // 当视频加载完毕以后，隐藏加载进度条
        progressBar.setVisibility(View.GONE);
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        String uri = mUris.get(mIndex).getPath();
        int playPosition = mAppData.getPlayingPosition(uri);
        if (playPosition > 0 && mediaPlayer.getDuration() > 0) {
            Log.d(TAG, "onPrepared seek pos: " + playPosition);
            mediaPlayer.seekTo(playPosition);
            // surfaceHolder.unlockCanvasAndPost(Constants.getCanvas());
        }
        //seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax((int)mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = getShowTime(videoTimeLong);
        vedioTiemTextView.setText("00:00:00/" + videoTimeString);
        Log.d(TAG, "videoTimeLong = " + videoTimeLong + " videoTimeString = " + videoTimeString);

        // 设置显示到屏幕
        Log.d(TAG, "onPrepared - setDisplay");
        //surfaceView.setVisibility(View.VISIBLE);

        mediaPlayer.setDisplay(surfaceHolder);
        if (mFirstCreated) {
            changeVideoSize();
            mFirstCreated = false;
        }
//        if (mediaPlayer instanceof IjkMediaPlayer) {
//            ((IjkMediaPlayer)mediaPlayer).selectTrack(1);
//        }
        // 播放视频
        mediaPlayer.start();

        mEndDate = new Date(System.currentTimeMillis());
        long diff = mEndDate.getTime() - mCurDate.getTime();
        int index = mIndex%mUris.size();

        Log.d(TAG, "[wym] onPrepared: diff = " + diff + " uri= " + mUris.get(index));

        // 开启线程 刷新进度条
        //new Thread(runnable).start();
        mHandler.removeMessages(UPDATE_SEEKBAR);
        mHandler.sendEmptyMessage(UPDATE_SEEKBAR);
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

    }

//    private void drawBlack() {
//        Canvas canvas = null;
//        try {
//            canvas = surfaceHolder.lockCanvas();
//            canvas.drawColor(Color.BLACK);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(surfaceHolder!= null)
//            {
//                surfaceHolder.unlockCanvasAndPost(canvas);;//结束锁定画图，并提交改变。
//            }
//        }
//        Log.d(TAG, "drawBlack - sendEmptyMessageDelayed");
//        //surfaceView.setVisibility(View.INVISIBLE);
//        mWorkerHandler.sendEmptyMessage(PLAY_VIDEO);
//    }

    private boolean reconnectVideo() {
        String uri = mUris.get(mIndex).getPath();
//        String suffix = uri.substring(uri.lastIndexOf(".") + 1);
//        Log.d(TAG, "reconnectVideo uri = " + uri + " suffix: " + suffix);
//        boolean t = suffix.equals("m3u8");
//        Log.d(TAG, "reconnectVideo t " + t + " duration: " + mediaPlayer.getDuration());
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            if (mediaPlayer.getDuration() > 0) {
                int playPosition = (int) mediaPlayer.getCurrentPosition();
                mAppData.setPlayingPosition(uri, playPosition);
                Log.d(TAG, "reconnectVideo store pos: " +  playPosition);
            }
        }
        mWorkerHandler.sendEmptyMessageDelayed(TIMEOUT_RECONNECT, 5000);
        return true;
    }

    /**
     * 滑动条变化线程
     */
    //private Runnable runnable = new Runnable() {

        private void updateSeekBar() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                //while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */

                    if (null != SurfaceViewTestActivity.this.mediaPlayer
                            && SurfaceViewTestActivity.this.mediaPlayer.isPlaying()) {
                        int position = (int)mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(position);
                        Log.d(TAG, "updateSeekBar - position = " + position);
                    }
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.removeMessages(UPDATE_SEEKBAR);
            mHandler.sendEmptyMessageDelayed(UPDATE_SEEKBAR, 1000);
        }
    //};

    /**
     * seekBar拖动监听类
     * 
     * @author shenxiaolei
     */
    @SuppressWarnings("unused")
    private class SeekBarChangeListener implements OnSeekBarChangeListener {
        int mProgress;

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    //mediaPlayer.seekTo(progress);
                    if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > progress) {
                      //  progress = (int) mediaPlayer.getCurrentPosition() - 10000;
                    } else {
                     //   progress = (int) mediaPlayer.getCurrentPosition() + 5000;
                    }
                    if (mediaPlayer != null ) {
                        mediaPlayer.seekTo(progress);
                        Log.e(TAG, "seekTo " + progress);
                    }
                }
                // 设置当前播放时间
                vedioTiemTextView.setText(getShowTime(progress) + "/" + videoTimeString);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

            Log.e(TAG, "onStartTrackingTouch");
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onStopTrackingTouch");
        }
    }

    /**
     * 按钮点击事件监听
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // 重新播放
        if (v == replayButton) {
            // mediaPlayer不空，则直接跳转
            if (null != mediaPlayer) {
                // MediaPlayer和进度条都跳转到开始位置
                if (mediaPlayer.getDuration() > 0 ) {
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                }
                // 如果不处于播放状态，则开始播放
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } else {
                // 为空则重新设置mediaPlayer
                playVideo();
            }

        }
        // 播放、暂停按钮
        if (v == playButton) {
            if (null != mediaPlayer) {
                // 正在播放
                if (mediaPlayer.isPlaying()) {
                    //Constants.playPosition = (int)mediaPlayer.getCurrentPosition();
                    // seekBarAutoFlag = false;
                    mediaPlayer.pause();
                    playButton.setText("播放");
                } else {
                    //if (Constants.playPosition >= 0) {
                        // seekBarAutoFlag = true;
                        //mediaPlayer.seekTo(Constants.playPosition);
                        mediaPlayer.start();
                        playButton.setText("暂停");
                        //Constants.playPosition = -1;
                    //}
                }

            }
        }
        // 视频截图
        if (v == screenShotButton) {
            if (null != mediaPlayer) {
                // 视频正在播放，
                if (mediaPlayer.isPlaying()) {
                    // 获取播放位置
                    Constants.playPosition = (int)mediaPlayer.getCurrentPosition();
                    // 暂停播放
                    mediaPlayer.pause();
                    //
                    playButton.setText("播放");
                }
                // 视频截图
                savaScreenShot(Constants.playPosition);
                Constants.playPosition = -1;
                mediaPlayer.start();
            } else {
                Toast.makeText(SurfaceViewTestActivity.this, "视频暂未播放！", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == videoSizeButton) {
            // 调用改变大小的方法
            changeVideoSize();
        }

        if (v == switchPlayerButton) {
                switchPlayer();
        }

        if (v == switchCodecButton) {
            switchCodec();
        }
    }

    /**
     * 播放完毕监听
     *
     * @param mp
     */
    @Override
    public void onCompletion(IMediaPlayer mp) {
        // 设置seeKbar跳转到最后位置
        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
        // 设置播放标记为false
        //seekBarAutoFlag = false;
        Log.d(TAG, "---onCompletion----");
        mHandler.sendEmptyMessage(UPDATE_SEEKBAR);
        String uri = mUris.get(mIndex).getPath();
        mAppData.setPlayingPosition(uri, -1);
        mIndex++;
        switchProgram(true);
    }

    /**
     * seek complete
     *
     * @param mp
     */
    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        Log.e(TAG, "onSeekComplete-->" + mediaPlayer.getCurrentPosition());
    }

    /**
     * 视频缓存大小监听,当视频播放以后 在started状态会调用
     */
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        // TODO Auto-generated method stub
        // percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
        //Log.e(TAG, "onBufferingUpdate-->" + percent);
        // 可以根据大小的变化来
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setProgress(percent);
        }
    }

    /**
     * 错误监听
     * 
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN (1, " +  extra + ")", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED (100, " + extra + ")", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                switch(extra) {
                    case -111: //Connection refused
                    case -1094995529: //Invalid data found when processing input
                        Toast.makeText(this, "Timeout, Media Server error (-110, " + extra + ")" , Toast.LENGTH_SHORT).show();
                        break;
                    case -110: //Connection timed out
                    case -101: //Network is unreachable
                    case -5: //I/O error
                    case -113: //No route to host
                        Toast.makeText(this, "Timeout, Network Disconnection (-110, " + extra + ") ", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, "Timeout, unknown error (-110, " + extra + ")" , Toast.LENGTH_SHORT).show();
                }
                return reconnectVideo();
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA_ERROR_IO (-1004, " + extra + ")", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA_ERROR_MALFORMED (-1007, " + extra + ")", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK (200, " + extra + ")",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED (-1010, " + extra + ")", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

//        switch (extra) {
//            case MediaPlayer.MEDIA_ERROR_IO:
//                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_MALFORMED:
//                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
//                        Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
//                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
//                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
//                break;
//        }
        return false;
    }

    /**
     * 从暂停中恢复
     */
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 判断播放位置
        String uri = mUris.get(mIndex).getPath();
        int playPosition = mAppData.getPlayingPosition(uri);
        if (null != mediaPlayer) {
            //seekBarAutoFlag = true;
            mHandler.sendEmptyMessage(UPDATE_SEEKBAR);
            if (playPosition > 0 && mediaPlayer.getDuration() > 0) {
                mediaPlayer.seekTo(playPosition);
            }
            mAppData.setPlayingPosition(uri,  -1);
            mediaPlayer.start();
        } else if (!mFirstCreated) {
            playVideo();
        }
    }

    /**
     * 页面处于暂停状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                if (mediaPlayer.getDuration() > 0) {
                    int playPosition = (int) mediaPlayer.getCurrentPosition();
                    String uri = mUris.get(mIndex).getPath();
                    mAppData.setPlayingPosition(uri, playPosition);
                }
                mediaPlayer.pause();
                //seekBarAutoFlag = false;
                mHandler.removeMessages(UPDATE_SEEKBAR);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 发生屏幕旋转时调用
     */
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (null != mediaPlayer) {
            // 保存播放位置
            if (mediaPlayer.getDuration() > 0) {
                int playPosition = (int) mediaPlayer.getCurrentPosition();
                String uri = mUris.get(mIndex).getPath();
                mAppData.setPlayingPosition(uri, playPosition);
            }
        }
    }

    /**
     * 屏幕旋转完成时调用
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        if (null != mediaPlayer) {
            String uri = mUris.get(mIndex).getPath();
            int playPosition = mAppData.getPlayingPosition(uri);
            if (playPosition > 0 && mediaPlayer.getDuration() > 0) {
                mediaPlayer.seekTo(playPosition);
                mAppData.setPlayingPosition(uri, playPosition);
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }
    /**
     * 屏幕销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "-----onDestroy -1-----");
        // 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
        try {
            release();
            mHandlerThread.quit();
            mQuit = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 转换播放时间
     * 
     * @param milliseconds 传入毫秒值
     * @return 返回 hh:mm:ss或mm:ss格式的数据
     */
    @SuppressLint("SimpleDateFormat")
    public String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");

        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 保存视频截图.该方法只能支持本地视频文件
     * 
     * @param time视频当前位置
     */
    public void savaScreenShot(long time) {
        // 标记是否保存成功
        boolean isSave = false;
        // 获取文件路径
        String path = null;
        // 文件名称
        String fileName = null;
        if (time >= 0) {
            try {
                //Uri uri = Uri
                //        .parse("http://183.61.13.14/vmind.qqvideo.tc.qq.com/x0200hkt1cg.p202.1.mp4");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(this, mUris.get(mIndex));
                // 获取视频的播放总时长单位为毫秒
                String timeString = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                // 转换格式为微秒
                long timelong = Long.parseLong(timeString) * 1000;
                // 计算当前视频截取的位置
                long index = (time * timelong) / mediaPlayer.getDuration();
                // 获取当前视频指定位置的截图,时间参数的单位是微秒,做了*1000处理
                // 第二个参数为指定位置，意思接近的位置截图
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(time * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                // 释放资源
                mediaMetadataRetriever.release();
                // 判断外部设备SD卡是否存在
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 存在获取外部文件路径
                    path = Environment.getExternalStorageDirectory().getPath();
                } else {
                    // 不存在获取内部存储
                    path = Environment.getDataDirectory().getPath();
                }
                // 设置文件名称 ，以事件毫秒为名称
                fileName = Calendar.getInstance().getTimeInMillis() + ".jpg";
                // 设置保存文件
                File file = new File(path + "/shen/" + fileName);

                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
                isSave = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 保存成功以后，展示图片
            if (isSave) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                imageView.setImageBitmap(BitmapFactory.decodeFile(path + "/shen/" + fileName));
                new AlertDialog.Builder(this).setView(imageView).show();
            }
        }
    }

    /**
     * 改变视频的显示大小，全屏，窗口，内容
     */
    public void changeVideoSize() {
        // 改变视频大小
        String videoSizeString = videoSizeButton.getText().toString();
        // 获取视频的宽度和高度
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        // 如果按钮文字为窗口则设置为窗口模式
        if ("窗口".equals(videoSizeString)) {

            /*
             * 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
             * 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
             */
            if (width > screenWidth || height > screenHeight) {
                // 计算出宽高的倍数
                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;
                // 获取最大的倍数值，按大数值进行缩放
                float max = Math.max(vWidth, vHeight);
                // 计算出缩放大小,取接近的正值
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(surfaceWidth,
                    surfaceHeight);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            imageView.setVisibility(View.VISIBLE);
            surfaceView.setLayoutParams(layoutParams);
            if (surfaceView instanceof SurfaceRenderView)
                ((SurfaceRenderView)surfaceView).setMeasureDen(0, 0);

            videoSizeButton.setText("全屏");

        } else if ("全屏".equals(videoSizeString)) {
            // 设置全屏
            // 设置SurfaceView的大小并居中显示
            if (surfaceView instanceof SurfaceRenderView)
                ((SurfaceRenderView)surfaceView).setMeasureDen(screenWidth, screenHeight);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                    screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("16:9");
        } else if ("16:9".equals(videoSizeString)) {
            if (surfaceView instanceof SurfaceRenderView)
                ((SurfaceRenderView)surfaceView).setMeasureDen(0, 0);
            imageView.setVisibility(View.INVISIBLE);
            width = screenWidth;//Math.min(width, screenWidth);
            height = width * 9 / 16;
            if (height > screenHeight) {
                height = screenHeight;
                width = height * 16 / 9;
            }
            //height = Math.min(height, screenHeight);
            Log.d(TAG, "changeVideoSize screenWidth = " + screenWidth + " screenHeight= " + screenHeight);
            Log.d(TAG, "changeVideoSize width = " + width + " height= " + height);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("4:3");
        } else if ("4:3".equals(videoSizeString)) {
            width = screenWidth;//Math.min(width, screenWidth);
            height = width * 3 / 4;
            if (height > screenHeight) {
                height = screenHeight;
                width = height * 4 / 3;
            }
            //height = Math.min(height, screenHeight);
            Log.d(TAG, "changeVideoSize screenWidth = " + screenWidth + " screenHeight= " + screenHeight);
            Log.d(TAG, "changeVideoSize width = " + width + " height= " + height);
            imageView.setVisibility(View.INVISIBLE);
            if (surfaceView instanceof SurfaceRenderView)
                ((SurfaceRenderView)surfaceView).setMeasureDen(0, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            videoSizeButton.setText("窗口");
        }
        mPrevVideoSize = videoSizeButton.getText().toString();
    }

    class MyHandler extends Handler{
        public MyHandler(){
        }
        public MyHandler(Looper looper){
            super(looper);
        }
         @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_VIDEO:
                    Log.d(TAG, "######## PLAY_VIDEO #######");
                    playVideo();
                    break;
                case UPDATE_SEEKBAR:
                    //Log.d(TAG, "######## UPDATE_SEEKBAR #######");
                    updateSeekBar();
                    break;
//                case DRAW_BLACK:
//                    drawBlack();
//                    break;
                case TIMEOUT_RECONNECT:
                    switchProgram(false);
                    break;
                default:
                    ;
            }
            super.handleMessage(msg);
        }
    };
}
