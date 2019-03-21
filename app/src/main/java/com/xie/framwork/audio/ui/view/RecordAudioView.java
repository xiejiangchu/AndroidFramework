package com.xie.framwork.audio.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.xie.framwork.R;
import com.xie.framwork.audio.AudioRecordManager;
import com.xie.framwork.audio.RecordStatus;


public class RecordAudioView extends AppCompatButton implements View.OnClickListener {

    private final String TAG = "RecordAudioView";
    private IRecordAudioListener recordAudioListener;
    private AudioRecordManager audioRecordManager;



    //0:初始状态
    //1:录音中
    //2:录音完成
    //3:录音取消
    private int status = RecordStatus.STATUS_INIT;

    public RecordAudioView(Context context) {
        this(context, null);
    }

    public RecordAudioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordAudioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setRecordAudioListener(IRecordAudioListener recordAudioListener) {
        this.recordAudioListener = recordAudioListener;
    }

    private void init() {
        audioRecordManager = AudioRecordManager.getInstance();
        setOnClickListener(this);
        setBackgroundResource(R.drawable.icon_record_voice);
    }

    @Override
    public void onClick(View v) {
        if (status == RecordStatus.STATUS_INIT) {
            startRecordAudio();
        } else if (status == RecordStatus.STATUS_RECORDING) {
            stopRecordAudio();
        }
    }

    private void startRecordAudio() {
        boolean isPrepaired = recordAudioListener.onRecordPrepare();
        if (isPrepaired) {
            Log.d(TAG, "startRecordAudio() has prepaired.");
            String audioFileName = recordAudioListener.onRecordStart();
            try {
                audioRecordManager.init(audioFileName);
                audioRecordManager.startRecord();
                status = RecordStatus.STATUS_RECORDING;
                setBackgroundResource(R.drawable.icon_record_voice);
            } catch (Exception e) {
                recordAudioListener.onRecordCancel();
                recordAudioListener.onRecordCancel();
                status = RecordStatus.STATUS_RECORDER_CANCELED;
                setBackgroundResource(R.drawable.icon_record_voice);
            }
        }
    }

    private void stopRecordAudio() {
        if (status == RecordStatus.STATUS_RECORDING) {
            audioRecordManager.stopRecord();
            recordAudioListener.onRecordStop();
            status = RecordStatus.STATUS_RECORD_FINISHED;
            setBackgroundResource(R.drawable.icon_record_voice);
        }
    }

    public void invokeStop() {
        stopRecordAudio();
    }


    public interface IRecordAudioListener {
        boolean onRecordPrepare();
        String onRecordStart();
        boolean onRecordStop();
        boolean onRecordCancel();
    }

}
