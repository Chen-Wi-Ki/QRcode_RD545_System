package wi.ki.rd545cmd1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import jp.co.tanita.comm.ble.TNTBLEManager;
import jp.co.tanita.comm.ble.TNTBLEPeripheral;
import jp.co.tanita.comm.ble.TNTDeviceInformation;
import jp.co.tanita.comm.ble.TNTDeviceStatus;
import jp.co.tanita.comm.ble.TNTDeviceType;
import jp.co.tanita.comm.ble.TNTMeasurementInformation;
import jp.co.tanita.comm.ble.TNTUserInformation;

public class MeasurementActivity extends AppCompatActivity {

    TNTBLEManager _TNTBLEManager;
    TNTBLEManager.TNTBLEManagerListener _TNTBLEManagerListener;
    TNTBLEPeripheral _TNTBLEPeripheral;
    TNTBLEPeripheral.TNTBLEPeripheralListener _TNTBLEPeripheralListener;
    TNTUserInformation _TNTUserInformation;
    TNTMeasurementInformation _TNTMeasurementInformation;

    final String SERVER_URL = "http://wiciar.com/bmi/testdata";
    //This test SERVER_URL,You should change your url.

    boolean NeedSaveUUID=true;
    private static final String FILE_NAME = "MAC1.txt";
    String MAC1 = "Null";
    String FileData = "";
    int _Count;
    UUID _UUID;

    int Gender;
    String Birthday,UserName="None",Height,ClothesWeight;
    TextView UserText;
    Button UuidSaveBtn,SaveRomButton,SetDataButton,UpdateCloudButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_measurement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UserText = findViewById(R.id.UserText);
        UuidSaveBtn = findViewById(R.id.SaveUUIDButton);
        UuidSaveBtn.setVisibility(View.GONE);
        SaveRomButton = findViewById(R.id.SaveRomButton);
        SaveRomButton.setVisibility(View.GONE);
        SetDataButton = findViewById(R.id.SetDataButton);
        SetDataButton.setVisibility(View.GONE);
        UpdateCloudButton = findViewById(R.id.UpdateCloudButton);

        Intent intent = getIntent();
        UserName        = intent.getStringExtra("Phone");
        Birthday        = intent.getStringExtra("Birthday");
        Gender          = intent.getIntExtra("Gender",1);
        Height          = intent.getStringExtra("Height");
        ClothesWeight   = intent.getStringExtra("ClothesWeight");

        //UserText.setText(Birthday+"|"+Gender+"|"+Height+"|"+ClothesWeight);

        _TNTBLEManagerListener = new TNTBLEManager.TNTBLEManagerListener() {
            @Override
            public void onTNTBLEManagerStateUpdated(int i) {
                Log.i("wiki1","onTNTBLEManagerStateUpdated runned...");
            }

            @Override
            public void onTNTBLEPeripheralConnected(TNTBLEManager tntbleManager, TNTBLEPeripheral tntblePeripheral, TNTDeviceInformation tntDeviceInformation) {
                Log.i("wiki1","onTNTBLEPeripheralConnected runned...");
                // 設置 Peripheral 監聽器
                tntblePeripheral.setTNTBLEPeripheralListener(_TNTBLEPeripheralListener);
                // 保存完成連線的 Peripheral
                _TNTBLEPeripheral = tntblePeripheral;
                if(MAC1.startsWith("Null"))
                {
                    Log.i("wiki1","MAC Recode runned...");
                    MAC1 = tntDeviceInformation.getBluetoothAddress();
                }
                else
                {
                    Log.i("wiki1","onTNTBLEPeripheralConnected的else runned...");
                    User0MsgGet2();
                }
            }

            @Override
            public void onTNTBLEPeripheralDisconnected(TNTBLEManager tntbleManager, TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onTNTBLEPeripheralDisconnected runned...");
            }

            @Override
            public void onTNTBLEPeripheralConnectionFailed(TNTBLEManager tntbleManager, TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onTNTBLEPeripheralConnectionFailed runned...");
            }

            @Override
            public void onTNTBLEPeripheralConnectionIgnored(TNTBLEManager tntbleManager, int i) {
                Log.i("wiki1","onTNTBLEPeripheralConnectionIgnored runned...");
            }
        };
        _TNTBLEPeripheralListener =new TNTBLEPeripheral.TNTBLEPeripheralListener() {
            @Override
            public void onDisconnectionRequested(TNTBLEPeripheral tntblePeripheral, int i, int i1) {
                Log.i("wiki1","onDisconnectionRequested runned...");
            }

            @Override
            public void onUUIDSaved(TNTBLEPeripheral tntblePeripheral, int i, int i1, int i2) {
                Log.i("wiki1","onUUIDSaved runned...");
            }
            @Override
            public void onRssiUpdated(TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onRssiUpdated runned...");
            }

            @Override
            public void onRequestFailed(TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onRequestFailed runned...error code="+i);
            }
            @Override
            public void onUserInformationRetrieved(TNTBLEPeripheral tntblePeripheral, TNTUserInformation tntUserInformation, int i, int i1) {
                Log.i("wiki1","onUserInformationRetrieved runned...");
                _TNTUserInformation = tntUserInformation;
                User0MsgSet2();
            }

            @Override
            public void onUserInformationSaved(TNTBLEPeripheral tntblePeripheral, TNTUserInformation tntUserInformation, int deviceStatus, int error) {
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    UserText.setText("Name:"+tntUserInformation.getNickname()+"\n"+
                            "Gender:"+tntUserInformation.getGender()+"(0=MALE;1=FEMALE)\n"+
                            "Birth:"+tntUserInformation.getDateOfBirth()+"\n"+
                            "Height:"+tntUserInformation.getHeight()+"cm\n"+
                            "Tare:"+tntUserInformation.getTare()+"\n"+
                            "Build Date:"+tntUserInformation.getDate()+"\n"
                    );
                    if(NeedSaveUUID)
                    {
                        _TNTBLEPeripheral.saveUUID(_UUID);
                        SaveStorageFile(_UUID,MAC1);
                    }
                    TestStartEvent();
                }
            }

