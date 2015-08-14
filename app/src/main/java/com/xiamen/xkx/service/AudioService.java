package com.xiamen.xkx.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class AudioService extends Service {
    AudioBinder binder = new AudioBinder();
    private MediaPlayer mediaPlayer;
    // 是不是已经导入了资源
    private boolean isFrist;
    private OnPlayCompleteListener listener;

    public AudioService() {
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.release();
        return super.onUnbind(intent);
    }

    public void play(Uri uri) throws IOException {
        if (uri == null) {
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this, uri);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                isFrist = true;
            }
        });
        //try {
        //} catch (IllegalArgumentException e) {
        //    //CrashHandler.getInstance().logStringToFile(e.toString());
        //} catch (SecurityException e) {
        //    //CrashHandler.getInstance().logStringToFile(e.toString());
        //} catch (IllegalStateException e) {
        //    //CrashHandler.getInstance().logStringToFile(e.toString());
        //} catch (IOException e) {
        //    //CrashHandler.getInstance().logStringToFile(e.toString());
        //}
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                listener.onPlayComplete();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface OnPlayCompleteListener {
        public void onPlayComplete();
    }

    public class AudioBinder extends Binder {
        public void setOnPlayCompleteListener(OnPlayCompleteListener lis) {
            listener = lis;
        }

        // 初始化并播放
        public void audioPlay(Uri uri) throws IOException {
            play(uri);
        }

        // 开始播放
        public void audioStart() {
            mediaPlayer.start();
        }

        // 暂停播放
        public void audioPause() {
            mediaPlayer.pause();
            // 设置为true后，暂停服务后不会被kill掉
            stopForeground(true);
        }

        // 停止播放
        public void audioStop() {
            mediaPlayer.stop();
        }

        // 是否在播放
        public boolean audioIsPlaying() {
            return mediaPlayer.isPlaying();
        }

        // 是否已经导入资源
        public boolean audioIsFrist() {
            return isFrist;
        }

        // 播放下一个
        public void audioPlayNext(Uri uri) throws IOException {
            isFrist = false;
            play(uri);
        }
    }
}
