package wi.ki.rd545cmd1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShowResultUiActivity extends AppCompatActivity {
    TextView WeightText, HeightText, BMIText, MuscleMassText, EpPulseText, BoneMassText, BodyWaterText, BodyFatText,
            VisceralFatText, BasalMetabolicRateText,
            MuscleMassTrunkText, MuscleMassLeftArmText, MuscleMassRightArmText, MuscleMassLeftFootText, MuscleMassRightFootText,
            BodyFatTrunkText, BodyFatLeftArmText, BodyFatRightArmText, BodyFatLeftFootText, BodyFatRightFootText;
    String  Height, Weight, BMI, MuscleMass, BoneMass, BodyWater, BodyFat,BoneMassJudgement,MuscleMassScore,
            VisceralFat, BasalMetabolicRate, MuscleMassTrunk, MuscleMassLeftArm,
            MuscleMassRightArm, MuscleMassLeftFoot, MuscleMassRightFoot,
            BodyFatTrunk, BodyFatLeftArm, BodyFatRightArm, BodyFatLeftFoot, BodyFatRightFoot;
    int Gender,EpPulse;
    ImageView BodySilhouette;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_result_ui);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WeightText              = findViewById(R.id.Weight);
        HeightText              = findViewById(R.id.Height);
        BMIText                 = findViewById(R.id.BMI);
        MuscleMassText          = findViewById(R.id.MuscleMass);
        EpPulseText             = findViewById(R.id.EpPulse);
        BoneMassText            = findViewById(R.id.BoneMass);
        BodyWaterText           = findViewById(R.id.BodyWater);
        BodyFatText             = findViewById(R.id.BodyFat);
        VisceralFatText         = findViewById(R.id.VisceralFat);
        BasalMetabolicRateText  = findViewById(R.id.BasalMetabolicRate);
        MuscleMassTrunkText     = findViewById(R.id.MuscleMassTrunk);
        MuscleMassLeftArmText   = findViewById(R.id.MuscleMassLeftArm);
        MuscleMassRightArmText  = findViewById(R.id.MuscleMassRightArm);
        MuscleMassLeftFootText  = findViewById(R.id.MuscleMassLeftFoot);
        MuscleMassRightFootText = findViewById(R.id.MuscleMassRightFoot);
        BodyFatTrunkText        = findViewById(R.id.BodyFatTrunk);
        BodyFatLeftArmText      = findViewById(R.id.BodyFatLeftArm);
        BodyFatRightArmText     = findViewById(R.id.BodyFatRightArm);
        BodyFatLeftFootText     = findViewById(R.id.BodyFatLeftFoot);
        BodyFatRightFootText    = findViewById(R.id.BodyFatRightFoot);
        BodySilhouette          = findViewById(R.id.body_silhouette);

        Intent intent = getIntent();
        Log.i("wiki1", "Gender=" + intent.getIntExtra("Gender",3));
        Log.i("wiki1", "Weight=" + intent.getStringExtra("Weight"));
        Log.i("wiki1", "Height=" + intent.getStringExtra("Height"));

        Gender              = intent.getIntExtra("Gender",3);

        if(Gender==1)
        {
            BodySilhouette.setImageResource(R.drawable.female1);
        }
        else
        {
            BodySilhouette.setImageResource(R.drawable.male1);
        }

        Weight              = intent.getStringExtra("Weight"                        );
        Height              = intent.getStringExtra("Height"                        );
        BMI                 = intent.getStringExtra("BMI"                           );
        MuscleMass          = intent.getStringExtra("MuscleMass"                    );
        EpPulse             = intent.getIntExtra   ("EpPulse",0          );
        BoneMass            = intent.getStringExtra("BoneMass"                      );
        BodyWater           = intent.getStringExtra("BodyWater"                     );
        BodyFat             = intent.getStringExtra("BodyFat"                       );
        VisceralFat         = intent.getStringExtra("VisceralFat"                   );
        BasalMetabolicRate  = intent.getStringExtra("BasalMetabolicRate"            );
        MuscleMassTrunk     = intent.getStringExtra("MuscleMassTrunk"               );
        MuscleMassLeftArm   = intent.getStringExtra("MuscleMassLeftArm"             );
        MuscleMassRightArm  = intent.getStringExtra("MuscleMassRightArm"            );
        MuscleMassLeftFoot  = intent.getStringExtra("MuscleMassLeftFoot"            );
        MuscleMassRightFoot = intent.getStringExtra("MuscleMassRightFoot"           );
        BodyFatTrunk        = intent.getStringExtra("BodyFatTrunk"                  );
        BodyFatLeftArm      = intent.getStringExtra("BodyFatLeftArm"                );
        BodyFatRightArm     = intent.getStringExtra("BodyFatRightArm"               );
        BodyFatLeftFoot     = intent.getStringExtra("BodyFatLeftFoot"               );
        BodyFatRightFoot    = intent.getStringExtra("BodyFatRightFoot"              );


        WeightText              .setText("Weight\n"                +Weight              +"kg");
        HeightText              .setText("Height\n"                +Height              +"cm");
        BMIText                 .setText("BMI\n"                   +BMI                 +"kg/㎡");
        MuscleMassText          .setText("MuscleMass\n"            +MuscleMass          +"kg");
        EpPulseText             .setText("HeartRate\n"             +EpPulse             +"bpm");
        BoneMassText            .setText("Bone\n"                  +BoneMass            +"kg");
        BodyWaterText           .setText("BodyWater\n"             +BodyWater           +"%");
        BodyFatText             .setText("BodyFat\n"               +BodyFat             +"%");
        VisceralFatText         .setText("VisceralFat\n"           +VisceralFat         +"");
        BasalMetabolicRateText  .setText("MetabolicRate\n"         +BasalMetabolicRate  +"kcal");
        MuscleMassTrunkText     .setText("Muscle\nTrunk\n"         +MuscleMassTrunk     +"kg");
        MuscleMassLeftArmText   .setText("Muscle\nLeftArm\n"       +MuscleMassLeftArm   +"kg");
        MuscleMassRightArmText  .setText("Muscle\nRightArm\n"      +MuscleMassRightArm  +"kg");
        MuscleMassLeftFootText  .setText("Muscle\nLeftFoot\n"      +MuscleMassLeftFoot  +"kg");
        MuscleMassRightFootText .setText("Muscle\nRightFoot\n"     +MuscleMassRightFoot +"kg");
        BodyFatTrunkText        .setText("BodyFat\nTrunk\n"        +BodyFatTrunk        +"%");
        BodyFatLeftArmText      .setText("BodyFat\nLeftArm\n"      +BodyFatLeftArm      +"%");
        BodyFatRightArmText     .setText("BodyFat\nRightArm\n"     +BodyFatRightArm     +"%");
        BodyFatLeftFootText     .setText("BodyFat\nLeftFoot\n"     +BodyFatLeftFoot     +"%");
        BodyFatRightFootText    .setText("BodyFat\nRightFoot\n"    +BodyFatRightFoot    +"%");
    }
}