            @Override
            public void onMeasurementFinished(TNTBLEPeripheral tntblePeripheral, int deviceStatus, int error) {
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    _TNTBLEPeripheral.retrieveMeasurementCount();
                }
                Log.i("wiki1","onMeasurementFinished runned...deviceStatus="+deviceStatus);
            }

            @Override
            public void onMeasurementCountRetrieved(TNTBLEPeripheral tntblePeripheral, int count, int deviceStatus, int error) {
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    _Count = count;
                    UserText.setText("Measurement Over!!!\n");

                    if(count<=1)
                    {
                        _TNTBLEPeripheral.retrieveMeasurementInformation(_Count);
                    }
                    else
                    {
                        _Count=_Count-1;
                        _TNTBLEPeripheral.retrieveMeasurementInformation(_Count);
                    }
                }
                Log.i("wiki1","onMeasurementCountRetrieved runned..._Count="+_Count);

            }

            @Override
            public void onMeasurementInformationRetrieved(TNTBLEPeripheral tntblePeripheral, int number, TNTMeasurementInformation tntMeasurementInformation, int deviceStatus, int error) {
                Log.i("wiki1","onMeasurementInformationRetrieved runned...deviceStatus="+deviceStatus);
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    _TNTMeasurementInformation = tntMeasurementInformation;
                    UserText.setText("--Measurement Message--"+"\n"+
                            "Weight:"+tntMeasurementInformation.getWeight()+":(kg)\n"+
                            "ActivityLevel:"+tntMeasurementInformation.getActivityLevel()+":(0~3)\n"+
                            "MuscleQuality:"+tntMeasurementInformation.getMuscleQuality()+":(0~100)\n"+
                            "AthleteIndex:"+tntMeasurementInformation.getAthleteIndex()+":(50~100)\n"+
                            "MBA:"+tntMeasurementInformation.getAthleteJudgement()+":(1~4)\n"+
                            "BasalMetabolicRate:"+tntMeasurementInformation.getBasalMetabolicRate()+":(kcal)\n"+
                            "GetTheDailyCaloricIntake:"+tntMeasurementInformation.getDailyCaloricIntake()+":(kcal)\n"+
                            "BasalMetabolicRateJudgement:"+tntMeasurementInformation.getBasalMetabolicRateJudgement()+":(1~16)\n"+
                            "VisceralFat:"+tntMeasurementInformation.getVisceralFat()+":(1~59)\n"+
                            "VisceralFatJudgement:"+tntMeasurementInformation.getVisceralFatJudgement()+":(1~3)\n"+
                            "MetabolicAge:"+tntMeasurementInformation.getMetabolicAge()+":(18~90)\n"+
                            "BoneMass:"+tntMeasurementInformation.getBoneMass()+":(0.30~7.06)\n"+
                            "BoneMassJudgement:"+tntMeasurementInformation.getBoneMassJudgement()+":(1~4)\n"+
                            "BodyWater:"+tntMeasurementInformation.getBodyWater()+":(%)\n"+
                            "Bmi:"+tntMeasurementInformation.getBodyMassIndex()+":(kg/㎡)\n"+
                            "BmiJudgement:"+tntMeasurementInformation.getBodyMassIndexJudgement()+":(1~4)\n"+
                            "EpPulse:"+tntMeasurementInformation.getEpPulse()+":(50-200)\n"+

                            "BodyFat:"+tntMeasurementInformation.getBodyFat()+":(%)\n"+
                            "BodyFatJudgement:"+tntMeasurementInformation.getBodyMassIndexJudgement()+":(1~4)\n"+
                            "MuscleMass:"+tntMeasurementInformation.getMuscleMass()+":(kg)\n"+
                            "MuscleJudgement:"+tntMeasurementInformation.getMuscleMassJudgement()+":(1~3)\n"+
                            "MuscleMassScore:"+tntMeasurementInformation.getMuscleMassScore()+":(-4~+4)\n"+
                            "MuscleQuality:"+tntMeasurementInformation.getMuscleQuality()+":(0~100)\n"+
                            "MuscleQualityJudgement:"+tntMeasurementInformation.getMuscleQualityJudgement()+":(1~9)\n"+

                            "BodyFat(Trunk):"+tntMeasurementInformation.getBodyFatTrunk()+":(5~75%)\n"+
                            "BodyFatJudgement(Trunk):"+tntMeasurementInformation.getBodyFatJudgementTrunk()+":(1~5)\n"+
                            "MuscleMass(Trunk):"+tntMeasurementInformation.getMuscleMassTrunk()+":(kg)\n"+
                            "MuscleMassJudgement(Trunk):"+tntMeasurementInformation.getMuscleMassJudgementTrunk()+":(1~3)\n"+
                            "MuscleMassScore(Trunk):"+tntMeasurementInformation.getMuscleMassScoreTrunk()+":(-4~4)\n"+

                            "BodyFat(LeftArm):"+tntMeasurementInformation.getBodyFatLeftArm()+":(5~75%)\n"+
                            "BodyFatJudgement(LeftArm):"+tntMeasurementInformation.getBodyFatJudgementLeftArm()+":(1~5)\n"+
                            "MuscleMass(LeftArm):"+tntMeasurementInformation.getMuscleMassLeftArm()+":(kg)\n"+
                            "MuscleMassJudgement (LeftArm):"+tntMeasurementInformation.getMuscleMassJudgementLeftArm()+":(1~3)\n"+
                            "MuscleMassScore(LeftArm):"+tntMeasurementInformation.getMuscleMassScoreLeftArm()+":(-4~4)\n"+
                            "MuscleQuality(LeftArm):"+tntMeasurementInformation.getMuscleQualityLeftArm()+":(0~100)\n"+
                            "MuscleQualityJudgement (LeftArm):"+tntMeasurementInformation.getMuscleQualityJudgementLeftArm()+":(1~9)\n"+

                            "BodyFat(RightArm):"+tntMeasurementInformation.getBodyFatRightArm()+":(5~75%)\n"+
                            "BodyFatJudgement(RightArm):"+tntMeasurementInformation.getBodyFatJudgementRightArm()+":(1~5)\n"+
                            "MuscleMass(RightArm):"+tntMeasurementInformation.getMuscleMassRightArm()+":(kg)\n"+
                            "MuscleMassJudgement(RightArm):"+tntMeasurementInformation.getMuscleMassJudgementRightArm()+":(1~3)\n"+
                            "MuscleMassScore(RightArm):"+tntMeasurementInformation.getMuscleMassScoreRightArm()+":(-4~4)\n"+
                            "MuscleQuality(RightArm):"+tntMeasurementInformation.getMuscleQualityRightArm()+":(0~100)\n"+
                            "MuscleQualityJudgement(RightArm):"+tntMeasurementInformation.getMuscleQualityJudgementRightArm()+":(1~9)\n"+

                            "BodyFat(LeftFoot):"+tntMeasurementInformation.getBodyFatLeftFoot()+":(5~75%)\n"+
                            "BodyFatJudgement(LeftFoot):"+tntMeasurementInformation.getBodyFatJudgementLeftFoot()+":(1~5)\n"+
                            "MuscleMass(LeftFoot):"+tntMeasurementInformation.getMuscleMassLeftFoot()+":(kg)\n"+
                            "MuscleMassJudgement(LeftFoot):"+tntMeasurementInformation.getMuscleMassJudgementLeftFoot()+":(1~3)\n"+
                            "MuscleMassScore(LeftFoot):"+tntMeasurementInformation.getMuscleMassScoreLeftFoot()+":(-4~4)\n"+
                            "MuscleQuality(LeftFoot):"+tntMeasurementInformation.getMuscleQualityLeftFoot()+":(0~100)\n"+
                            "MuscleQualityJudgement(LeftFoot):"+tntMeasurementInformation.getMuscleQualityJudgementLeftFoot()+":(1~9)\n"+

                            "BodyFat(RightFoot):"+tntMeasurementInformation.getBodyFatRightFoot()+":(5~75%)\n"+
                            "BodyFatJudgement(RightFoot):"+tntMeasurementInformation.getBodyFatJudgementRightFoot()+":(1~5)\n"+
                            "MuscleMass(RightFoot):"+tntMeasurementInformation.getMuscleMassRightFoot()+":(kg)\n"+
                            "MuscleMassJudgement(RightFoot):"+tntMeasurementInformation.getMuscleMassJudgementRightFoot()+":(1~3)\n"+
                            "MuscleMassScore(RightFoot):"+tntMeasurementInformation.getMuscleMassScoreRightFoot()+":(-4~4)\n"+
                            "MuscleQuality(RightFoot):"+tntMeasurementInformation.getMuscleQualityRightFoot()+":(0~100)\n"+
                            "MuscleQualityJudgement(RightFoot):"+tntMeasurementInformation.getMuscleQualityJudgementRightFoot()+"(1~9)\n"+

                            "ImpedanceArms50kHzR:"+tntMeasurementInformation.getImpedanceArms50kHzR()+":(Ω)\n"+
                            "ImpedanceArms50kHzX:"+tntMeasurementInformation.getImpedanceArms50kHzX()+":(Ω)\n"+
                            "ImpedanceArms6.25kHzR:"+tntMeasurementInformation.getImpedanceArms6_25kHzR()+":(Ω)\n"+
                            "ImpedanceArms6.25kHzX:"+tntMeasurementInformation.getImpedanceArms6_25kHzX()+":(Ω)\n"+

                            "ImpedanceLeftArm50kHzR:"+tntMeasurementInformation.getImpedanceLeftArm50kHzR()+":(Ω)\n"+
                            "ImpedanceLeftArm50kHzX:"+tntMeasurementInformation.getImpedanceLeftArm50kHzX()+":(Ω)\n"+
                            "ImpedanceLeftArm6.25kHzR:"+tntMeasurementInformation.getImpedanceLeftArm6_25kHzR()+":(Ω)\n"+
                            "ImpedanceLeftArm6.25kHzX:"+tntMeasurementInformation.getImpedanceLeftArm6_25kHzX()+":(Ω)\n"+

                            "ImpedanceRightArm50kHzR:"+tntMeasurementInformation.getImpedanceRightArm50kHzR()+":(Ω)\n"+
                            "ImpedanceRightArm50kHzX:"+tntMeasurementInformation.getImpedanceRightArm50kHzX()+":(Ω)\n"+
                            "ImpedanceRightArm6.25kHzR:"+tntMeasurementInformation.getImpedanceRightArm6_25kHzR()+":(Ω)\n"+
                            "ImpedanceRightArm6.25kHzX:"+tntMeasurementInformation.getImpedanceRightArm6_25kHzX()+":(Ω)\n"+

                            "ImpedanceFoot50kHzR:"+tntMeasurementInformation.getImpedanceFoot50kHzR()+":(Ω)\n"+
                            "ImpedanceFoot50kHzX:"+tntMeasurementInformation.getImpedanceFoot50kHzX()+":(Ω)\n"+
                            "ImpedanceFoot6.25kHzR:"+tntMeasurementInformation.getImpedanceFoot6_25kHzR()+":(Ω)\n"+
                            "ImpedanceFoot6.25kHzX:"+tntMeasurementInformation.getImpedanceFoot6_25kHzX()+":(Ω)\n"+

                            "ImpedanceLeftFoot50kHzR:"+tntMeasurementInformation.getImpedanceLeftFoot50kHzR()+":(Ω)\n"+
                            "ImpedanceLeftFoot50kHzX:"+tntMeasurementInformation.getImpedanceLeftFoot50kHzX()+":(Ω)\n"+
                            "ImpedanceLeftFoot6.25kHzR:"+tntMeasurementInformation.getImpedanceLeftFoot6_25kHzR()+":(Ω)\n"+
                            "ImpedanceLeftFoot6.25kHzX:"+tntMeasurementInformation.getImpedanceLeftFoot6_25kHzX()+":(Ω)\n"+

                            "ImpedanceRightFoot50kHzR:"+tntMeasurementInformation.getImpedanceRightFoot50kHzR()+":(Ω)\n"+
                            "ImpedanceRightFoot50kHzX:"+tntMeasurementInformation.getImpedanceRightFoot50kHzX()+":(Ω)\n"+
                            "ImpedanceRightFoot6.25kHzR:"+tntMeasurementInformation.getImpedanceRightFoot6_25kHzR()+":(Ω)\n"+
                            "ImpedanceRightFoot6.25kHzX:"+tntMeasurementInformation.getImpedanceRightFoot6_25kHzX()+":(Ω)\n"+

                            "ImpedanceLeftBody50kHzR:"+tntMeasurementInformation.getImpedanceLeftBody50kHzR()+":(Ω)\n"+
                            "ImpedanceLeftBody50kHzX:"+tntMeasurementInformation.getImpedanceLeftBody50kHzX()+":(Ω)\n"+
                            "ImpedanceLeftBody6.25kHzR:"+tntMeasurementInformation.getImpedanceLeftBody6_25kHzR()+":(Ω)\n"+
                            "ImpedanceLeftBody6.25kHzX:"+tntMeasurementInformation.getImpedanceLeftBody6_25kHzX()+":(Ω)\n"+

                            "ImpedanceRightBody50kHzR:"+tntMeasurementInformation.getImpedanceRightBody50kHzR()+":(Ω)\n"+
                            "ImpedanceRightBody50kHzX:"+tntMeasurementInformation.getImpedanceRightBody50kHzX()+":(Ω)\n"+
                            "ImpedanceRightBody6.25kHzR:"+tntMeasurementInformation.getImpedanceRightBody6_25kHzR()+":(Ω)\n"+
                            "ImpedanceRightBody6.25kHzX:"+tntMeasurementInformation.getImpedanceRightBody6_25kHzX()+":(Ω)\n"
                    );
                    _TNTBLEPeripheral.disconnect();
                    _TNTBLEManager.destroy();
                }
            }
        };

        CheckPermissions();//BLE Permissions
        LinkDevice();

    }
    public void LinkDevice()
    {
        String temp= LoadStorageFile();

        if(temp.startsWith("第一次連線"))
        {
            UserText.setText(temp);
            //Toast.makeText(this, "設備連接...", Toast.LENGTH_LONG).show();
            _TNTBLEManager = new TNTBLEManager(this);
            _TNTBLEManager.setTNTBLEManagerListener(_TNTBLEManagerListener);
            int state = _TNTBLEManager.getState();
            _UUID = _TNTBLEManager.createApplicationUUID();
            if (TNTBLEManager.TNTBLEManagerState.READY == state) {
                // 連線體脂機 type 為 BODY_COMPOSITION_MONITOR
                _TNTBLEManager.connect(TNTDeviceType.BODY_COMPOSITION_MONITOR);
            }
            Log.i("wiki1","Device Link(Over)..."+_TNTBLEManager.createApplicationUUID());
            UuidSaveBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            NeedSaveUUID = false;
            MAC1 = temp.split("\n")[2];
            String UUID1 = temp.split("\n")[3];
            _UUID=UUID.fromString(UUID1);
            Log.i("wiki1","Device Link...MAC:"+MAC1+"/UUID:"+UUID1);
            //MessageText.setText(MAC1 +"\n"+UUID1);
            _TNTBLEManager = new TNTBLEManager(this);
            _TNTBLEManager.setTNTBLEManagerListener(_TNTBLEManagerListener);
            int state = _TNTBLEManager.getState();
            //_UUID = _TNTBLEManager.createApplicationUUID();
            if (TNTBLEManager.TNTBLEManagerState.READY == state) {
                // 連線體脂機 type 為 BODY_COMPOSITION_MONITOR
                UserText.setText("RD-545 wake up!!!");
                _TNTBLEManager.connect(MAC1,_UUID,TNTDeviceType.BODY_COMPOSITION_MONITOR,false);
            }
            Log.i("wiki1","Device Link(Over)...MAC:"+MAC1+"/UUID:"+UUID1);
        }
    }
    public void User0MsgGet(View v)
    {
        Log.i("wiki1","Run User0MsgGet...");
        //Toast.makeText(this, "執行讀取使用者資訊...", Toast.LENGTH_LONG).show();
        _TNTBLEPeripheral.retrieveUserInformation(1);
        SaveRomButton.setVisibility(View.VISIBLE);
        Log.i("wiki1","Run User0MsgGet...(Over)");
    }
    public void User0MsgGet2()
    {
        Log.i("wiki1","Run User0MsgGet...");
        //Toast.makeText(this, "執行讀取使用者資訊...", Toast.LENGTH_LONG).show();
        _TNTBLEPeripheral.retrieveUserInformation(1);
        SaveRomButton.setVisibility(View.VISIBLE);
        Log.i("wiki1","Run User0MsgGet...(Over)");
    }
    public void User0MsgSet2()
    {
        Log.i("wiki1","Run User0MsgSet...");
        //Toast.makeText(this, "執行讀取使用者資訊...", Toast.LENGTH_LONG).show();
        _TNTUserInformation.setNickname("Guest");
        _TNTUserInformation.setActivityLevel(0);
        _TNTUserInformation.setArea(0);
        Log.i("wiki1","Area set is ok!!!");
        Date NowDate = new Date();
        _TNTUserInformation.setDate(NowDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date BirthDate = dateFormat.parse(Birthday);
            _TNTUserInformation.setDateOfBirth(BirthDate);
            Log.i("wiki1","Birthday set is ok!!!");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        _TNTUserInformation.setHeight(Height);
        _TNTUserInformation.setTare(ClothesWeight);
        _TNTUserInformation.setUnit(1);
        _TNTBLEPeripheral.saveUserInformation(_TNTUserInformation, 1);

        Log.i("wiki1","Run User0MsgSet...(Over)");
    }
    public void User0MsgSetForFirstLink(View v)
    {
        Log.i("wiki1","Run User0MsgSetFor1...");
        //Toast.makeText(this, "執行讀取使用者資訊...", Toast.LENGTH_LONG).show();
        _TNTUserInformation.setNickname("Guest");
        _TNTUserInformation.setActivityLevel(0);
        _TNTUserInformation.setArea(0);
        Log.i("wiki1","Area set is ok!!!");
        Date NowDate = new Date();
        _TNTUserInformation.setDate(NowDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date BirthDate = dateFormat.parse(Birthday);
            _TNTUserInformation.setDateOfBirth(BirthDate);
            Log.i("wiki1","Birthday set is ok!!!");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        _TNTUserInformation.setHeight(Height);
        _TNTUserInformation.setTare(ClothesWeight);
        _TNTUserInformation.setUnit(1);
        _TNTBLEPeripheral.saveUserInformation(_TNTUserInformation, 1);

        Log.i("wiki1","Run User0MsgSetFor1...(Over)");
    }
    public void TestStartEvent()
    {
        Log.i("wiki1","Measurement Open...");
        //Toast.makeText(this, "執行量測...", Toast.LENGTH_LONG).show();
        UserText.setText(UserText.getText()+"\nMeasurement Start...");
        _TNTBLEPeripheral.startMeasurement();
        //Log.i("wiki1","詢問設備登入編號1之使用者別名(TNTUserInformation.getNickname):"+_TNTUserInformation.getNickname());
    }
    public void UpdateCloudServerEvent(View v)
    {
        String[] parts = UserText.getText().toString().split("\n");
        if(parts.length>80)
        {
            Toast.makeText(this, "上傳中...", Toast.LENGTH_LONG).show();

            RequestQueue queue = Volley.newRequestQueue(this);
            try {
                JSONObject PostData = new JSONObject();
                PostData.put("TestTime"                         , _TNTUserInformation.getDate());
                PostData.put("MachineNumber"                    , "ST0001");
                PostData.put("UserName"                         , UserName);
                PostData.put("Birthday"                         , Birthday);
                PostData.put("Height"                           , Height);
                PostData.put("Gender"                           , Gender);
                PostData.put("Weight"                           , _TNTMeasurementInformation.getWeight());
                PostData.put("ActivityLevel"                    , _TNTMeasurementInformation.getActivityLevel());
                PostData.put("MuscleQuality"                    , _TNTMeasurementInformation.getMuscleQuality());
                PostData.put("AthleteIndex"                     , _TNTMeasurementInformation.getAthleteIndex());
                PostData.put("MBA"                              , _TNTMeasurementInformation.getMetabolicAge());
                PostData.put("BasalMetabolicRate"               , _TNTMeasurementInformation.getBasalMetabolicRate());
                PostData.put("BasalMetabolicRateJudgement"      , _TNTMeasurementInformation.getBasalMetabolicRateJudgement());
                PostData.put("VisceralFat"                      , _TNTMeasurementInformation.getVisceralFat());
                PostData.put("VisceralFatJudgement"             , _TNTMeasurementInformation.getVisceralFatJudgement());
                PostData.put("MetabolicAge"                     , _TNTMeasurementInformation.getMetabolicAge());
                PostData.put("BoneMass"                         , _TNTMeasurementInformation.getBoneMass());
                PostData.put("BoneMassJudgement"                , _TNTMeasurementInformation.getBoneMassJudgement());
                PostData.put("BodyWater"                        , _TNTMeasurementInformation.getBodyWater());
                PostData.put("BMI"                              , _TNTMeasurementInformation.getBodyMassIndex());
                PostData.put("BmiJudgement"                     , _TNTMeasurementInformation.getBodyMassIndexJudgement());
                PostData.put("EpPulse"                          , _TNTMeasurementInformation.getEpPulse());
                PostData.put("BodyFat"                          , _TNTMeasurementInformation.getBodyFat());
                PostData.put("BodyFatJudgement"                 , _TNTMeasurementInformation.getBodyFatJudgement());
                PostData.put("MuscleMass"                       , _TNTMeasurementInformation.getMuscleMass());
                PostData.put("MuscleMassJudgement"              , _TNTMeasurementInformation.getMuscleMassJudgement());
                PostData.put("MuscleMassScore"                  , _TNTMeasurementInformation.getMuscleMassScore());
                PostData.put("MuscleQuality"                    , _TNTMeasurementInformation.getMuscleQuality());
                PostData.put("MuscleQualityJudgement"           , _TNTMeasurementInformation.getMuscleQualityJudgement());
                PostData.put("BodyFatTrunk"                     , _TNTMeasurementInformation.getBodyFatTrunk());
                PostData.put("BodyFatJudgementTrunk"            , _TNTMeasurementInformation.getBodyFatJudgementTrunk());
                PostData.put("MuscleMassTrunk"                  , _TNTMeasurementInformation.getMuscleMassTrunk());
                PostData.put("MuscleMassJudgementTrunk"         , _TNTMeasurementInformation.getMuscleMassJudgementTrunk());
                PostData.put("MuscleMassScoreTrunk"             , _TNTMeasurementInformation.getMuscleMassScoreTrunk());
                PostData.put("BodyFatLeftArm"                   , _TNTMeasurementInformation.getBodyFatLeftArm());
                PostData.put("BodyFatJudgementLeftArm"          , _TNTMeasurementInformation.getBodyFatJudgementLeftArm());
                PostData.put("MuscleMassLeftArm"                , _TNTMeasurementInformation.getMuscleMassLeftArm());
                PostData.put("MuscleMassJudgementLeftArm"       , _TNTMeasurementInformation.getMuscleMassJudgementLeftArm());
                PostData.put("MuscleMassScoreLeftArm"           , _TNTMeasurementInformation.getMuscleMassScoreLeftArm());
                PostData.put("MuscleQualityLeftArm"             , _TNTMeasurementInformation.getMuscleQualityLeftArm());
                PostData.put("MuscleQualityJudgementLeftArm"    , _TNTMeasurementInformation.getMuscleQualityJudgementLeftArm());
                PostData.put("BodyFatRightArm"                  , _TNTMeasurementInformation.getBodyFatRightArm());
                PostData.put("BodyFatJudgementRightArm"         , _TNTMeasurementInformation.getBodyFatJudgementRightArm());
                PostData.put("MuscleMassRightArm"               , _TNTMeasurementInformation.getMuscleMassRightArm());
                PostData.put("MuscleMassJudgementRightArm"      , _TNTMeasurementInformation.getMuscleMassJudgementRightArm());
                PostData.put("MuscleMassScoreRightArm"          , _TNTMeasurementInformation.getMuscleMassScoreRightArm());
                PostData.put("MuscleQualityRightArm"            , _TNTMeasurementInformation.getMuscleQualityRightArm());
                PostData.put("MuscleQualityJudgementRightArm"   , _TNTMeasurementInformation.getMuscleQualityJudgementRightArm());
                PostData.put("BodyFatLeftFoot"                  , _TNTMeasurementInformation.getBodyFatLeftFoot());
                PostData.put("BodyFatJudgementLeftFoot"         , _TNTMeasurementInformation.getBodyFatJudgementLeftFoot());
                PostData.put("MuscleMassLeftFoot"               , _TNTMeasurementInformation.getMuscleMassLeftFoot());
                PostData.put("MuscleMassJudgementLeftFoot"      , _TNTMeasurementInformation.getMuscleMassJudgementLeftFoot());
                PostData.put("MuscleMassScoreLeftFoot"          , _TNTMeasurementInformation.getMuscleMassScoreLeftFoot());
                PostData.put("MuscleQualityLeftFoot"            , _TNTMeasurementInformation.getMuscleQualityLeftFoot());
                PostData.put("MuscleQualityJudgementLeftFoot"   , _TNTMeasurementInformation.getMuscleQualityJudgementLeftFoot());
                PostData.put("BodyFatRightFoot"                 , _TNTMeasurementInformation.getBodyFatRightFoot());
                PostData.put("BodyFatJudgementRightFoot"        , _TNTMeasurementInformation.getBodyFatJudgementRightFoot());
                PostData.put("MuscleMassRightFoot"              , _TNTMeasurementInformation.getMuscleMassRightFoot());
                PostData.put("MuscleMassJudgementRightFoot"     , _TNTMeasurementInformation.getMuscleMassJudgementRightFoot());
                PostData.put("MuscleMassScoreRightFoot"         , _TNTMeasurementInformation.getMuscleMassScoreRightFoot());
                PostData.put("MuscleQualityRightFoot"           , _TNTMeasurementInformation.getMuscleQualityRightFoot());
                PostData.put("MuscleQualityJudgementRightFoot"  , _TNTMeasurementInformation.getMuscleQualityJudgementRightFoot());
                PostData.put("ImpedanceArms50kHzX"              , _TNTMeasurementInformation.getImpedanceArms50kHzX());
                PostData.put("ImpedanceArms6_25kHzR"            , _TNTMeasurementInformation.getImpedanceArms6_25kHzR());
                PostData.put("ImpedanceArms6_25kHzX"            , _TNTMeasurementInformation.getImpedanceArms6_25kHzX());
                PostData.put("ImpedanceLeftArm50kHzR"           , _TNTMeasurementInformation.getImpedanceLeftArm50kHzR());
                PostData.put("ImpedanceLeftArm50kHzX"           , _TNTMeasurementInformation.getImpedanceLeftArm50kHzX());
                PostData.put("ImpedanceLeftArm6_25kHzR"         , _TNTMeasurementInformation.getImpedanceLeftArm6_25kHzR());
                PostData.put("ImpedanceLeftArm6_25kHzX"         , _TNTMeasurementInformation.getImpedanceLeftArm6_25kHzX());
                PostData.put("ImpedanceRightArm50kHzR"          , _TNTMeasurementInformation.getImpedanceRightArm50kHzR());
                PostData.put("ImpedanceRightArm50kHzX"          , _TNTMeasurementInformation.getImpedanceRightArm50kHzX());
                PostData.put("ImpedanceRightArm6_25kHzR"        , _TNTMeasurementInformation.getImpedanceRightArm6_25kHzR());
                PostData.put("ImpedanceRightArm6_25kHzX"        , _TNTMeasurementInformation.getImpedanceRightArm6_25kHzX());
                PostData.put("ImpedanceFoot50kHzR"              , _TNTMeasurementInformation.getImpedanceFoot50kHzR());
                PostData.put("ImpedanceFoot50kHzX"              , _TNTMeasurementInformation.getImpedanceFoot50kHzX());
                PostData.put("ImpedanceFoot6_25kHzR"            , _TNTMeasurementInformation.getImpedanceFoot6_25kHzR());
                PostData.put("ImpedanceFoot6_25kHzX"            , _TNTMeasurementInformation.getImpedanceFoot6_25kHzX());
                PostData.put("ImpedanceLeftFoot50kHzR"          , _TNTMeasurementInformation.getImpedanceLeftFoot50kHzR());
                PostData.put("ImpedanceLeftFoot50kHzX"          , _TNTMeasurementInformation.getImpedanceLeftFoot50kHzX());
                PostData.put("ImpedanceLeftFoot6_25kHzR"        , _TNTMeasurementInformation.getImpedanceLeftFoot6_25kHzR());
                PostData.put("ImpedanceLeftFoot6_25kHzX"        , _TNTMeasurementInformation.getImpedanceLeftFoot6_25kHzX());
                PostData.put("ImpedanceRightFoot50kHzR"         , _TNTMeasurementInformation.getImpedanceRightFoot50kHzR());
                PostData.put("ImpedanceRightFoot50kHzX"         , _TNTMeasurementInformation.getImpedanceRightFoot50kHzX());
                PostData.put("ImpedanceRightFoot6_25kHzR"       , _TNTMeasurementInformation.getImpedanceRightFoot6_25kHzR());
                PostData.put("ImpedanceRightFoot6_25kHzX"       , _TNTMeasurementInformation.getImpedanceRightFoot6_25kHzX());
                PostData.put("ImpedanceLeftBody50kHzR"          , _TNTMeasurementInformation.getImpedanceLeftBody50kHzR());
                PostData.put("ImpedanceLeftBody50kHzX"          , _TNTMeasurementInformation.getImpedanceLeftBody50kHzX());
                PostData.put("ImpedanceLeftBody6_25kHzR"        , _TNTMeasurementInformation.getImpedanceLeftBody6_25kHzR());
                PostData.put("ImpedanceLeftBody6_25kHzX"        , _TNTMeasurementInformation.getImpedanceLeftBody6_25kHzX());
                PostData.put("ImpedanceRightBody50kHzR"         , _TNTMeasurementInformation.getImpedanceRightBody50kHzR());
                PostData.put("ImpedanceRightBody50kHzX"         , _TNTMeasurementInformation.getImpedanceRightBody50kHzX());
                PostData.put("ImpedanceRightBody6_25kHzR"       , _TNTMeasurementInformation.getImpedanceRightBody6_25kHzR());
                PostData.put("ImpedanceRightBody6_25kHzX"       , _TNTMeasurementInformation.getImpedanceRightBody6_25kHzX());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        SERVER_URL,
                        PostData,
                        response -> {
                            try {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "error: message null.", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> Toast.makeText(this, "update error!!!", Toast.LENGTH_LONG).show()
                );

                queue.add(jsonObjectRequest);

            } catch (Exception e) {
                Toast.makeText(this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            UserText.setText(UserText.getText()+"\nUpdata Over...");
            UpdateCloudButton.setVisibility(View.GONE);
            //finish();
        }
        else
        {
            Toast.makeText(this, "資料不足無法上傳", Toast.LENGTH_LONG).show();
        }
    }
    public void WriteCSV(View v)
    {
        String[] parts = UserText.getText().toString().split("\n");
        if(parts.length>80) {
            // Build CSV Folder
            File csvDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyCSVFolder");
            if (!csvDir.exists()) {
                csvDir.mkdirs();
            }
            // Build CSV File,use Timestamp
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
            String fileName = UserName + "_" + timestamp + ".csv";
            File csvFile = new File(csvDir, fileName);

            try (FileWriter writer = new FileWriter(csvFile)) {
                writer.append("TestTime,MachineNumber,UserName,Birthday,Height,Gender,weight,ActivityLevel,MuscleQuality,AthleteIndex,MBA,BasalMetabolicRate,BasalMetabolicRateJudgement,VisceralFat,VisceralFatJudgement,MetabolicAge,BoneMass,BoneMassJudgement,BodyWater,BMI,BmiJudgement,EpPulse,BodyFat,BodyFatJudgement,MuscleMass,MuscleMassJudgement,MuscleMassScore,MuscleQuality,MuscleQualityJudgement,BodyFatTrunk,BodyFatJudgementTrunk,MuscleMassTrunk,MuscleMassJudgementTrunk,MuscleMassScoreTrunk,BodyFatLeftArm,BodyFatJudgementLeftArm,MuscleMassLeftArm,MuscleMassJudgementLeftArm,MuscleMassScoreLeftArm,MuscleQualityLeftArm,MuscleQualityJudgementLeftArm,BodyFatRightArm,BodyFatJudgementRightArm,MuscleMassRightArm,MuscleMassJudgementRightArm,MuscleMassScoreRightArm,MuscleQualityRightArm,MuscleQualityJudgementRightArm,BodyFatLeftFoot,BodyFatJudgementLeftFoot,MuscleMassLeftFoot,MuscleMassJudgementLeftFoot,MuscleMassScoreLeftFoot,MuscleQualityLeftFoot,MuscleQualityJudgementLeftFoot,BodyFatRightFoot,BodyFatJudgementRightFoot,MuscleMassRightFoot,MuscleMassJudgementRightFoot,MuscleMassScoreRightFoot,MuscleQualityRightFoot,MuscleQualityJudgementRightFoot,ImpedanceArms50kHzX,ImpedanceArms6_25kHzR,ImpedanceArms6_25kHzX,ImpedanceLeftArm50kHzR,ImpedanceLeftArm50kHzX,ImpedanceLeftArm6_25kHzR,ImpedanceLeftArm6_25kHzX,ImpedanceRightArm50kHzR,ImpedanceRightArm50kHzX,ImpedanceRightArm6_25kHzR,ImpedanceRightArm6_25kHzX,ImpedanceFoot50kHzR,ImpedanceFoot50kHzX,ImpedanceFoot6_25kHzR,ImpedanceFoot6_25kHzX,ImpedanceLeftFoot50kHzR,ImpedanceLeftFoot50kHzX,ImpedanceLeftFoot6_25kHzR,ImpedanceLeftFoot6_25kHzX,ImpedanceRightFoot50kHzR,ImpedanceRightFoot50kHzX,ImpedanceRightFoot6_25kHzR,ImpedanceRightFoot6_25kHzX,ImpedanceLeftBody50kHzR,ImpedanceLeftBody50kHzX,ImpedanceLeftBody6_25kHzR,ImpedanceLeftBody6_25kHzX,ImpedanceRightBody50kHzR,ImpedanceRightBody50kHzX,ImpedanceRightBody6_25kHzR,ImpedanceRightBody6_25kHzX\n");
                writer.append(_TNTUserInformation.getDate() + ",ST0001" + "," + UserName + "," + _TNTUserInformation.getGender() + "," + _TNTUserInformation.getHeight() + "," + _TNTUserInformation.getGender() + "," + _TNTMeasurementInformation.getWeight() + "," + _TNTMeasurementInformation.getActivityLevel() + "," + _TNTMeasurementInformation.getMuscleQuality() + "," + _TNTMeasurementInformation.getAthleteIndex() + "," + _TNTMeasurementInformation.getMetabolicAge() + "," + _TNTMeasurementInformation.getBasalMetabolicRate() + "," + _TNTMeasurementInformation.getBasalMetabolicRateJudgement() + "," + _TNTMeasurementInformation.getVisceralFat() + "," + _TNTMeasurementInformation.getVisceralFatJudgement() + "," + _TNTMeasurementInformation.getMetabolicAge() + ","  + _TNTMeasurementInformation.getBoneMass() + "," + _TNTMeasurementInformation.getBoneMassJudgement() + "," + _TNTMeasurementInformation.getBodyWater() + "," + _TNTMeasurementInformation.getBodyMassIndex() + "," + _TNTMeasurementInformation.getBodyMassIndexJudgement() + "," + _TNTMeasurementInformation.getEpPulse() + "," + _TNTMeasurementInformation.getBodyFat() + "," + _TNTMeasurementInformation.getBodyFatJudgement() + "," + _TNTMeasurementInformation.getMuscleMass() + "," + _TNTMeasurementInformation.getMuscleMassJudgement() + "," + _TNTMeasurementInformation.getMuscleMassScore() + "," + _TNTMeasurementInformation.getMuscleQuality() + "," + _TNTMeasurementInformation.getMuscleQualityJudgement() + "," + _TNTMeasurementInformation.getBodyFatTrunk() + "," + _TNTMeasurementInformation.getBodyFatJudgementTrunk() + "," + _TNTMeasurementInformation.getMuscleMassTrunk() + "," + _TNTMeasurementInformation.getMuscleMassJudgementTrunk() + "," + _TNTMeasurementInformation.getMuscleMassScoreTrunk() + "," + _TNTMeasurementInformation.getBodyFatLeftArm() + "," + _TNTMeasurementInformation.getBodyFatJudgementLeftArm() + "," + _TNTMeasurementInformation.getMuscleMassLeftArm() + "," + _TNTMeasurementInformation.getMuscleMassJudgementLeftArm() + "," + _TNTMeasurementInformation.getMuscleMassScoreLeftArm() + "," + _TNTMeasurementInformation.getMuscleQualityLeftArm() + "," + _TNTMeasurementInformation.getMuscleQualityJudgementLeftArm() + "," + _TNTMeasurementInformation.getBodyFatRightArm() + "," + _TNTMeasurementInformation.getBodyFatJudgementRightArm() + "," + _TNTMeasurementInformation.getMuscleMassRightArm() + "," + _TNTMeasurementInformation.getMuscleMassJudgementRightArm() + "," + _TNTMeasurementInformation.getMuscleMassScoreRightArm() + "," + _TNTMeasurementInformation.getMuscleQualityRightArm() + "," + _TNTMeasurementInformation.getMuscleQualityJudgementRightArm() + "," + _TNTMeasurementInformation.getBodyFatLeftFoot() + "," + _TNTMeasurementInformation.getBodyFatJudgementLeftFoot() + "," + _TNTMeasurementInformation.getMuscleMassLeftFoot() + "," + _TNTMeasurementInformation.getMuscleMassJudgementLeftFoot() + "," + _TNTMeasurementInformation.getMuscleMassScoreLeftFoot() + "," + _TNTMeasurementInformation.getMuscleQualityLeftFoot() + "," + _TNTMeasurementInformation.getMuscleQualityJudgementLeftFoot() + "," + _TNTMeasurementInformation.getBodyFatRightFoot() + "," + _TNTMeasurementInformation.getBodyFatJudgementRightFoot() + "," + _TNTMeasurementInformation.getMuscleMassRightFoot() + "," + _TNTMeasurementInformation.getMuscleMassJudgementRightFoot() + "," + _TNTMeasurementInformation.getMuscleMassScoreRightFoot() + "," + _TNTMeasurementInformation.getMuscleQualityRightFoot() + "," + _TNTMeasurementInformation.getMuscleQualityJudgementRightFoot() + "," + _TNTMeasurementInformation.getImpedanceArms50kHzX() + "," + _TNTMeasurementInformation.getImpedanceArms6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceArms6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftArm50kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftArm50kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftArm6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftArm6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightArm50kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightArm50kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightArm6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightArm6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceFoot50kHzR() + "," + _TNTMeasurementInformation.getImpedanceFoot50kHzX() + "," + _TNTMeasurementInformation.getImpedanceFoot6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceFoot6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftFoot50kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftFoot50kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftFoot6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftFoot6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightFoot50kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightFoot50kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightFoot6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightFoot6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftBody50kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftBody50kHzX() + "," + _TNTMeasurementInformation.getImpedanceLeftBody6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceLeftBody6_25kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightBody50kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightBody50kHzX() + "," + _TNTMeasurementInformation.getImpedanceRightBody6_25kHzR() + "," + _TNTMeasurementInformation.getImpedanceRightBody6_25kHzX());
                writer.flush();
                Toast.makeText(this, "CSV Output: Download/MyCSVFolder/" + fileName, Toast.LENGTH_LONG).show();
                Log.i("wiki1", "CSV Output:" + csvFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("wiki1", "CSV Output Error:" + e.getMessage());
            }
        }
        else
        {
            Toast.makeText(this, "資料不足無法建檔", Toast.LENGTH_LONG).show();
        }
    }
    public void EasyModeShowEvent(View v)
    {
        String[] parts = UserText.getText().toString().split("\n");
        if(parts.length>80) {
            Intent intent = new Intent(this,ShowResultUiActivity.class);
            intent.putExtra("TestTime"                         , _TNTUserInformation.getDate());
            intent.putExtra("MachineNumber"                    , "ST0001");
            intent.putExtra("UserName"                         , UserName); //if have,this user's phone.
            intent.putExtra("Birthday"                         , Birthday);
            intent.putExtra("Height"                           , Height);
            intent.putExtra("Gender"                           , Gender);
            intent.putExtra("Weight"                           , _TNTMeasurementInformation.getWeight());
            intent.putExtra("ActivityLevel"                    , _TNTMeasurementInformation.getActivityLevel());
            intent.putExtra("MuscleQuality"                    , _TNTMeasurementInformation.getMuscleQuality());
            intent.putExtra("AthleteIndex"                     , _TNTMeasurementInformation.getAthleteIndex());
            intent.putExtra("MBA"                              , _TNTMeasurementInformation.getMetabolicAge());
            intent.putExtra("BasalMetabolicRate"               , _TNTMeasurementInformation.getBasalMetabolicRate());
            intent.putExtra("BasalMetabolicRateJudgement"      , _TNTMeasurementInformation.getBasalMetabolicRateJudgement());
            intent.putExtra("VisceralFat"                      , _TNTMeasurementInformation.getVisceralFat());
            intent.putExtra("VisceralFatJudgement"             , _TNTMeasurementInformation.getVisceralFatJudgement());
            intent.putExtra("MetabolicAge"                     , _TNTMeasurementInformation.getMetabolicAge());
            intent.putExtra("BoneMass"                         , _TNTMeasurementInformation.getBoneMass());
            intent.putExtra("BoneMassJudgement"                , _TNTMeasurementInformation.getBoneMassJudgement());
            intent.putExtra("BodyWater"                        , _TNTMeasurementInformation.getBodyWater());
            intent.putExtra("BMI"                              , _TNTMeasurementInformation.getBodyMassIndex());
            intent.putExtra("BmiJudgement"                     , _TNTMeasurementInformation.getBodyMassIndexJudgement());
            intent.putExtra("EpPulse"                          , _TNTMeasurementInformation.getEpPulse());
            intent.putExtra("BodyFat"                          , _TNTMeasurementInformation.getBodyFat());
            intent.putExtra("BodyFatJudgement"                 , _TNTMeasurementInformation.getBodyFatJudgement());
            intent.putExtra("MuscleMass"                       , _TNTMeasurementInformation.getMuscleMass());
            intent.putExtra("MuscleMassJudgement"              , _TNTMeasurementInformation.getMuscleMassJudgement());
            intent.putExtra("MuscleMassScore"                  , _TNTMeasurementInformation.getMuscleMassScore());
            intent.putExtra("MuscleQuality"                    , _TNTMeasurementInformation.getMuscleQuality());
            intent.putExtra("MuscleQualityJudgement"           , _TNTMeasurementInformation.getMuscleQualityJudgement());
            intent.putExtra("BodyFatTrunk"                     , _TNTMeasurementInformation.getBodyFatTrunk());
            intent.putExtra("BodyFatJudgementTrunk"            , _TNTMeasurementInformation.getBodyFatJudgementTrunk());
            intent.putExtra("MuscleMassTrunk"                  , _TNTMeasurementInformation.getMuscleMassTrunk());
            intent.putExtra("MuscleMassJudgementTrunk"         , _TNTMeasurementInformation.getMuscleMassJudgementTrunk());
            intent.putExtra("MuscleMassScoreTrunk"             , _TNTMeasurementInformation.getMuscleMassScoreTrunk());
            intent.putExtra("BodyFatLeftArm"                   , _TNTMeasurementInformation.getBodyFatLeftArm());
            intent.putExtra("BodyFatJudgementLeftArm"          , _TNTMeasurementInformation.getBodyFatJudgementLeftArm());
            intent.putExtra("MuscleMassLeftArm"                , _TNTMeasurementInformation.getMuscleMassLeftArm());
            intent.putExtra("MuscleMassJudgementLeftArm"       , _TNTMeasurementInformation.getMuscleMassJudgementLeftArm());
            intent.putExtra("MuscleMassScoreLeftArm"           , _TNTMeasurementInformation.getMuscleMassScoreLeftArm());
            intent.putExtra("MuscleQualityLeftArm"             , _TNTMeasurementInformation.getMuscleQualityLeftArm());
            intent.putExtra("MuscleQualityJudgementLeftArm"    , _TNTMeasurementInformation.getMuscleQualityJudgementLeftArm());
            intent.putExtra("BodyFatRightArm"                  , _TNTMeasurementInformation.getBodyFatRightArm());
            intent.putExtra("BodyFatJudgementRightArm"         , _TNTMeasurementInformation.getBodyFatJudgementRightArm());
            intent.putExtra("MuscleMassRightArm"               , _TNTMeasurementInformation.getMuscleMassRightArm());
            intent.putExtra("MuscleMassJudgementRightArm"      , _TNTMeasurementInformation.getMuscleMassJudgementRightArm());
            intent.putExtra("MuscleMassScoreRightArm"          , _TNTMeasurementInformation.getMuscleMassScoreRightArm());
            intent.putExtra("MuscleQualityRightArm"            , _TNTMeasurementInformation.getMuscleQualityRightArm());
            intent.putExtra("MuscleQualityJudgementRightArm"   , _TNTMeasurementInformation.getMuscleQualityJudgementRightArm());
            intent.putExtra("BodyFatLeftFoot"                  , _TNTMeasurementInformation.getBodyFatLeftFoot());
            intent.putExtra("BodyFatJudgementLeftFoot"         , _TNTMeasurementInformation.getBodyFatJudgementLeftFoot());
            intent.putExtra("MuscleMassLeftFoot"               , _TNTMeasurementInformation.getMuscleMassLeftFoot());
            intent.putExtra("MuscleMassJudgementLeftFoot"      , _TNTMeasurementInformation.getMuscleMassJudgementLeftFoot());
            intent.putExtra("MuscleMassScoreLeftFoot"          , _TNTMeasurementInformation.getMuscleMassScoreLeftFoot());
            intent.putExtra("MuscleQualityLeftFoot"            , _TNTMeasurementInformation.getMuscleQualityLeftFoot());
            intent.putExtra("MuscleQualityJudgementLeftFoot"   , _TNTMeasurementInformation.getMuscleQualityJudgementLeftFoot());
            intent.putExtra("BodyFatRightFoot"                 , _TNTMeasurementInformation.getBodyFatRightFoot());
            intent.putExtra("BodyFatJudgementRightFoot"        , _TNTMeasurementInformation.getBodyFatJudgementRightFoot());
            intent.putExtra("MuscleMassRightFoot"              , _TNTMeasurementInformation.getMuscleMassRightFoot());
            intent.putExtra("MuscleMassJudgementRightFoot"     , _TNTMeasurementInformation.getMuscleMassJudgementRightFoot());
            intent.putExtra("MuscleMassScoreRightFoot"         , _TNTMeasurementInformation.getMuscleMassScoreRightFoot());
            intent.putExtra("MuscleQualityRightFoot"           , _TNTMeasurementInformation.getMuscleQualityRightFoot());
            intent.putExtra("MuscleQualityJudgementRightFoot"  , _TNTMeasurementInformation.getMuscleQualityJudgementRightFoot());
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "資料不足無法建檔", Toast.LENGTH_LONG).show();
        }
    }
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private void CheckPermissions(){//程式內需設計請求使用者進行藍芽權限開啟,Android8.0以後的要求
        int permission1 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }
    public String LoadStorageFile()
    {
        File file = new File(getExternalFilesDir(null), FILE_NAME); // 指向內部儲存的檔案
        Log.d("wiki1", "File absolute path: " + file.getAbsolutePath());
        // 檢查檔案是否存在
        if (!file.exists()) {
            //Toast.makeText(this, "檔案不存在，請先儲存資料", Toast.LENGTH_SHORT).show();
            return "第一次連線\n請點按RD-545設備上的o鍵等待連接後,再點按上面面按鍵做UUID儲存。";
        }

        StringBuilder content = new StringBuilder();
        try {
            // 使用 FileInputStream 讀取檔案
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "讀取失敗: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return "讀取失敗\n"+ e.getMessage();
        }

        return "讀取成功\n喚醒設備:\n"+content.toString().trim(); // 回傳檔案內容
    }
    public void SaveStorageFile(UUID UUID0, String MAC0)
    {
        try {
            String formattedMac = formatMacAddress(MAC0);
            FileData=formattedMac+"\n"+UUID0;
            File file = new File(getExternalFilesDir(null), FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(FileData.getBytes());
            fos.close();
            UserText.setText("儲存MAC:"+FileData);
            //Toast.makeText(this, "已成功儲存至: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            //MessageText.setText("儲存MAC失敗!!!");
            //Toast.makeText(this, "儲存失敗: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static String formatMacAddress(String mac)
    {
        return mac.replaceAll("(.{2})", "$1:").replaceAll(":$", "");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _TNTBLEPeripheral.disconnect();
    }
}