package kr.hs.emirim.w2009.project13_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list1;
    TextView textMusic;
    TextView textTime;
    SeekBar seek1;
    ArrayList<String> arrList;
    String selectedMusic;
    String musicPath = Environment.getExternalStorageDirectory().getPath() + "/";
    MediaPlayer media;
    Button btnStart,btnStop,btnCurs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MP3 Player");
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        arrList = new ArrayList<String>();
        File[] listFiles = new File(musicPath).listFiles();
        String filename, extName;
        for (File file : listFiles){
            filename = file.getName();
            extName = filename.substring(filename.length()-3);
            if (extName.equals("mp3")){
                arrList.add(filename);
            }
        }
        list1 = findViewById(R.id.list1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, arrList);
        list1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list1.setAdapter(adapter);
        list1.setItemChecked(0, true);

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMusic = arrList.get(position);
            }
        });
        selectedMusic = arrList.get(0);

         btnStart = findViewById(R.id.btn_start);
         btnStop = findViewById(R.id.btn_stop);
        textMusic = findViewById(R.id.text_music);
        seek1 = findViewById(R.id.seek1);
        textTime = findViewById(R.id.text_time);
        btnCurs = findViewById(R.id.btn_curs);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media = new MediaPlayer();
                try {
                    media = new MediaPlayer();
                    media.setDataSource(musicPath + selectedMusic);
                    media.prepare();
                    media.start();
                    textMusic.setText(selectedMusic + ":");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnThread();

                new Thread(){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                    @Override
                    public void run() {
                        if(media == null){
                            return;
                        }
                        seek1.setMax(media.getDuration());
                        while(media.isPlaying()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seek1.setProgress(media.getCurrentPosition());
                                    textTime.setText("진행시간: ");
                                    textTime.append(dateFormat.format(media.getCurrentPosition()));

                                }
                            });
                            SystemClock.sleep(200);
                        }
                    }
                }.start();
            }
        });
        btnCurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCurs.getText().equals("일시중지")){
                    media.pause();
                    btnCurs.setText("이어듣기");
                }else if(btnCurs.getText().equals("이어듣기")){
                    media.start();
                    runOnThread();
                    btnCurs.setText("일시중지");
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media.stop();
                media.reset();
                btnStart.setClickable(true);
                btnStop.setClickable(false);
                textMusic.setText("실행음악 중지: ");
                textTime.setText("진행 시간: ");
            }
        });
        btnStop.setClickable(false);
;    }
    public void runOnThread(){
        new Thread(){
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            @Override
            public void run(){
                if (media == null){
                    return;
                }
                seek1.setMax(media.getDuration());
                while(media.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seek1.setProgress(media.getCurrentPosition());
                            textTime.setText("진행 시간: ");
                            textTime.append(dateFormat.format(media.getCurrentPosition()));
                        }
                    });
                    SystemClock.sleep(200);
                }
            }
        }.start();
    }
}