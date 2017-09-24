package com.xsm.progressview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ProgressView progressView;

    private int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = (ProgressView) findViewById(R.id.progress_view);
        progressView.setProgressListener(new ProgressView.ProgressListener() {
            @Override
            public void progressFinish() {
                Toast.makeText(MainActivity.this, "进度完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void progress(int progress) {
                Log.d(TAG, "progress: "  + progress);
            }
        });
    }

    public void btnStart(View view) {
        Log.d(TAG, "btnStart: width=" + dp2px(300) + "  height=" + dp2px(30));
        progressView.setProgress(0);
        number = 0;
        handler.postDelayed(mRunnable, 100);

    }


    Handler handler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (number <= 100) {
                progressView.setProgress(number);
                if (number <= 50) {
                    handler.postDelayed(mRunnable, 50);
                } else {
                    handler.postDelayed(mRunnable, 100);
                }
                number = number + 1;
            } else {
                Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
