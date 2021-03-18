package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private Button button;

    private MediaPlayer mediaPlayer;

    private boolean timerIsCounting = false;
    private CountDownTimer countDownTimer;

    private SharedPreferences sp;

    private int defaultInterval;
    private int maxTimerSeconds = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        defaultInterval = Integer.parseInt(sp.getString("default_interval", "30"));
        tuneSeekBar();
        formAndSetTimerText(defaultInterval);
    }

    public void tuneSeekBar() {
        seekBar.setMax(maxTimerSeconds);
        seekBar.setProgress(defaultInterval);

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

    public void formAndSetTimerText(int totalSecondsLeft) {
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

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
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
                int totalSecondsLeft = (int) (millisUntilFinished / 1000) + 1;
                formAndSetTimerText(totalSecondsLeft);
            }

            @Override
            public void onFinish() {
                stopCountDownTimer();

                String melodyName = sp.getString("timer_melody", "bell");
                if (melodyName.equals("bell")) {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                } else if (melodyName.equals("alarm_siren")) {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                } else if (melodyName.equals("bip")) {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                }
                mediaPlayer.start();
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
        button.setText(getString(R.string.start));
        timerIsCounting = false;
        seekBar.setEnabled(true);
        seekBar.setProgress(defaultInterval);
        formAndSetTimerText(defaultInterval);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")) {
            try {
                defaultInterval = Integer.parseInt(sp.getString("default_interval", "30"));
            } catch (NumberFormatException e) {

            }
            formAndSetTimerText(defaultInterval);
            seekBar.setProgress(defaultInterval);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }
}