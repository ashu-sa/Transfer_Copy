package com.appcake.transfercopy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.appcake.transfercopy.Fragments.QRcodeFragment
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class FileTransferActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFileTransferBinding
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    var channel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver =
                manager?.let { com.appcake.transfercopy.WifiP2P.BroadcastReceiver(it,channel,this) }
        }
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        this.registerReceiver(receiver,intentFilter)





        binding.apply {
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@FileTransferActivity,MainActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

           receiveFileCard.setOnClickListener {
               val intent = Intent(this@FileTransferActivity,PhoneStorageScreen::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }

            sendFileCard.setOnClickListener {
                validatePermission()
                manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                    override fun onSuccess() {
                        val fragment = QRcodeFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
                    }

                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(this@FileTransferActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
    }
    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@FileTransferActivity, "Permission 2", Toast.LENGTH_SHORT).show()

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(this@FileTransferActivity, "Permission 3", Toast.LENGTH_SHORT).show()

                }

            }).check()
    }
    override fun onStop() {
        super.onStop()
        manager?.cancelConnect(channel, null)
        manager?.removeGroup(channel, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(receiver)
    }

}