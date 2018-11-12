package pe.edu.bitec.appbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    //widgets
    Button btnPaired;
    RecyclerView devicelist;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    AdapterItemBluetooth adapter;
    ArrayList<ItemBluetooth> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button);

        devicelist = (RecyclerView) findViewById(R.id.listView);

        //lista
        lista = new ArrayList<ItemBluetooth>();

        //adapter
        adapter = new AdapterItemBluetooth(lista);

        //orientacion de la lista
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL,
                        false);

        //asignamos la orientacion
        devicelist.setLayoutManager(linearLayoutManager);

        //seteamos adapter
        devicelist.setAdapter(adapter);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                lista.clear();
                adapter.notifyDataSetChanged();
                pairedDevicesList();
            }
        });

    }


    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ItemBluetooth itemBluetooth;

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                itemBluetooth = new ItemBluetooth(bt.getName(),bt.getAddress());
                lista.add(itemBluetooth);

            }

            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

    }









}
