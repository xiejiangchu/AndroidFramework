package com.xie.framwork.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xie.framwork.audio.entity.EnterRecordAudioEntity;
import com.xie.framwork.audio.ui.AudioRecordActivity;


/**
 * Created by chenxf on 17-7-6.
 */

public class AudioRecordJumpUtil {

    /**
     * 跳转录制语音页面
     */
    public static void startRecordAudio(Context context) {
        EnterRecordAudioEntity entity = new EnterRecordAudioEntity();
        entity.setSourceType(EnterRecordAudioEntity.SourceType.AUDIO_FEED);

        Intent intent = new Intent(context, AudioRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AudioRecordActivity.KEY_ENTER_RECORD_AUDIO_ENTITY, entity);
        intent.putExtra(AudioRecordActivity.KEY_AUDIO_BUNDLE, bundle);
        context.startActivity(intent);
    }

    public static void startRecordAudio(Activity activity, int requestCode) {
        EnterRecordAudioEntity entity = new EnterRecordAudioEntity();
        entity.setSourceType(EnterRecordAudioEntity.SourceType.AUDIO_FEED);

        Intent intent = new Intent(activity, AudioRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AudioRecordActivity.KEY_ENTER_RECORD_AUDIO_ENTITY, entity);
        intent.putExtra(AudioRecordActivity.KEY_AUDIO_BUNDLE, bundle);
        activity.startActivityForResult(intent, requestCode);
    }
}
