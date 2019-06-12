package com.example.chetan.eeg_recorder;

import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.neurosky.thinkgear.*;

import android.bluetooth.*;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class EEG_record extends AppCompatActivity {
    TGDevice tgDevice;
    BluetoothAdapter btAdapter;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    String myFileName;
    String state;
    File root,dir,file;
    //Handler handler;
    ArrayList<String> arrayList = new ArrayList<String>();
    EditText ed;


    TextView textView_signal;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eeg_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView_signal = (TextView) findViewById(R.id.textView_signal);
        ed=(EditText)findViewById(R.id.file_name);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            tgDevice = new TGDevice(btAdapter, handler);
        }



    }


    Handler handler = new Handler() {
         @Override
            public void handleMessage(Message msg) {


                switch (msg.what) {
                    case TGDevice.MSG_STATE_CHANGE:
                        switch (msg.arg1) {
                            case TGDevice.STATE_IDLE:
                                break;
                            case TGDevice.STATE_CONNECTING:
                                break;
                            case TGDevice.STATE_CONNECTED:
                                tgDevice.start();
                                break;
                            case TGDevice.STATE_DISCONNECTED:
                                break;
                            case TGDevice.STATE_NOT_FOUND:
                            case TGDevice.STATE_NOT_PAIRED:
                            default:
                                break;
                        }
                        break;
                    case TGDevice.MSG_POOR_SIGNAL:
                        Log.v("HelloEEG", "PoorSignal: " + msg.arg1);
                    case TGDevice.MSG_ATTENTION:
                        Log.v("HelloEEG", "Attention: " + msg.arg1);
                        break;
                    case TGDevice.MSG_RAW_DATA:
                        int rawValue = msg.arg1;
                        double raw1=((((double) rawValue) * 1.8d) / 4096.0d) / 2000.0d;
                        String raw=Double.toString(raw1);
                        textView_signal.setText(raw);
                        arrayList.add(raw);








                            break;
                    case TGDevice.MSG_EEG_POWER:
                        TGEegPower ep = (TGEegPower)msg.obj;
                        Log.v("HelloEEG", "Delta: " + ep.delta);
                    default:
                        break;

                }
            }
        };

             public void start(View view) {
                 if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(true);

             }

    public void stop(View view) {
        if (tgDevice.getState() == TGDevice.STATE_CONNECTED && tgDevice.getState() != TGDevice.STATE_DISCONNECTED) {
            tgDevice.stop();
        }

        myFileName=ed.getText().toString();
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            root = Environment.getExternalStorageDirectory();
            dir = new File(root.getAbsolutePath() + "/EEG_RECORDER");
            if (!dir.exists()) {
                dir.mkdir();
            }
            file = new File(dir, myFileName);

            try {
                FileOutputStream fos = new FileOutputStream(file, true);
                for (String item : arrayList) {
                    fos.write(item.getBytes());
                    fos.write("\n".getBytes());
                }
                fos.close();
                //file.createNewFile();
                //file.createNewFile();
            } catch (Exception e) {

            }

        }
    }
        public void send(View view)
    {


        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/EEG_RECORDER", myFileName);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

// set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        //String to[] = {"amitvaidya.20695@gmail.com"};
        //emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "CSV file");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }














}



