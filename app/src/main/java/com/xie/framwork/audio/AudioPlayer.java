package com.xie.framwork.audio;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private Activity context;
    private MediaPlayer mediaPlayer;
    private String playingUrl;
    private OnMediaPlayerListener onMediaPlayerListener;
    private Timer mTimer;

    private int startTime;
    private SeekBar seekBar;
    private TextView timeTextView;

    private int duration;
    private int current;
    private int second;
    private int amountToUpdate = 0;
    private int currentTime = 0;

    public AudioPlayer(Activity context) {
        this.context = context;
    }

    public void play(String audioPath, int startTime) {
        this.startTime = startTime;
        if (mediaPlayer != null) {
            stop();
            mediaPlayer = null;
        }
        this.playingUrl = audioPath;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, Uri.parse(this.playingUrl));
        } catch (IOException e) {
            Toasty.error(context, e.getMessage()).show();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.prepareAsync();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (null != onMediaPlayerListener) {
            onMediaPlayerListener.pause(currentTime);
        }
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        if (null != mTimer) {
            mTimer.cancel();
        }
    }

    private void updateUI() {
        if (seekBar != null) {
            seekBar.setMax(100);
            seekBar.setProgress(current);
            seekBar.setSecondaryProgress(second);
        }
//        TODO 更新UI
    }

    public boolean isPlaying(String url) {
        if (url != null) {
            return mediaPlayer != null && mediaPlayer.isPlaying() && url.equals(playingUrl);
        }
        return false;
    }

    public void setOnMediaPlayerListener(OnMediaPlayerListener onMediaPlayerListener) {
        this.onMediaPlayerListener = onMediaPlayerListener;
    }

    private void resetBindView() {
        if (seekBar == null) {
            return;
        }
        seekBar.setOnSeekBarChangeListener(null);
        TextView textView = (TextView) timeTextView.getTag();
        if (textView != null) {
            textView.setText("播放");
        }
        current = 0;
        second = 0;
        if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
            duration = mediaPlayer.getDuration();
        } else {
            duration = (int) seekBar.getTag();
        }
        updateUI();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null != mTimer) {
            mTimer.cancel();
        }
        if (onMediaPlayerListener != null) {
            onMediaPlayerListener.onComplete(mp);
        }
        current = 0;
        second = 0;
        updateUI();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                current = progress;
                updateUI();
                mediaPlayer.seekTo(progress * amountToUpdate);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (startTime > 0) {
            current = startTime / amountToUpdate;
            mediaPlayer.seekTo(startTime);
        } else {
            duration = mp.getDuration();
            current = 0;
            amountToUpdate = duration / 100;
            second = 0;
        }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentTime = amountToUpdate * current;
                if (!(currentTime >= duration)) {
                    current += 1;
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                }
            }
        }, 0, amountToUpdate);
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toasty.error(context, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:").show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toasty.error(context, "MEDIA_ERROR_SERVER_DIED:").show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toasty.error(context, "MEDIA_ERROR_UNKNOWN:").show();
                break;
            default:
                Toasty.error(context, "播放错误：" + extra).show();
                break;
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        second = percent;
        updateUI();
    }

    public interface OnMediaPlayerListener {

        void pause(int current);

        void onComplete(MediaPlayer mp);
    }

    public void bindControlView(SeekBar seekBar, TextView timeTextView) {
        if (this.seekBar != seekBar || this.timeTextView != timeTextView) {
            resetBindView();
        }
        this.seekBar = seekBar;
        this.timeTextView = timeTextView;
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(this);
        }
    }
}
