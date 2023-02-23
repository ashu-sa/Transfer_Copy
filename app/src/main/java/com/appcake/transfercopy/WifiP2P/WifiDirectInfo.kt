package com.appcake.transfercopy.WifiP2P

import android.net.wifi.p2p.*
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.ActionListener

class WifiDirectInfo(private val wifiP2pManager: WifiP2pManager, private val channel: Channel) {

     var ssid: String? = null
     var securityType: String? = null
     var password: String? = null

    fun getWifiDirectInfo(device: WifiP2pDevice, onSuccess: (ssid: String, securityType: String, password: String) -> Unit) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }
        wifiP2pManager.connect(channel, config, object : ActionListener {
            override fun onSuccess() {
                wifiP2pManager.requestConnectionInfo(channel, object : WifiP2pManager.ConnectionInfoListener {
                    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
                        info?.let {
                            val groupOwnerAddress = it.groupOwnerAddress?.hostAddress
                            if (groupOwnerAddress == null) {
                                ssid =it.groupOwnerAddress?.hostAddress
                                securityType = "WPA2"
                                password = ""
                            } else {
                                wifiP2pManager.requestGroupInfo(channel, object : WifiP2pManager.GroupInfoListener {
                                    override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
                                        group?.let {
                                            ssid = group.networkName
                                            securityType = group.passphrase?.let { passphrase ->
                                                if (passphrase.matches(Regex("^\\p{ASCII}+$"))) {
                                                    "WPA2"
                                                } else {
                                                    "WEP"
                                                }
                                            }
                                            password = group.passphrase
                                        }
                                    }
                                })
                            }
                            onSuccess(ssid!!, securityType!!, password!!)
                        }
                    }
                })
            }
            override fun onFailure(reason: Int) {
                // handle failure
            }
        })
    }


}