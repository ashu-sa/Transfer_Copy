package com.appcake.transfercopy

import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.net.wifi.WifiManager
import android.net.wifi.p2p.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
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
    private lateinit var manager: WifiManager




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFileTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager


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
                showCustomDialog()

//                        val fragment = QRcodeFragment()
//                        supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit


            }
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
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
        val qrEncoder = QRGEncoder("value", null, QRGContents.Type.TEXT, dimen)
        try {
            val bitmap = qrEncoder.getBitmap(0)
            image.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
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

}