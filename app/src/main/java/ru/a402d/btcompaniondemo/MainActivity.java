package ru.a402d.btcompaniondemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    // empty filter = all devices
    final BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder().build();

    final AssociationRequest pairingRequest = new AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .build();

    CompanionDeviceManager deviceManager;

    private final ActivityResultLauncher<IntentSenderRequest> btCompanionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        try {
                            BluetoothDevice device = Objects.requireNonNull(
                                            result.getData()).
                                    getParcelableExtra(
                                            CompanionDeviceManager.EXTRA_DEVICE);
                            if (device != null) {
                                // действия по запоминанию выбора
                                ((TextView) findViewById(R.id.txtMac)).setText(device.getAddress());
                            }
                        } catch (Exception ignored) {
                        }
                    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceManager = (CompanionDeviceManager) getSystemService(Context.COMPANION_DEVICE_SERVICE);

        findViewById(R.id.button).setOnClickListener(v -> callCompanion());
    }

    private void callCompanion() {
        deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
            // Called when a device is found. Launch the IntentSender so the user can
            // select the device they want to pair with.
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                btCompanionLauncher.launch(new IntentSenderRequest.Builder(chooserLauncher).build());
            }

            @Override
            public void onFailure(CharSequence error) {
                // Handle the failure.
            }
        }, null);
    }

}