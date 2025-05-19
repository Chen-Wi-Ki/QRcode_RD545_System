package wi.ki.rd545cmd1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    boolean NeedSaveUUID=true;
    private static final String FILE_NAME = "MAC1.txt";
    String MAC1 = "Null";
    String FileData = "";
    int _Count;
    UUID _UUID;

    String Birthday;
    int Gender;
    String Height,ClothesWeight;
    TextView UserText;
    Button UuidSaveBtn,SaveRomButton,SetDataButton;
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
        Intent intent = getIntent();
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
            public void onUserInformationRetrieved(TNTBLEPeripheral tntblePeripheral, TNTUserInformation tntUserInformation, int i, int i1) {
                Log.i("wiki1","onUserInformationRetrieved runned...");
                _TNTUserInformation = tntUserInformation;
                User0MsgSet2();
            }

            @Override
            public void onUserInformationSaved(TNTBLEPeripheral tntblePeripheral, TNTUserInformation tntUserInformation, int deviceStatus, int error) {
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    // to do something
                    //UserText.setText("Save User Message Over!!!");
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
                    _TNTBLEPeripheral.retrieveMeasurementInformation(_Count);
                }
                Log.i("wiki1","onMeasurementCountRetrieved runned..._Count="+_Count);
            }

            @Override
            public void onMeasurementInformationRetrieved(TNTBLEPeripheral tntblePeripheral, int number, TNTMeasurementInformation tntMeasurementInformation, int deviceStatus, int error) {
                Log.i("wiki1","onMeasurementInformationRetrieved runned...deviceStatus="+deviceStatus);
                if (deviceStatus == TNTDeviceStatus.NORMAL) {
                    _TNTMeasurementInformation = tntMeasurementInformation;
                    UserText.setText("Measurement Message:"+"\n"+
                            "Weight:"+tntMeasurementInformation.getWeight()+"(kg)\n"+
                            "BMI:"+tntMeasurementInformation.getBodyMassIndex()+"\n"+

                            "Body Water:"+tntMeasurementInformation.getBodyWater()+"(%)\n"+
                            "Body Fat:"+tntMeasurementInformation.getBodyFat()+"(%)\n"+
                            "Body Fat udgement:"+tntMeasurementInformation.getBodyMassIndexJudgement()+"(1~4)\n"+
                            "Visceral Fat:"+tntMeasurementInformation.getVisceralFat()+"(1~59)\n"+
                            "Visceral Fat Judgement:"+tntMeasurementInformation.getVisceralFatJudgement()+"(1~3)\n"+


                            "Bone Mass:"+tntMeasurementInformation.getBoneMass()+"(0.30 - 7.06 )\n"+
                            "Bone Mass Judgement:"+tntMeasurementInformation.getBoneMassJudgement()+"(1~4)\n"+


                            "Muscle Mass:"+tntMeasurementInformation.getMuscleMass()+"(kg)\n"+
                            "Muscle Mass Score:"+tntMeasurementInformation.getMuscleMassScore()+"(-4~+4)\n"+
                            "Muscle Judgement:"+tntMeasurementInformation.getMuscleMassJudgement()+"(1~3)\n"+
                            "Muscle Quality:"+tntMeasurementInformation.getMuscleQuality()+"(0~100)\n"+


                            "Basal Metabolic Rate:"+tntMeasurementInformation.getBasalMetabolicRate()+"(kcal)\n"+
                            "Basal Metabolic Rate Judgement:"+tntMeasurementInformation.getBasalMetabolicRateJudgement()+"(1~16)\n"+

                            "Get the Daily Caloric Intake:"+tntMeasurementInformation.getDailyCaloricIntake()+"(kcal)\n"+
                            "Ep Pulse:"+tntMeasurementInformation.getEpPulse()+"(50-200)");
                }
                //_TNTBLEPeripheral.disconnect();
            }

            @Override
            public void onRssiUpdated(TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onRssiUpdated runned...");
            }

            @Override
            public void onRequestFailed(TNTBLEPeripheral tntblePeripheral, int i) {
                Log.i("wiki1","onRequestFailed runned...error code="+i);
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
        Log.i("wiki1","執行量測...");
        //Toast.makeText(this, "執行量測...", Toast.LENGTH_LONG).show();
        UserText.setText(UserText.getText()+"\nMeasurement Start...");
        _TNTBLEPeripheral.startMeasurement();
        //Log.i("wiki1","詢問設備登入編號1之使用者別名(TNTUserInformation.getNickname):"+_TNTUserInformation.getNickname());
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

    }

}