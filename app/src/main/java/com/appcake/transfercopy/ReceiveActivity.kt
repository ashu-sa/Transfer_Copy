package com.appcake.transfercopy

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.appcake.transfercopy.databinding.ActivityReceiveBinding
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class ReceiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiveBinding
    private var wifiP2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var peerListListener: WifiP2pManager.PeerListListener? = null
    private var peerList: MutableList<WifiP2pDevice>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validatePermission()
        wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager?.initialize(this, mainLooper, null)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        // Wi-Fi Direct state changed
                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        wifiP2pManager!!.requestPeers(channel,
                            WifiP2pManager.PeerListListener { peers ->
                                // Process the list of available peers
                            })
                    }
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        // Wi-Fi Direct connection state changed
                        val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                        if (networkInfo!!.isConnected) {
                            // Connected to a peer
                            val wifiP2pInfo = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                            val groupOwnerAddress = wifiP2pInfo?.groupOwnerAddress
                            // Process the connection
                        } else {
                            // Disconnected from a peer
                        }
                    }
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                        // This device's Wi-Fi Direct details changed
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        registerReceiver(receiver, intentFilter)
        wifiP2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Successfully initiated peer discovery
            }

            override fun onFailure(reason: Int) {
                // Failed to initiate peer discovery
            }
        })
        peerListListener = WifiP2pManager.PeerListListener { peers ->
            // Update the available peer list
            peerList = peers.deviceList as MutableList<WifiP2pDevice>?
            val l = (peers.deviceList as MutableList<WifiP2pDevice>?)?.get(1)
        }
        openQRscanner()
//        openQRscanner()
        binding.apply {
            backImg.setOnClickListener {
                val intent = Intent(this@ReceiveActivity, FileTransferActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.receiveQrText.visibility = View.INVISIBLE
                    binding.cameraImg.visibility = View.INVISIBLE
                    binding.scannerViewLayout.visibility = View.VISIBLE
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@ReceiveActivity, "Please grant Permission", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }

            }).check()
    }
    private fun openQRscanner(){
         val codeScanner = CodeScanner(this,binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
                channel = wifiP2pManager!!.initialize(this, mainLooper, null)

                if (it != null){
                    val deviceAddress = it.text
                    connectToDevice(deviceAddress)
                }

//                val match = "Hello"
//                if (it.text == match){
//                    val dialog = Dialog(this)
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                    dialog.setCancelable(true)
//                    dialog.setContentView(R.layout.custom_dialog_qrcode)
//                    val image = dialog.findViewById<ImageView>(R.id.qr_code)
//                    val text = dialog.findViewById<TextView>(R.id.scan_text)
//                    image.setImageResource(R.drawable.succesful_tick)
//                    text.text = "Connection Successful"
//                    dialog.show()
//                    val intent = Intent(this@ReceiveActivity,PhoneStorageScreen::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                }else{
//                    val dialog = Dialog(this)
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                    dialog.setCancelable(true)
//                    dialog.setContentView(R.layout.custom_dialog_qrcode)
//                    val image = dialog.findViewById<ImageView>(R.id.qr_code)
//                    val text = dialog.findViewById<TextView>(R.id.scan_text)
//                    image.setImageResource(R.drawable.help)
//                    text.text = "Connection Failes"
//                    dialog.show()
//                    val handler = Handler()
//                    handler.postDelayed({
//                        val intent = Intent(this@ReceiveActivity,FileTransferActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
//                    }, 2000)
//
//                }

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                val intent = Intent(this@ReceiveActivity,FileTransferActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
            codeScanner.startPreview()
    }
    fun connectToDevice(deviceAddress: String) {
        // Find the device with the given MAC address in the peer list
        val deviceToConnect = peerList?.find { it.deviceAddress == deviceAddress }

        // Create a WifiP2pConfig object with the device's details
        val config = WifiP2pConfig()
        config.deviceAddress = deviceToConnect?.deviceAddress
        config.wps.setup = WpsInfo.PBC

        // Connect to the device
        wifiP2pManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@ReceiveActivity, "Connection Successful", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@ReceiveActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}