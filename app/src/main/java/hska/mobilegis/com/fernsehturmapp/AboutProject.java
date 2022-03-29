package hska.mobilegis.com.fernsehturmapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutProject extends AppCompatActivity {

    Button readmore;
    TextView tv_aboutProject;
    private String text;
    private String text_key ="key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_project);

        tv_aboutProject = findViewById(R.id.tv_aboutProject);
        readmore=(Button)findViewById(R.id.btn_readMore);
        readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tv_aboutProject.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        text = savedInstanceState.getString(text_key);
        tv_aboutProject.setText(text);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        text = tv_aboutProject.getText().toString();
        outState.putString(text_key, text);
    }
}
