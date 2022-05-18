package hska.mobilegis.com.fernsehturmapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class MyCustomAppIntro extends AppIntro {
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //add slide1
        addSlide(AppIntroFragment.newInstance("Slide 1", "This is the first slide",
                R.mipmap.ic_launcher, R.font.allerta, R.font.allerta, ContextCompat.getColor(getApplicationContext(), R.color.slide1)));

        //add slide2
        addSlide(AppIntroFragment.newInstance("Slide 2", "This is the second slide",
                R.mipmap.ic_launcher, ContextCompat.getColor(getApplicationContext(), R.color.slide2)));

        //add slide3
        addSlide(AppIntroFragment.newInstance("Slide 3", "This is the third slide",
                R.mipmap.ic_launcher, ContextCompat.getColor(getApplicationContext(), R.color.slide3)));

        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        showStatusBar(true);
        setScrollDurationFactor(3);

        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)
        setColorTransitionsEnabled(true);

        //Prevent the back button from exiting the slides
        //setSystemBackButtonLocked(true);

        //Activate wizard mode (Some aesthetic changes)
        //setWizardMode(true);

        //Show/hide skip button
        setSkipButtonEnabled(true);

        //Enable/disable immersive mode (no status and nav bar)
        setImmersive(true);

        //Enable/disable page indicators
        //setIndicatorEnabled(true);

        //Dhow/hide ALL buttons
        setButtonsEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);




    }

    @Override
    public void onSkipPressed(Fragment currentFragment){
        super.onSkipPressed(currentFragment);
        //Do something when users tap on Skip button
        finish();
    }



    @Override
    public void onDonePressed(Fragment currentFragment){
        super.onDonePressed(currentFragment);
        //Do something when users tap on Skip button
        finish();
    }

}