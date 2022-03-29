package hska.mobilegis.com.fernsehturmapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by slytk on 17.01.2018.
 */

public class SplashScreen extends AppCompatActivity {

    private ImageView iv; //iv for ImageView

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        iv = (ImageView) findViewById(R.id.imageView3);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(myanim);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent splash = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(splash);
                }
            }
        };
        timerThread.start();
    }
}





//the intent is to move from the splash screen to the Main Activity