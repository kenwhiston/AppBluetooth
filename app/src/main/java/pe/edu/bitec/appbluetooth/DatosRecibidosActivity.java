package pe.edu.bitec.appbluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class DatosRecibidosActivity extends AppCompatActivity {

    private ItemBluetooth itemBluetooth;
    TextView txtValor;

    String address = null;

    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    BluetoothAdapter myBluetooth = null;

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;


    volatile boolean stopWorker;


    String cadena="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_recibidos);

        txtValor = findViewById(R.id.txtValor);

        Bundle bundle = getIntent().getExtras();

        itemBluetooth = (ItemBluetooth) bundle.getSerializable("ITEM");

        address = itemBluetooth.getNumber();

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

    }

    @Override
    protected void onStart() {
        super.onStart();

        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice device = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available

        //textStatus.setText("start ThreadConnectBTdevice");
        msg("start ThreadConnectBTdevice");
        myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
        myThreadConnectBTdevice.start();

    }

    /*@Override
    protected void onDestroy() {


        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();

            stopWorker = true;

            myThreadConnected.interrupt();
            myThreadConnectBTdevice.interrupt();



        }

        super.onDestroy();


    }*/

    @Override
    protected void onPause() {

        super.onPause();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();

            stopWorker = true;

            myThreadConnected.interrupt();
            myThreadConnectBTdevice.interrupt();

            myThreadConnected = null;
            myThreadConnectBTdevice = null;

        }

        finish();
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    //seccion de subclases//
    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                //textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
                msg("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                        msg("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        //textStatus.setText(msgconnected);
                        msg(msgconnected);

                        //listViewPairedDevice.setVisibility(View.GONE);
                        //inputPane.setVisibility(View.VISIBLE);
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            /*Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();*/

            msg("close bluetoothSocket");

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;

            stopWorker = false;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            //while (true) {
            while(!Thread.currentThread().isInterrupted() && !stopWorker){
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    //concatenamos
                    cadena = cadena + strReceived;

                    if(cadena.length()>0){
                        if(cadena.substring(cadena.length()-1).compareTo("-")==0){
                            //ya tenemos una cadena formada
                            final String cadenaEntrada = cadena.substring(0,cadena.length() - 1 );
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    //textStatus.setText(msgReceived);
                                    //msg(cadenaEntrada);
                                    txtValor.setText(cadenaEntrada);
                                }});

                            cadena = "";
                        }
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //textStatus.setText(msgConnectionLost);
                            //stopWorker = true;
                            msg(msgConnectionLost);
                        }});
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    //fin de seccion de subclases///



}
