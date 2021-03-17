package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView textView;
    Button button;

    MediaPlayer mediaPlayer;

    boolean timerIsCounting = false;
    CountDownTimer countDownTimer;

    int defaultSeconds = 30;
    int maxTimerSeconds = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        tuneSeekBar();

        formAndSetTimerText(defaultSeconds);

        mediaPlayer = MediaPlayer.create(this, R.raw.bell_sound);
    }

    public void tuneSeekBar() {
        seekBar.setMax(maxTimerSeconds);
        seekBar.setProgress(defaultSeconds);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    formAndSetTimerText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void formAndSetTimerText(long totalSecondsLeft) {
        int minutes = (int) (totalSecondsLeft / 60);
        int seconds = (int) (totalSecondsLeft % 60);

        StringBuilder timerText = new StringBuilder();
        if (minutes <= 9) {
            timerText.append("0");
        }
        timerText.append(minutes).append(" : ");
        if (seconds <= 9) {
            timerText.append("0");
        }
        timerText.append(seconds);

        textView.setText(timerText.toString());
    }

    public void onClick(View view) {
        if (!timerIsCounting) {
            startCountDownTimer();
        } else {
            stopCountDownTimer();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    public void startCountDownTimer() {
        if (seekBar.getProgress() == 0) {
            return;
        }

        countDownTimer = new CountDownTimer(1000 * seekBar.getProgress(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSecondsLeft = millisUntilFinished / 1000 + 1;
                formAndSetTimerText(totalSecondsLeft);
                Log.i("Tick", "" + totalSecondsLeft);
            }

            @Override
            public void onFinish() {
                stopCountDownTimer();
                mediaPlayer.start();
                Log.i("Tick", "finished");
            }
        }.start();

        button.setText(getString(R.string.stop));
        seekBar.setEnabled(false);
        timerIsCounting = true;
    }

    public void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        button.setText(getString(R.string.stop));
        timerIsCounting = false;
        seekBar.setEnabled(true);
        seekBar.setProgress(defaultSeconds);
        formAndSetTimerText(defaultSeconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings) {
            Toast.makeText(this, item.getTitle() + " will be later, maybe", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_item_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Learning Timer App")
                    .setIcon(R.drawable.ic_baseline_info_24)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
        return true;
    }
}