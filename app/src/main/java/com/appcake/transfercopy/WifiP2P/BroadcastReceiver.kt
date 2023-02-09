package com.appcake.transfercopy.WifiP2P

import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import com.appcake.transfercopy.FileTransferActivity

class BroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: FileTransferActivity
):android.content.BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String = intent?.action!!
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                if (intent?.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 0) != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "Please Switch on Wifi to transfer files", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                val wifiDevicesList = intent.getParcelableExtra<WifiP2pDeviceList>(WifiP2pManager.EXTRA_P2P_DEVICE_LIST)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val wifiP2pInfo = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val localDevice = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
            }

        }
    }
}