package com.chinanetcenter.wsvideotest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.TableLayout;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

public class InfoHudViewHolder {
    private TableLayoutBinder mTableLayoutBinder;
    private SparseArray<View> mRowMap = new SparseArray<View>();
    private IMediaPlayer mMediaPlayer;
//    private MediaInfo mi;

    public InfoHudViewHolder(Context context, TableLayout tableLayout) {
        mTableLayoutBinder = new TableLayoutBinder(context, tableLayout);
    }

    private void appendSection(int nameId) {
        mTableLayoutBinder.appendSection(nameId);
    }

    private void appendRow(int nameId) {
        View rowView = mTableLayoutBinder.appendRow2(nameId, null);
        mRowMap.put(nameId, rowView);
    }

    private void setRowValue(int id, String value) {
        View rowView = mRowMap.get(id);
        if (rowView == null) {
            rowView = mTableLayoutBinder.appendRow2(id, value);
            mRowMap.put(id, rowView);
        } else {
            mTableLayoutBinder.setValueText(rowView, value);
        }
    }

    public void setMediaPlayer(IMediaPlayer mp) {
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
//            if (mMediaPlayer instanceof IjkMediaPlayer) {
//                mi = ((IjkMediaPlayer)mMediaPlayer).getMediaInfo();
//            }
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
        } else {
            mHandler.removeMessages(MSG_UPDATE_HUD);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >=  1000) {
            return String.format(Locale.US, "%.2f sec", ((float)duration) / 1000);
        } else {
            return String.format(Locale.US, "%d msec", duration);
        }
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100 * 1024) {
            return String.format(Locale.US, "%.2f MB", ((float)bytes) / 1024 / 1024);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", ((float)bytes) / 1024);
        } else {
            return String.format(Locale.US, "%d B", bytes);
        }
    }

    private static String formatedBit(long bytes) {
        if (bytes >= 100 * 1000) {
            return String.format(Locale.US, "%.2f mbps", ((float)bytes) / 1000 / 1000);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f kbps", ((float)bytes) / 1000);
        } else {
            return String.format(Locale.US, "%d bps", bytes);
        }
    }

    private static String formatedSec(long usec) {
        return String.format(Locale.US, "%.6f", ((float)usec) / 1000 / 1000);
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    InfoHudViewHolder holder = InfoHudViewHolder.this;
                    IjkMediaPlayer mp = null;
                    if (mMediaPlayer == null)
                        break;
                    if (mMediaPlayer instanceof IjkMediaPlayer) {
                        mp = (IjkMediaPlayer) mMediaPlayer;
                    } else if (mMediaPlayer instanceof MediaPlayerProxy) {
                        MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                        IMediaPlayer internal = proxy.getInternalMediaPlayer();
                        if (internal != null && internal instanceof IjkMediaPlayer)
                            mp = (IjkMediaPlayer) internal;
                    }
                    if (mp == null)
                        break;

                    int vdec = mp.getVideoDecoder();
//                    switch (vdec) {
//                        case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
//                            setRowValue(R.string.vdec, "avcodec" + (mi != null ? ", " + mi.mVideoDecoderImpl : ""));
//                            break;
//                        case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
//                            setRowValue(R.string.vdec, "MediaCodec" + (mi != null ? ", " + mi.mVideoDecoderImpl : ""));
//                            break;
//                        default:
//                            setRowValue(R.string.vdec, "");
//                            break;
//                    }

                    setRowValue(R.string.vdec, mp.getVideoCodecInfo());
                    setRowValue(R.string.adec, mp.getAudioCodecInfo());

                    float fpsOutput = mp.getVideoOutputFramesPerSecond();
                    float fpsDecode = mp.getVideoDecodeFramesPerSecond();
                    setRowValue(R.string.fps, String.format(Locale.US, "%.2f / %.2f", fpsDecode, fpsOutput));

                    long videoCachedDuration = mp.getVideoCachedDuration();
                    long audioCachedDuration = mp.getAudioCachedDuration();
                    long videoCachedBytes    = mp.getVideoCachedBytes();
                    long audioCachedBytes    = mp.getAudioCachedBytes();

                    setRowValue(R.string.v_cache, String.format(Locale.US, "%s, %s", formatedDurationMilli(videoCachedDuration), formatedSize(videoCachedBytes)));
                    setRowValue(R.string.a_cache, String.format(Locale.US, "%s, %s", formatedDurationMilli(audioCachedDuration), formatedSize(audioCachedBytes)));

                    float speed = mp.getSpeed(.0f);
                    float avdelay = mp.getAvDelay();
                    float avdiff = mp.getAvDiff();
                    float efps = mp.getEfps();
                    float tbr = mp.getTbr();
                    setRowValue(R.string.speed, "" + String.format(Locale.US, "%.2f", speed));
                    setRowValue(R.string.av_delay, "" + String.format(Locale.US, "%.3f", avdelay));
                    setRowValue(R.string.av_diff, "" + String.format(Locale.US, "%.3f", avdiff));
                    setRowValue(R.string.efps, "" + String.format(Locale.US, "%.2f", efps));
                    setRowValue(R.string.tbr, "" + String.format(Locale.US, "%.2f", tbr));

                    long videoStream = mp.getSelectedVideoStream();
                    long audioStream = mp.getSelectedAudioStream();
                    long bitrate    = mp.getBitrate();
                    long tcpspeed    = mp.getTcpSpeed();
                    long videoSoftTime    = mp.getVideoSoftDecTime();
                    long videoRefreshTime    = mp.getVideoRefreshTime();
                    int width = mp.getVideoWidth();
                    int height = mp.getVideoHeight();

                    setRowValue(R.string.v_stream, "" + String.format(Locale.US, "%d", videoStream));
                    setRowValue(R.string.a_stream, "" + String.format(Locale.US, "%d", audioStream));
                    setRowValue(R.string.resolution, "" + String.format(Locale.US, "%d X %d", width, height));
                    setRowValue(R.string.bit_rate, "" + String.format(Locale.US, "%s", formatedBit(bitrate)));
                    setRowValue(R.string.tcp_speed, "" + String.format(Locale.US, "%s/s", formatedSize(tcpspeed)));
                    if (vdec == IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC ) {
                        setRowValue(R.string.v_dec_time, "" + String.format(Locale.US, "%ss", formatedSec(videoSoftTime)));
                    }
                    setRowValue(R.string.v_disp_time, "" + String.format(Locale.US, "%ss", formatedSec(videoRefreshTime)));
                    setRowValue(R.string.frame_drop, "" + String.format(Locale.US, "%4d,%4d", mp.getVideoFrameDropEarly(),  mp.getVideoFrameDropLate()));
//                    float total = videoRemainingTime + videoRefreshTime/1000/1000 - avdiff;
//                    setRowValue(R.string.total_time, "" + String.format(Locale.US, "%3f", total));



                    mHandler.removeMessages(MSG_UPDATE_HUD);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);

//                    MediaInfo mi = mp.getMediaInfo();
//                    Log.d("wym", "" + String.format(Locale.US, "%s %s:%s %s:%s", mi.mMediaPlayerName, mi.mVideoDecoder, mi.mVideoDecoderImpl,
//                                                    mi.mAudioDecoder, mi.mAudioDecoderImpl));
//                    Log.d("wym", "" +  String.format(Locale.US, "%s %d %d %d", mi.mMeta.mFormat, mi.mMeta.mDurationUS, mi.mMeta.mStartUS, mi.mMeta.mBitrate));

//                    Log.d("wym", "" +  String.format(Locale.US, "%s", mi.mMeta.mVideoStream.toString()));
//                    Log.d("wym", "" +  String.format(Locale.US, "%s", mi.mMeta.mAudioStream.toString()));
//                    Log.d("wym", "" +  String.format(Locale.US, "%s", mi.mMeta.mStreams.toString()));

                }
            }
        }
    };
}
