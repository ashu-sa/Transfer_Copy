package com.appcake.transfercopy

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.wifi.WifiConfiguration
import android.net.wifi.p2p.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.appcake.transfercopy.WifiP2P.WifiDirectInfo
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import org.json.JSONObject
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import android.net.wifi.WifiManager as WifiManager

class FileTransferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileTransferBinding

    private var wifiP2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var connectionInfoListener: WifiP2pManager.ConnectionInfoListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager?.initialize(this, mainLooper, null)

        val wifimanager: WifiManager =
            getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

        validatePermission()
        binding.apply {
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@FileTransferActivity, MainActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            receiveFileCard.setOnClickListener {
                if (!wifimanager.isWifiEnabled) {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }
                val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(
                        this@FileTransferActivity,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this@FileTransferActivity, ReceiveActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@FileTransferActivity,
                        "Please Grant All Permissions Manually",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            sendFileCard.setOnClickListener {
                if (!wifimanager.isWifiEnabled) {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }
                showCustomDialog()
            }
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog_qrcode)
        val image = dialog.findViewById<ImageView>(R.id.qr_code)
        val wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = wifiP2pManager.initialize(this, mainLooper, null)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        // Wi-Fi Direct state changed
                    }
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        // List of available peers changed
                    }
                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        // Wi-Fi Direct connection state changed
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
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@FileTransferActivity, "Discovery Started", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(reason: Int) {
                // Discovery failed
            }
        })
        val peerListListener = WifiP2pManager.PeerListListener { peerList ->
            // Handle the list of nearby Wi-Fi P2P devices
            val peers = peerList.deviceList
            // Get the address of the first nearby Wi-Fi P2P device
            val deviceAddress = peers.firstOrNull()?.deviceAddress
            // Generate QR code using the device address
            val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val display: Display = windowManager.defaultDisplay
            val point: Point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4
            val qrEncoder = QRGEncoder(deviceAddress, null, QRGContents.Type.TEXT, dimen)
            try {
                val bitmap = qrEncoder.getBitmap(0)
                image.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        wifiP2pManager.requestPeers(channel, peerListListener)
        dialog.show()
    }

    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report.let {
                        if (report!!.areAllPermissionsGranted()) {

                        } else {
                            Toast.makeText(
                                this@FileTransferActivity,
                                "Please Grant Permissions to use the App",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }.check()
    }

    fun getIPAddress(): String {
        var ipAddress = ""
        try {
            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ip = wifiInfo.ipAddress
            ipAddress = InetAddress.getByAddress(
                ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ip).array()
            ).hostAddress
        } catch (ex: Exception) {
            Log.e("IP Address", ex.toString())
        }
        return ipAddress
    }

    private fun createWifiHotspot(): String {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }

        val wifiConfiguration = WifiConfiguration()
        wifiConfiguration.SSID = "MyHotspot"
        wifiConfiguration.preSharedKey = UUID.randomUUID().toString()

        return wifiConfiguration.preSharedKey
    }
}