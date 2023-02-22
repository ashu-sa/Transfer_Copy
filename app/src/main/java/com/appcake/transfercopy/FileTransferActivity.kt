package com.appcake.transfercopy

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.wifi.WifiManager
import android.net.wifi.p2p.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
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
import com.appcake.transfercopy.databinding.ActivityFileTransferBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.util.ArrayList

class FileTransferActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFileTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager: WifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel: WifiP2pManager.Channel = manager.initialize(this, Looper.getMainLooper(), null)

        validatePermission()
        binding.apply {
            backImg.setOnClickListener {
                val intent = Intent(Intent(this@FileTransferActivity,MainActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

           receiveFileCard.setOnClickListener {
               val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
               if (ContextCompat.checkSelfPermission(this@FileTransferActivity, permission) == PackageManager.PERMISSION_GRANTED) {
                   val intent = Intent(this@FileTransferActivity,ReceiveActivity::class.java)
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                   startActivity(intent)
               } else {
                   Toast.makeText(this@FileTransferActivity, "Please Grant All Permissions Manually", Toast.LENGTH_SHORT).show()
               }
              
           }

            sendFileCard.setOnClickListener {
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
        val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val point: Point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        val qrEncoder = QRGEncoder("Something", null, QRGContents.Type.TEXT, dimen)
        try {
            val bitmap = qrEncoder.getBitmap(0)
            image.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialog.show()
    }
    private fun validatePermission() {
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.READ_CALENDAR)
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                  report.let {
                      if (report!!.areAllPermissionsGranted()){
                          
                      }else{
                          Toast.makeText(this@FileTransferActivity, "Please Grant Permissions to use the App", Toast.LENGTH_SHORT).show()
                      }
                  }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                  token?.continuePermissionRequest()
                }

            }).withErrorListener{
                Toast.makeText(this, it.name , Toast.LENGTH_SHORT).show()
            }.check()
    }
}