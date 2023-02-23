package com.appcake.transfercopy

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
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
    private var connectionInfoListener: WifiP2pManager.ConnectionInfoListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validatePermission()

        val manager: WifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel: WifiP2pManager.Channel = manager.initialize(this, Looper.getMainLooper(), null)



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
                    Toast.makeText(this@ReceiveActivity, "Permission 2", Toast.LENGTH_SHORT).show()

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(this@ReceiveActivity, "Permission 3", Toast.LENGTH_SHORT).show()

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
                    val config = WifiP2pConfig()
                    config.deviceAddress = deviceAddress
                    wifiP2pManager!!.connect(channel, config, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            Toast.makeText(this@ReceiveActivity, "Connected to $deviceAddress", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(reason: Int) {
                            Toast.makeText(this@ReceiveActivity, "Failed to connect to $deviceAddress", Toast.LENGTH_SHORT).show()
                        }
                    })
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
}