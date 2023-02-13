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
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.QRCode
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.*

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

           binding.qrCode.setImageBitmap(generateQrCode("Suceess"))


     }

        fun generateQrCode(value: String): Bitmap {
            val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

            val qrCodeWriter = QRCodeWriter()

            val size = 512

            val bitMatrix = qrCodeWriter.encode(value, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until width) {
                    bmp.setPixel(y, x, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        }

    }
