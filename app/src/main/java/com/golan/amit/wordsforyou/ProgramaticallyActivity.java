package com.golan.amit.wordsforyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProgramaticallyActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener, View.OnClickListener {

    CountDownTimer cTimer;

    TextView[] up, down;
    TextView tvTimeDisplay;
    ImageButton ibRollback;
    int heightAbs = -1;
    int widthAbs = -1;

    float initialX = -1;
    float initialY = -1;

    MediaPlayer mp;
    SeekBar sb;
    AudioManager am;

    NamingHelper nh;

    Rect[] upPositionRectArray;
    private SharedPreferences sp;
    private int millisInFuture;
    private int countDownInterval;
    private long timeToRemain;

    public enum DemoState {
        YES, NO
    }

    public enum FlowState {
        RESUME, PAUSE
    };

    private DemoState demoState = DemoState.YES;
    private FlowState flowState = FlowState.RESUME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programatically);

        init();

        setListeners();

        PlayNameAsync();

    }

    private void PlayNameAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    PlayName();
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
    }

    private void PlayName() {
        nh.generate_random_name();
        for (int i = 0; i < nh.getRnd_name().length; i++) {
            setText(down[i], nh.getNameCharByIndex(i));
            SystemClock.sleep(150);
        }
    }

    private void setText(final TextView textView, final String nameCharByIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(nameCharByIndex);
            }
        });
    }

    private void displayDemoAsync() {
        demoState = DemoState.NO;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    displayDemo();
                } catch (Exception e) {
                    Log.e(MainActivity.DEBUGTAG, "exception while calling display demo: " + e);
                }
                return null;
            }
        }.execute();
    }

    private void displayDemo() {
        for (int u = 0; u < 7; u++) {
            int cell = (int) (Math.random() * down.length);
            float initY = down[cell].getY();
            float endY = 450;
            for (int y = (int) initY; y > (int) endY; y--) {
                down[cell].setY(y);
            }
            SystemClock.sleep(250);
            for (int y = (int) endY; y < (int) initY; y++) {
                down[cell].setY(y);
            }
            SystemClock.sleep(250);
        }
    }

    private void init() {
        cTimer = null;
        timeToRemain = 180000;
        up = new TextView[]{
                findViewById(R.id.tvUp0), findViewById(R.id.tvUp1), findViewById(R.id.tvUp2),
                findViewById(R.id.tvUp3), findViewById(R.id.tvUp4)
        };
        down = new TextView[]{
                findViewById(R.id.tvDown0), findViewById(R.id.tvDown1), findViewById(R.id.tvDown2),
                findViewById(R.id.tvDown3), findViewById(R.id.tvDown4)
        };
        tvTimeDisplay = findViewById(R.id.tvTimeDisplayId);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightAbs = displayMetrics.heightPixels;
        widthAbs = displayMetrics.widthPixels;

        nh = new NamingHelper();

        sb = findViewById(R.id.sb);
        mp = MediaPlayer.create(this, R.raw.words);
        mp.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(max);
        sb.setProgress(max / 4);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max / 4, 0);
        sb.setOnSeekBarChangeListener(this);

        upPositionRectArray = new Rect[down.length];
        ibRollback = findViewById(R.id.ibRollbackId);

        sp = getSharedPreferences(getString(R.string.sharetag), MODE_PRIVATE);
    }

    private void setListeners() {
        for (int i = 0; i < down.length; i++) {
            down[i].setOnTouchListener(this);
        }
        ibRollback.setOnClickListener(this);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ibRollback.setY(100);
        ibRollback.setX(20);

        sb.setY(350);
        sb.setX(105);

        tvTimeDisplay.setY(1100);
        tvTimeDisplay.setX(175);

        int startXpos = 150;
        for (int i = 0; i < up.length; i++) {
            up[i].setY(25);
            up[i].setX(startXpos);

            down[i].setY(heightAbs - 800);
            down[i].setX(startXpos);

            int width = up[i].getWidth();
            int height = up[i].getHeight();
            float x = up[i].getX();
            float y = up[i].getY();
            Rect r = new Rect((int) x,
                    (int) y,
                    (int) (x + width),
                    (int) (y + height));
            upPositionRectArray[i] = new Rect(r);
            startXpos += up[i].getWidth() + 10;
        }

        if (demoState.equals(DemoState.YES))
            displayDemoAsync();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int currId = -1;
        TextView currTv = null;
        for (int i = 0; i < down.length; i++) {
            if (v == down[i]) {
                currId = i;
                currTv = down[i];
            }
        }
        if (currId == -1 || currTv == null) {
            Log.e(MainActivity.DEBUGTAG, "wrong touch");
            return true;
        }
        if (currTv.getText().toString().equalsIgnoreCase("")) {
            Log.e(MainActivity.DEBUGTAG, "empty source cell");
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float evX = event.getRawX();
                float evY = event.getRawY();
                Rect r = new Rect((int) evX, (int) currTv.getY(), (int) (evX + currTv.getWidth()), (int) (currTv.getY()) + currTv.getHeight());
                int cellId = -1;
                cellId = matchAndReturnCellId(r);
                if (initialX != -1) {
                    currTv.setX(initialX);
                    initialX = -1;
                }
                if (initialY != -1) {
                    currTv.setY(initialY);
                    initialY = -1;
                }
                if (cellId == -1) {
                    Log.e(MainActivity.DEBUGTAG, "NO CELL DETECTED");
                    return true;
                }

                String tmpCellVal = null;
                if (up[cellId].getText() == null) {
                    Log.e(MainActivity.DEBUGTAG, "get text is null ");
                    return true;
                }
                try {
                    tmpCellVal = up[cellId].getText().toString();
                } catch (Exception e) {
                    return true;
                }
                if (!tmpCellVal.equalsIgnoreCase("")) {
                    return true;
                }
                String tmpTextChar = null;
                try {
                    tmpTextChar = currTv.getText().toString();
                } catch (Exception e) {
                    return true;
                }

                //  Let the fun begin
                ibRollback.setVisibility(View.VISIBLE);
                up[cellId].setText(tmpTextChar);
                currTv.setText("");

                nh.increaseName_counter();
                if (nh.getName_counter() == nh.getCurr_name().length()) {    //  finished
                    if(MainActivity.DEBUG) {
                        Log.i(MainActivity.DEBUGTAG, "finished ");
                    }
                    if (cTimer != null) {
                        try {
                            cTimer.cancel();
                            if(MainActivity.DEBUG) {
                                Log.i(MainActivity.DEBUGTAG, "time cancel when finished");
                            }
                        } catch (Exception e) {
                            Log.e(MainActivity.DEBUGTAG, "time cancel when finished exception: " + e);
                        }
                    } else {
                        Log.e(MainActivity.DEBUGTAG, "ctimer is null");
                    }
                    ibRollback.setVisibility(View.INVISIBLE);
                    boolean won = won_evaluated();
                    if (!won) {
                        won = nh.isAlternativeWordExist(getCurrWordAsString());
                    }

                    SharedPreferences.Editor editor = sp.edit();
                    if (won) {
                        editor.putString("name", getCurrWordAsString());
                    } else {
                        editor.putString("name", nh.getCurr_name());
                    }
                    editor.putBoolean("state", won);
                    editor.commit();
                    Intent i = new Intent(this, EndActivity.class);
                    startActivity(i);
                    return true;
                }
                nh.push_stack(currId);
                nh.push_stack_u(cellId);

                break;
            case MotionEvent.ACTION_DOWN:
                initialX = currTv.getX();
                initialY = currTv.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(currTv, event);

                break;
            case MotionEvent.ACTION_MASK:
                Log.i(MainActivity.DEBUGTAG, "mask was touched");
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        if (v == ibRollback) {
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "rollback delete last button was clicked");
            }
            nh.decreaseName_counter();
            if (nh.getName_counter() == 0)
                ibRollback.setVisibility(View.INVISIBLE);
            int tmpCurrPosition = -1;
            int tmpCurrUpPosition = -1;
            if (nh.getSti().size() > 0) {
                tmpCurrPosition = nh.pop_stack();
            }
            if (nh.getStu().size() > 0) {
                tmpCurrUpPosition = nh.pop_stack_u();
            }
            String tmpChar = null;
            try {
                tmpChar = up[tmpCurrUpPosition].getText().toString();
                up[tmpCurrUpPosition].setText("");
                down[tmpCurrPosition].setText(tmpChar);
            } catch (Exception e) {
            }
        }
    }

    private String getCurrWordAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < up.length; i++) {
            sb.append(up[i].getText().toString());
        }
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "build current word string from characters: {" + sb.toString() + "}");
        }
        return sb.toString();
    }

    private boolean won_evaluated() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < up.length; i++) {
            sb.append(up[i].getText().toString());
        }
        Log.i(MainActivity.DEBUGTAG, "comparing {" + sb.toString() + "} to {" + nh.getCurr_name() + "}");
        return sb.toString().equalsIgnoreCase(nh.getCurr_name());
    }

    private int matchAndReturnCellId(Rect incoming) {
        int cellId = -1;

        for (int i = 0; i < upPositionRectArray.length; i++) {
            Rect r = new Rect(upPositionRectArray[i]);
            if (r.contains(incoming.left, incoming.centerY())) {
                cellId = i;
                break;
            }
        }
        return cellId;
    }

    private void handleMove(TextView currTv, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float evX = event.getRawX();
            float evY = event.getRawY();
            if (evX > 10 && evX < (widthAbs - 250)) {
                currTv.setX(evX);
            }
            //  TODO: find relative y border
            if (evY > 250 && evY < heightAbs) {
                currTv.setY(evY - currTv.getHeight() * 2);
//                Log.i(MainActivity.DEBUGTAG, "y: " + evY);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flowState = FlowState.PAUSE;
        cTimer.cancel();
        mp.pause();
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "on pause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowState = FlowState.RESUME;
        timerDemo(timeToRemain);
        if (mp != null) {
            try {
                mp.start();
            } catch (Exception e) {
            }
        }
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "on resume");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "on destroy");
        }
    }

    private void timerDemo(long millisInFuture) {

        countDownInterval = 1000;
        cTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if(flowState.equals(FlowState.PAUSE)) {
                    millisUntilFinished = timeToRemain;
                } else {
                    timeToRemain = millisUntilFinished;
                }
                long sec = millisUntilFinished / 1000;

                if(sec == 60) {
                    Toast.makeText(ProgramaticallyActivity.this, nh.getCurr_heb_name(), Toast.LENGTH_LONG).show();
                }

                tvTimeDisplay.setTextColor(Color.GREEN);
                if (sec < 30) {
                    tvTimeDisplay.setTextColor(Color.RED);
                }
                tvTimeDisplay.setText(String.valueOf(sec));
            }

            @Override
            public void onFinish() {
                Toast.makeText(ProgramaticallyActivity.this, "נגמר הזמן", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("state", false);
                editor.putString("name", nh.getCurr_name());
                editor.commit();

                Intent i = new Intent(ProgramaticallyActivity.this, EndActivity.class);
                startActivity(i);
            }
        }.start();
    }
}