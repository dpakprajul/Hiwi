package hska.mobilegis.com.fernsehturmapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class Contactus extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactus);



        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_tvtower_round)
                .setDescription(getString(R.string.contactus_descc))


                .addItem(new Element().setTitle("Version 2.0.0 Beta"))
//                .addItem(new Element().setIconDrawable(R.drawable.ic_baseline_bug_report_24).setTitle("Report bug in Contact us section"))
                .addGroup(getString(R.string.contact_with_us))
                .addEmail("reiner.jaeger@web.de")
                .addWebsite("http://goca.info/")
                .addYoutube("UC94jGaTYn9MOXWLfQjPE3RQ")//Enter your youtube link here (replace with my channel link)
                .addItem(debug())
//                .addPlayStore("com.example.yourprojectname")   //Replace all this with your package name
//                .addInstagram("jarves.usaram")    //Your instagram id
                .addItem(createCopyright())

                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format(getString(R.string.copyright), Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Contactus.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
    private Element debug()
    {
        Element debug = new Element();

        debug.setTitle(getString(R.string.debug));
        debug.setIconDrawable(R.drawable.ic_baseline_bug_report_24);
        // copyright.setIcon(R.mipmap.ic_launcher);
        debug.setGravity(Gravity.LEFT);
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","naznin.2013@yahoo.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        return debug;
    }
}