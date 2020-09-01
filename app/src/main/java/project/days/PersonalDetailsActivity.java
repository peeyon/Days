package project.days;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText NameET, NickNameET;
    private TextView MaleT, FemaleT, OtherT, DateT;
    private Button NextButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    boolean maleb,femaleb, otherb;
    String name,nickname,gender,date;
    int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        MaleT = (TextView) findViewById(R.id.male_select);
        FemaleT = (TextView) findViewById(R.id.female_select);
        OtherT = (TextView) findViewById(R.id.other_select);
        DateT = (TextView) findViewById(R.id.pd_dob);

        DateT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        PersonalDetailsActivity.this,
                        android.R.style.Theme_Material_Light_Dialog,
                        mDateSetListener,
                        day,month,year
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1+1;
                day = i2;
                date = day + "/" + month + "/" + year;
                DateT.setText(date);
            }
        };


        MaleT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                MaleT.setTextColor(getResources().getColor(R.color.colorWhite));
                OtherT.setTextColor(getResources().getColor(R.color.grey));
                FemaleT.setTextColor(getResources().getColor(R.color.grey));
                FemaleT.setBackgroundColor(getResources().getColor(R.color.colorWhite));

                OtherT.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                maleb = true;
                femaleb = false;
                otherb = false;
            }
        });
        FemaleT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                FemaleT.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                FemaleT.setTextColor(getResources().getColor(R.color.colorWhite));
                MaleT.setTextColor(getResources().getColor(R.color.grey));
                OtherT.setTextColor(getResources().getColor(R.color.grey));
                OtherT.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                maleb = false;
                femaleb = true;
                otherb = false;

            }
        });
        OtherT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleT.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                FemaleT.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                OtherT.setTextColor(getResources().getColor(R.color.colorWhite));
                MaleT.setTextColor(getResources().getColor(R.color.grey));
                FemaleT.setTextColor(getResources().getColor(R.color.grey));
                OtherT.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                maleb = false;
                femaleb = false;
                otherb = true;

            }
        });

        NameET = (EditText) findViewById(R.id.pd_name);
        NickNameET = (EditText) findViewById(R.id.pd_nickname);
        NextButton = (Button) findViewById(R.id.nextBtn);

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(maleb || femaleb || otherb)
                {
                    if (maleb)
                        gender = "Male";
                    if(femaleb)
                        gender = "Female";
                    if(otherb)
                        gender = "Other";
                }
            }
            // Add validation here
            // I have added the validation for gender field alone now, since it is not familiar
        });

    }
}