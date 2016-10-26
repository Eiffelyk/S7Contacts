package com.eiffelyk.me.s7contacts;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String DATABASE_FILENAME = "BT.db";
    private static final String GPS_BYD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/馋猫/GPS_BYD";
    //private static final String GPS_BYD_PATH = Environment.getExternalStorageDirectory().toString()+"/YYYYYYY/GPS_BYD";
    private static long contacts_counter;
    private static long db_id;
    private String BTName = getBTName();
    private SQLiteDatabase db;
    private Context mContext;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RunningPersimssionsTool.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "允许读取通讯录和写入SDcard文件才能正常使用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                start2();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void GetSimContact(String paramString) {
        try {
            Uri localUri = Uri.parse(paramString);
            Cursor localCursor = getContentResolver().query(localUri, null, null, null, null);
            if (localCursor != null)
                while (localCursor.moveToNext()) {
                    insertDB(localCursor.getString(localCursor.getColumnIndex("name")), keepNumbersOnly(localCursor.getString(localCursor.getColumnIndex("number"))));
                    contacts_counter = 1L + contacts_counter;
                }
            assert localCursor != null;
            localCursor.close();
        } catch (Exception localException) {
        }

    }

    private String getBTName() {
        try {
            BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (localBluetoothAdapter == null)
                return "no BT";
            return localBluetoothAdapter.getName();
        } catch (Exception localException) {
            return "BT fail";
        }
    }

    private void getContacts() {
        db_id = 0l;
        contacts_counter = 0l;
        String str1 = GPS_BYD_PATH + "/" + DATABASE_FILENAME;
        this.db = SQLiteDatabase.openOrCreateDatabase(str1, null);
        Cursor localCursor = this.mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "sort_key asc");
        if (localCursor != null) {
            while (localCursor.moveToNext()) {
                insertDB(localCursor.getString(localCursor.getColumnIndex("display_name")), keepNumbersOnly(localCursor.getString(localCursor.getColumnIndex("data1"))));
                Message msg  = handler.obtainMessage();
                msg.what =2;
                msg.arg1 = (int) contacts_counter;
                handler.sendMessage(msg);
                contacts_counter = 1L + contacts_counter;
            }
            localCursor.close();
        }
    }

    private int insertDB(String paramString1, String paramString2) {
        if (paramString2.length() == 0|| paramString1.contains("'"))
            return 0;
        db_id = 1L + db_id;
        String str = "INSERT INTO Contact(ID,DeviceName,PhoneNum,Name) VALUES (" + db_id + ",'" + this.BTName + "','" + paramString2 + "','" + paramString1 + "');";
        this.db.execSQL(str);
        return 1;
    }

    private String keepNumbersOnly(CharSequence paramCharSequence) {
        return paramCharSequence.toString().replaceAll("[^0-9\\+]", "");
    }
    private void setBTName(TextView view,String BTName){
        if (BTName.equals("no BT")){
            view.setText("此设备不支持蓝牙");
        }else if (BTName.equals("BT fail")){
            view.setText("获取蓝牙名称失败");
        }else{
            view.setText("蓝牙名称："+BTName);
        }
    }
    private void myCopyFile2SD(String paramString) {
        String str = GPS_BYD_PATH + "/" + paramString;
        File localFile = new File(str);
        if (localFile.exists())
            localFile.delete();
        try {
            InputStream localInputStream = getResources().getAssets().open(paramString);
            int i = localInputStream.available();
            byte[] arrayOfByte = new byte[i];
            localInputStream.read(arrayOfByte);
            localInputStream.close();
            FileOutputStream localFileOutputStream = new FileOutputStream(str);
            localFileOutputStream.write(arrayOfByte, 0, i);
            localFileOutputStream.close();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private Button buttonStart;
    private TextView textViewBTName;
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        this.mContext = getApplicationContext();
        textViewBTName = (TextView) findViewById(R.id.txtbtName);
        findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                MainActivity.this.finish();
            }
        });
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<RunningPersimssion> runningPersimssionArrayList = new ArrayList<>();
                runningPersimssionArrayList.add(new RunningPersimssion(Manifest.permission.WRITE_EXTERNAL_STORAGE, "向SDcard写入文件"));
                runningPersimssionArrayList.add(new RunningPersimssion(Manifest.permission.READ_CONTACTS, "读取通讯录"));
                if (RunningPersimssionsTool.insertDummyContactWrapper(MainActivity.this, runningPersimssionArrayList)) {
                    start2();
                }
            }
        });
        setBTName(textViewBTName, getBTName());
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ((TextView) findViewById(R.id.localTextView2)).setText("共读取联系人：" + contacts_counter + "人");
                        ((TextView) findViewById(R.id.textView2)).setText("保存路径：" + GPS_BYD_PATH);
                        buttonStart.setEnabled(true);
                        buttonStart.setText("重新读取");
                        break;
                    case 1:
                        buttonStart.setEnabled(false);
                        buttonStart.setText("读取中...");
                        break;
                    case 2:
                        ((TextView) findViewById(R.id.localTextView2)).setText("共读取联系人：" + msg.arg1 + "人");
                        buttonStart.setText("读取中...");
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    private Handler handler;
    private void start2(){
        setBTName(textViewBTName, getBTName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
                createPath(GPS_BYD_PATH);
                myCopyFile2SD(DATABASE_FILENAME);
                myCopyFile2SD("MortScript.exe");
                myCopyFile2SD("StartUp.exe");
                myCopyFile2SD("StartUp.mscr");
                getContacts();
                handler.sendEmptyMessage(0);
            }
        }).start();

    }
    private void createPath(String path){
        File localPath = new File(path);
        if (!localPath.exists()) {
            localPath.mkdirs();
        }
    }
}
