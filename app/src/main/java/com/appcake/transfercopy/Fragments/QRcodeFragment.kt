package com.appcake.transfercopy.Fragments

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.view.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.appcake.transfercopy.WifiP2P.JsonHelper
import com.appcake.transfercopy.databinding.FragmentQRcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class QRcodeFragment : Fragment() {
      private lateinit var binding: FragmentQRcodeBinding
      private lateinit var manager: WifiManager
      private lateinit var qrEncoder: QRGEncoder
      private lateinit var bitmap: Bitmap
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

//       manager = requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        fun oreoAndAboveDevicesSetupHotspot() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) manager.startLocalOnlyHotspot(
//                object : LocalOnlyHotspotCallback() {
//                    override fun onStarted(reservation: LocalOnlyHotspotReservation) {
//                        val reservation = reservation
//                        val retry = 0
//                        val jsonObject = JSONObject()
//
//                        try {
//                            jsonObject.put(JsonHelper.SSID, reservation.wifiConfiguration!!.SSID)
//                            jsonObject.put(
//                                JsonHelper.Password,
//                                reservation.wifiConfiguration!!.preSharedKey
//                            )
//
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                },
//                null
//            )
//        }
//        binding.button.setOnClickListener {
//        }
//        val windowManager: WindowManager = requireActivity().getSystemService(WINDOW_SERVICE) as WindowManager
//        val display: Display = windowManager.defaultDisplay
//        val point: Point = Point()
//        display.getSize(point)
//        val width = point.x
//        val height = point.y
//        var dimen = if (width < height) width else height
//        dimen = dimen * 3 / 4
//        qrEncoder = QRGEncoder(binding.ettext.text.toString(), null, QRGContents.Type.TEXT, dimen)
//        try {
//            bitmap = qrEncoder.getBitmap(0)
//            binding.qrCode.setImageBitmap(bitmap)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }




     }

        fun generateQrCode(value: String): Bitmap {
            val windowManager: WindowManager = requireActivity().getSystemService(WINDOW_SERVICE) as WindowManager
            val display: Display = windowManager.defaultDisplay
            val point: Point = Point()
            display.getSize(point)
            val width = point.x
            val height = point.y
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4
            qrEncoder = QRGEncoder(value, null, QRGContents.Type.TEXT, dimen)
            try {
               bitmap = qrEncoder.getBitmap()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }


    }
