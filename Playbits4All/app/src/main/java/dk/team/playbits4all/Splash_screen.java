package dk.team.playbits4all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;
public class Splash_screen extends AppCompatActivity {
    private static final int SPLASH_SCREEN = 4000;

    private ImageView imageView, imageView1;
    private Animation shake,move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        imageView = findViewById(R.id.imageV);
        imageView1 = findViewById(R.id.imageV1);

        shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        move = AnimationUtils.loadAnimation(this, R.anim.move_animation);
        
        imageView.startAnimation(shake);
        imageView1.startAnimation(move);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_screen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);
    }
}
