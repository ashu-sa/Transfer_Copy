package com.appcake.transfercopy.Fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appcake.transfercopy.R
import com.appcake.transfercopy.databinding.FragmentQRcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import com.journeyapps.barcodescanner.BarcodeEncoder

class QRcodeFragment : Fragment() {
      private lateinit var binding: FragmentQRcodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentQRcodeBinding.inflate(inflater, container, false)
        return binding.root

        val barcodeEncoder = BarcodeEncoder()
        try {
            val bitmap = barcodeEncoder.encodeBitmap("WIFI:T:WPA;S:mynetwork;P:mypass;;;", BarcodeFormat.QR_CODE, 200,200)
            binding.qrCode.setImageBitmap(bitmap)

        }catch (e:WriterException){
            e.printStackTrace()
        }

//        val writer = QRCodeWriter()
//        try {
//            val bitmatrix = writer.encode("Some",BarcodeFormat.QR_CODE,512,512)
//            val width = bitmatrix.width
//            val height = bitmatrix.height
//            val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
//            for(x in 0 until width){
//                for (y in 0 until height ){
//                    bitmap.setPixel(x,y,if(bitmatrix[x,y]) Color.BLACK else Color.WHITE)
//                }
//                binding.qrCode.setImageBitmap(bitmap)
//            }
//
//        }catch (e:WriterException){
//            e.printStackTrace()
//        }


     }
    fun getQrCodeBitmap(ssid: String, password: String): Bitmap {
        val size = 512 //pixels
        val qrCodeContent = "WIFI:S:$ssid;T:WPA;P:$password;;"
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }


    }
