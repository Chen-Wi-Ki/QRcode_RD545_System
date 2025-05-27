package wi.ki.rd545cmd1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //Button TestStartBtn = findViewById(R.id.TestStartBtn);
    RadioGroup GenderRadioGroup;
    EditText EditTextBirthday,EditTextHeight,EditTextClothesWeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String model = Build.MODEL; // 手機型號
        String manufacturer = Build.MANUFACTURER; // 手機品牌
        String androidVersion = Build.VERSION.RELEASE; // Android 系統版本
        int sdkVersion = Build.VERSION.SDK_INT; // SDK 版本號
        Log.i("wiki1","廠牌:"+manufacturer+"/型號:"+model+"/sdkVersion:"+sdkVersion+"/androidVersion:"+androidVersion);

        GenderRadioGroup = findViewById(R.id.Gender1Select);
        GenderRadioGroup.check(R.id.MaleRadio);// Default--Male
        EditTextBirthday = findViewById(R.id.EditTextBirthday);
        EditTextHeight = findViewById(R.id.EditTextHeight);
        EditTextClothesWeight = findViewById(R.id.EditTextClothesWeight);

    }
    public void TestStartEvent(View v)
    {
        boolean CheckData=true;

        String inputBirthdayDate = EditTextBirthday.getText().toString();
        // 檢查格式 (例如 yyyy-MM-dd)
        String BirthdayPattern = "\\d{4}-\\d{2}-\\d{2}";

        if (inputBirthdayDate.matches(BirthdayPattern)) {
            //Toast.makeText(this, "日期格式正確", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Birthday format is yyyy-mm-dd", Toast.LENGTH_SHORT).show();
            CheckData=false;
        }

        String inputHeightDate = EditTextHeight.getText().toString();
        // 檢查格式 (例如 yyyy-MM-dd)
        String HeightPattern = "\\d+\\.\\d{1}";

        if (inputHeightDate.matches(HeightPattern)) {
            //Toast.makeText(this, "格式正確", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Height(cm) format example:175.7", Toast.LENGTH_SHORT).show();
            CheckData=false;
        }

        String inputTareDate = EditTextClothesWeight.getText().toString();
        // 檢查格式 (例如 yyyy-MM-dd)
        String TarePattern = "\\d+\\.\\d{1}";

        if (inputTareDate.matches(TarePattern)) {
            //Toast.makeText(this, "格式正確", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "請輸入正確的扣重(kg)格式,例:0.4)", Toast.LENGTH_SHORT).show();
            CheckData=false;
        }

        int checkedId = GenderRadioGroup.getCheckedRadioButtonId();
        Log.i("wiki1","checkedId="+checkedId);
        int gender;
        if(checkedId==R.id.FemaleRadio)
        {
            Toast.makeText(this, "OK, Lady!", Toast.LENGTH_SHORT).show();
            gender=1;
        }
        else
        {
            Toast.makeText(this, "OK, Gentleman!", Toast.LENGTH_SHORT).show();
            gender=0;
        }

        if(CheckData)
        {
            Toast.makeText(this, "Measurement will start soon...", Toast.LENGTH_SHORT).show();
            //Run RD545 API
            Intent intent = new Intent(this,MeasurementActivity.class);
            intent.putExtra("Birthday",inputBirthdayDate);
            intent.putExtra("Gender",gender);
            intent.putExtra("Height",inputHeightDate);
            intent.putExtra("ClothesWeight",inputTareDate);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please correct it and continue...", Toast.LENGTH_SHORT).show();
        }
    }
}