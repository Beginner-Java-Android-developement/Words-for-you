package com.golan.amit.wordsforyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPlayAgain;
    SharedPreferences sp;
    TextView tvDisplay;
    ImageView ivStatus, ivMain;
    Animation animation;
    SoundPool soundPool;
    int sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        init();
    }

    private void init() {
        ivStatus = findViewById(R.id.ivStatus);
        ivMain = findViewById(R.id.ivMain);
        tvDisplay = findViewById(R.id.tvInfoDisplay);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_slideup);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnPlayAgain.setOnClickListener(this);

        /**
         * Sound
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME).build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10).setAudioAttributes(aa).build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        sp = getSharedPreferences(getString(R.string.sharetag), MODE_PRIVATE);
        String name = sp.getString("name", null);
        final Boolean won = sp.getBoolean("state", false);

        int[] winSounds = new int[] {
                R.raw.applause, R.raw.cheering, R.raw.wine
        };

        sound = won ? (soundPool.load(this, winSounds[((int)(Math.random() * winSounds.length))], 1)) : (soundPool.load(this, R.raw.failtrombone, 1));

        int picResource = won ? R.mipmap.thumbupgreen : R.mipmap.thumbdownred;
        int[] picMain = new int[] {
                R.mipmap.liorbirthday_9_01, R.mipmap.liorbirthday_9_02, R.mipmap.liorbirthday_9_03,
                R.mipmap.liorbirthday_9_04, R.mipmap.liorbirthday_9_05, R.mipmap.liorbirthday_9_06,
                R.mipmap.liorbirthday_9_07, R.mipmap.lior_ariel
        };
        ivMain.setImageResource(picMain[(int)(Math.random() * picMain.length)]);
        if (name != null) {
            String toDisplay = name +  " הצלחת להרכיב את המילה ";
            if(!won) {
                toDisplay = name +  " לא הצלחת להרכיב את המילה ";
            }
            tvDisplay.setText(toDisplay);
            ivStatus.setImageResource(picResource);
            ivStatus.startAnimation(animation);
        }

        soundPool.setOnLoadCompleteListener(
                new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        soundPool.play(sampleId, 1, 1, 0, 1, 1);
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        if(v == btnPlayAgain) {
            soundPool.release();
            Intent i = new Intent(this, ProgramaticallyActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            soundPool.pause(sound);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            soundPool.resume(sound);
        } catch (Exception e) {
        }
    }
}
