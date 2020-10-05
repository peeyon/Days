package project.days;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DiaryContentActivity extends AppCompatActivity {
    private EditText mDiaryContent;
    private Button mContentViewBack;
    private Button mContentEdit;
    private Button mContentDelete;
    private Button mContentAddPhotos;
    private Button mContentAddVideos;
    private FirebaseAuth fAuth;

    public DiaryContentActivity() {
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_content);

        mContentViewBack = findViewById(R.id.content_view_back_icon);
        mContentEdit     = findViewById(R.id.content_edit_icon);
        mContentDelete   = findViewById(R.id.content_delete_icon);
        mDiaryContent    = findViewById(R.id.diary_content_text);
        mContentAddPhotos= findViewById(R.id.content_add_photos);
        mContentAddVideos= findViewById(R.id.content_add_videos);


        fAuth = FirebaseAuth.getInstance();

        mContentEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v){
                                                String diarycontent = mDiaryContent.getText().toString().trim();

                                                if(TextUtils.isEmpty(diarycontent)){
                                                    mDiaryContent.setError("Enter the content.");
                                                    return;
                                                }
                                                if(diarycontent.length()<1000000000){
                                                    mDiaryContent.setError("Content is exceded");

                                                }
                                            }
                                        }

        );

        {

        }


    }
}