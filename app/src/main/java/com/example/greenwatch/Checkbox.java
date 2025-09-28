package com.example.greenwatch;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Checkbox extends AppCompatActivity {


    private CheckBox cseCheck, eeeCheck, meCheck;
    private Button submitButton;

    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkbox);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cseCheck = (CheckBox) findViewById(R.id.cCSE);
        eeeCheck = (CheckBox)  findViewById(R.id.cEEE);
        meCheck = (CheckBox)  findViewById(R.id.cME);
        submitButton = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.resText);

//        submitButton.setOnClickListener(v -> {
//            StringBuilder result = new StringBuilder("Selected Departments:\n");
//
//            if (cseCheck.isChecked()) {
//                result.append("Computer Science and Engineering\n");
//            }
//            if (eeeCheck.isChecked()) {
//                result.append("Electrical and Electronics Engineering\n");
//            }
//            if (meCheck.isChecked()) {
//                result.append("Mechanical Engineering\n");
//            }
//
//            if (result.toString().equals("Selected Departments:\n")) {
//                result.append("None");
//            }
//
//            resultTextView.setText(result.toString());
//        });

        submitButton.setOnClickListener(v -> {
            String result = "Selected Departments:\n";
            boolean selected = false;

            if (cseCheck.isChecked()) {
                result = result + "Computer Science and Engineering\n";
                selected = true;
            }
            if (eeeCheck.isChecked()) {
                result = result + "Electrical and Electronics Engineering\n";
                selected = true;
            }
            if (meCheck.isChecked()) {
                result = result + "Mechanical Engineering\n";
                selected = true;
            }
            if(!selected){
                result = "No Department Selected";
            }
            resultTextView.setText(result);

            Intent intent = new Intent(Checkbox.this, MainActivity.class);
            startActivity(intent);

        });

    }




}