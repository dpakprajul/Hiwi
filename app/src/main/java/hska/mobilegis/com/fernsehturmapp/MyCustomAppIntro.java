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
        addSlide(AppIntroFragment.newInstance("Stuttgart TV Tower Monitoring App", "Structural Health Monitoring of Stuttgart TV Tower. \n By continuing, you agree to the privacy policy",
                R.mipmap.ic_launcher_tvtower_round, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        //add slide2
        addSlide(AppIntroFragment.newInstance("Real Time Position Times Series", "Press the Real Time Position Times Series button to see the real time position time series of the Tower",
                R.mipmap.ic_launcher_tvtower_round, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        //add slide3
        addSlide(AppIntroFragment.newInstance("Real Time Graph of Tower Motion", "Press the Real Time Graph of Tower Motion button to see the real time graph of the Tower",
                R.mipmap.ic_launcher_tvtower_round, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        addSlide(AppIntroFragment.newInstance("Position Time Series", "Press the Position Time Series button to see the position time series of the Tower \n \n Enter the start time, end time, and the desired date and finally hit Update Graph to visualize the graph. More data would make an app a little unresponsive",
                R.mipmap.ic_launcher_tvtower_round, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        addSlide(AppIntroFragment.newInstance("Graph of Tower Motion", "Press the Graph of Tower Motion button to see the desired time graph of the Tower \n \n Enter the start time, end time, and the desired date and finally hit Update Graph to visualize the graph. More data would make an app a little unresponsive",
                R.drawable.tutorial, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        addSlide(AppIntroFragment.newInstance("Enjoy the Monitoring", " ",
                R.mipmap.ic_launcher_tvtower_round, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        showStatusBar(true);
        setScrollDurationFactor(1);

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