package com.pdf.bp_sensortesting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference("distance");
        databaseReference.setValue("0");
    }
    public void startBluetooth(View view)
    {
        boolean flag = false;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());

        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:36:00:D2:67");
        System.out.println(hc05.getName());

        BluetoothSocket btSocket = null;
        int counter = 0;
        assert btSocket != null;
        do {
            try {
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(btSocket);
                btSocket.connect();
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && (counter < 3));


        /*try {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write(48);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        InputStream inputStream = null;
        try {
            inputStream = btSocket.getInputStream();
            inputStream.skip(inputStream.available());

            for (;;) {
                try {
                    //inputStream.reset();
                    char c = (char)inputStream.read();
                    System.out.println(c);
                    if(c!='0') {
                        databaseReference.setValue("1");
                        flag=true;

                        Thread.sleep(200);
                        inputStream.reset();
                        inputStream.reset();
                        inputStream.reset();
                        inputStream.reset();

                    }
                    else{
                        if(flag){

                            databaseReference.setValue("0");
                            flag=false;
                            Thread.sleep(200);
                            inputStream.reset();
                            inputStream.reset();
                            inputStream.reset();
                            inputStream.reset();

                        }
                    }
                    inputStream.reset();
                }
                catch (Exception e){
                    System.out.println();
                }
                Thread.sleep(35);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            btSocket.close();
            System.out.println(btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}