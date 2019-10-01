package com.mahmoudsalah.guess;

import android.app.DownloadManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT =1000 ;

    Switch switchButton;
    TextView rightWrongText, countText;
    Button startButton;
    byte wrongScore;
    int x, y;
    Random random = new Random();
    boolean getstarted;
    ArrayList<TextView> blocks = new ArrayList<>();
    byte rep, shakerep,micCou;
    MediaPlayer wrong, start;
    MediaPlayer right;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchButton = findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Sensey.getInstance().init(MainActivity.this);
                    Sensey.getInstance().startShakeDetection(new ShakeDetector.ShakeListener() {
                        @Override
                        public void onShakeDetected() {

                        }

                        @Override
                        public void onShakeStopped() {
                            shakerep++;
                            if (shakerep == 1) {
//                    wrongScore = 0;
//                    rightWrongText.setText("");
//                    countText.setText("");
                                for (TextView block : blocks) {
                                    block.setEnabled(true);
                                }
                                x = random.nextInt(9) + 1;

                                if (rep == x) {
                                    x = random.nextInt(9) + 1;
                                    rep = (byte) x;
                                    return;
                                } else {
                                    rep = (byte) x;
                                    getstarted = true;

                                }
                            } else if (shakerep > 0) {
                                y = random.nextInt(9) + 1;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak("" + y, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    tts.speak("" + y, TextToSpeech.QUEUE_FLUSH, null);
                                }
                                Toast.makeText(MainActivity.this, "the number is " + y, Toast.LENGTH_SHORT).show();
                                if (y == x) {
                                    rightWrongText.setText("Right");
                                    right.start();
                                    shakerep = 0;
                                    getstarted = false;
                                } else {
                                    rightWrongText.setText("Wrong");
                                    wrong.start();
                                    wrongScore++;
                                    countText.setText("" + wrongScore);
                                    if (wrongScore == 3) {
                                        Toast.makeText(MainActivity.this, "Game Over the correct number is " + x, Toast.LENGTH_SHORT).show();
                                        shakerep = 0;
                                        wrongScore = 0;
                                        rightWrongText.setText("");
                                        countText.setText("");
                                    }
                                }

                            }

                        }
                    });
                }
            }
        });
        rightWrongText = findViewById(R.id.rightWrongText);
        countText = findViewById(R.id.countText);
        startButton = findViewById(R.id.startButton);
        wrong = MediaPlayer.create(this, R.raw.wrong2);
        right = MediaPlayer.create(this, R.raw.right2);
        start = MediaPlayer.create(this, R.raw.wrong);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });

    }
private void speak(){
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"hi speak something");

    try{
startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }
    catch (Exception e){
        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this, "" + result.get(0), Toast.LENGTH_SHORT).show();
                    x = random.nextInt(9)+1;
                    if (result.get(0) == String.valueOf(x)){
                        rightWrongText.setText("Right");
                        right.start();
                        getstarted = false;
                    }
                    else{
                        wrongScore++;
                        wrong.start();
                        rightWrongText.setText("Wrong");
                        countText.setText("" + wrongScore);
                    }
                    if (wrongScore == 3) {
                        Toast.makeText(this, "Game Over and the number was " + x, Toast.LENGTH_SHORT).show();
                        wrong.start();
//                        rightWrongText.setText("");
//                        countText.setText("");
//                        wrongScore = 0;
                        getstarted = false;
                    }
                    }
                }
                break;
            }
        }

    @Override
    protected void onPause() {
        Sensey.getInstance().stop();
        super.onPause();
    }

    public void start(View view) {
        wrongScore = 0;
        rightWrongText.setText("");
        countText.setText("");
        for (TextView block : blocks) {
            block.setEnabled(true);
        }
        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(1)
                .playOn(startButton);
        x = random.nextInt(9) + 1;

        if (rep == x) {
            x = random.nextInt(9) + 1;
            rep = (byte) x;
            return;
        } else {
            rep = (byte) x;
            getstarted = true;
//            Toast.makeText(this, "" + x, Toast.LENGTH_SHORT).show();
        }
    }

    public void answer(View view) {
        if (getstarted == false) {
            Toast.makeText(this, "Please start game first", Toast.LENGTH_SHORT).show();
            start.start();
            YoYo.with(Techniques.Flash)
                    .duration(1000)
                    .repeat(1)
                    .playOn(startButton);
            return;
        } else {


            TextView tv = (TextView) view;
            tv.setEnabled(false);
            blocks.add(tv);
            int number = Integer.parseInt(tv.getText().toString());
            YoYo.with(Techniques.Pulse)
                    .duration(500)
                    .repeat(1)
                    .playOn(tv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak("" + number, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                tts.speak("" + number, TextToSpeech.QUEUE_FLUSH, null);
            }
                if (number == x) {
                    rightWrongText.setText("Right");
                    right.start();
                    getstarted = false;
                } else {
                    wrongScore++;
                    wrong.start();
                    rightWrongText.setText("Wrong");
                    countText.setText("" + wrongScore);
                }
                if (wrongScore == 3) {
                    Toast.makeText(this, "Game Over and the number was " + x, Toast.LENGTH_SHORT).show();
                    wrong.start();
                    rightWrongText.setText("");
                    countText.setText("");
                    wrongScore = 0;
                    getstarted = false;
                }
            }


        }

    public void mic(View view) {
        micCou++;
        speak();
        if (micCou == 4){
            rightWrongText.setText("");
            countText.setText("");
            wrongScore = 0;
            micCou =0;
        }
    }



    }
