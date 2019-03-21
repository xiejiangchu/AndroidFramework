package com.xie.framwork.audio.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.xie.framwork.R;
import com.xie.framwork.audio.AudioRecordManager;
import com.xie.framwork.audio.entity.EnterRecordAudioEntity;
import com.xie.framwork.audio.ui.view.LineWaveVoiceView;
import com.xie.framwork.audio.ui.view.RecordAudioView;
import com.xie.framwork.audio.util.StringUtil;
import com.xie.framwork.bean.CommonBean;
import com.xie.framwork.request.ApiService;
import com.xie.framwork.utils.MyCallback;
import com.xie.framwork.utils.RetrofitHelper;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 录制语音feed页面
 */
public class AudioRecordActivity extends FragmentActivity implements
        RecordAudioView.IRecordAudioListener, View.OnClickListener {

    private static final String TAG = "AudioRecordActivity";
    public static final String KEY_ENTER_RECORD_AUDIO_ENTITY = "enter_record_audio";
    public static final String KEY_AUDIO_BUNDLE = "audio_bundle";
    public static final long DEFAULT_MAX_RECORD_TIME = 60*1000;
    public static final long DEFAULT_MIN_RECORD_TIME = 2000;
    protected static final int DEFAULT_MIN_TIME_UPDATE_TIME = 1000;

    private RecordAudioView recordAudioView;
    private String audioFileName;
    private long maxRecordTime = DEFAULT_MAX_RECORD_TIME;
    private long minRecordTime = DEFAULT_MIN_RECORD_TIME;
    private Timer timer;
    private TimerTask timerTask;
    private Handler mainHandler;
    private long recordTotalTime;
    private EnterRecordAudioEntity entity;
    private LineWaveVoiceView mHorVoiceView;
    private View emptyView;
    private View operateView;
    private Button cancelBtn;
    private Button sendBtn;
    private TextView recordTipTV;
    private AudioRecordManager audioRecordManager;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pp_bottom_in, R.anim.pp_bottom_out);
        setContentView(R.layout.activity_sound_feed);
        recordAudioView.setRecordAudioListener(this);
        emptyView.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        mainHandler = new Handler();
        Bundle bundle = getIntent().getBundleExtra(KEY_AUDIO_BUNDLE);
        entity = (EnterRecordAudioEntity) bundle.getSerializable(KEY_ENTER_RECORD_AUDIO_ENTITY);
        audioRecordManager = AudioRecordManager.getInstance();
        apiService = RetrofitHelper.get(ApiService.class);
        startRecordAudio();
    }

    private void startRecordAudio() {
        boolean isPrepaired = onRecordPrepare();
        if (isPrepaired) {
            Log.d(TAG, "startRecordAudio() has prepaired.");
            String audioFileName = onRecordStart();
            try {
                audioRecordManager.init(audioFileName);
                audioRecordManager.startRecord();
            } catch (Exception e) {
                onRecordCancel();
            }
        }
    }

    private void stopRecordAudio() {
        audioRecordManager.stopRecord();
        onRecordStop();
    }

    @Override
    public boolean onRecordPrepare() {
        //检查录音权限
        if (!XXPermissions.isHasPermission(this, Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE)) {
            XXPermissions.with(this)
                    .permission(Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if (isAll) {
                                startRecordAudio();
                            } else {
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                        }
                    });
            return false;
        }
        return true;
    }

    @Override
    public String onRecordStart() {
        recordTotalTime = 0;
        initTimer();
        timer.schedule(timerTask,0,DEFAULT_MIN_TIME_UPDATE_TIME);
        audioFileName = getExternalCacheDir()+ File.separator + createAudioName();
        mHorVoiceView.setVisibility(View.VISIBLE);
        mHorVoiceView.startRecord();
        return audioFileName;
    }

    @Override
    public boolean onRecordStop() {
        if(recordTotalTime >= minRecordTime){
            timer.cancel();
            operateView.setVisibility(View.VISIBLE);
            switch (entity.getSourceType()){
                case AUDIO_FEED:
                    break;
                default:
                    break;
            }
        }else{
            onRecordCancel();
        }
        return false;
    }

    @Override
    public boolean onRecordCancel() {
        if(timer != null){
            timer.cancel();
        }
        updateCancelUi();
        return false;
    }
    private void updateCancelUi(){
        mHorVoiceView.setVisibility(View.INVISIBLE);
        mHorVoiceView.stopRecord();
        deleteTempFile();
    }

    @Override
    protected void onDestroy() {
        AudioRecordManager.getInstance().stopRecord();
        RetrofitHelper.cancel(apiService);
        super.onDestroy();
    }

    private void deleteTempFile(){
        //取消录制后删除文件
        if(audioFileName != null){
            File tempFile = new File(audioFileName);
            if(tempFile.exists()){
                tempFile.delete();
            }
        }
    }


    @Override
    public void onClick(View v) {
    }

    private void sendAudio() {
//        Toast.makeText(this, "录音文件路径: " + audioFileName, Toast.LENGTH_LONG).show();
        File audioFile = new File(audioFileName);
        if (!audioFile.exists()) {
            Toast.makeText(this, "录音失败，请重新录音", Toast.LENGTH_LONG).show();
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), audioFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("voiceFile", audioFile.getName(), requestBody);
//        ThreadPool.getInstance().getCachedPools().submit(new Runnable() {
//            @Override
//            public void run() {
//                File audioFile = new File(audioFileName);
//                String result = AudioConverter.fileToBase64(audioFile);
//                if (result != null) {
//                    Log.d(TAG, "录音文件大小：" + result.length());
//                    Log.d(TAG, "录音文件Base64: \n" + result);
//                }
//
//                String path = getExternalCacheDir().getPath();
//                boolean success = AudioConverter.base64ToFile(result, path, "test_audio.amr");
//                Log.d(TAG, "save file success: " + success);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toasty.normal(getApplicationContext(), "发送成功!").show();
//                        finish();
//                    }
//                });
//            }
//        });
    }

    /**
     * 初始化计时器用来更新倒计时
     */
    private void initTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //每隔1000毫秒更新一次ui
                        updateTimerUI();
                        recordTotalTime += 1000;
                    }
                });
            }
        };
    }

    private void updateTimerUI(){
        if(recordTotalTime > maxRecordTime){
            stopRecordAudio();
            recordAudioView.invokeStop();
        }else{
            String string = String.format(" %s ", StringUtil.formatTimeLong(recordTotalTime));
            mHorVoiceView.setText(string);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pp_bottom_in, R.anim.pp_bottom_out);
    }

    public String createAudioName(){
        long time = System.currentTimeMillis();
        String fileName = UUID.randomUUID().toString() + time + ".amr";
        return fileName;
    }
}
