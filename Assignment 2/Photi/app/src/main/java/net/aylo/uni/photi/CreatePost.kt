package net.aylo.uni.photi

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import kotlinx.android.synthetic.main.createpost_layout.*
import kotlinx.android.synthetic.main.createpost_layout.imgImage
import kotlinx.android.synthetic.main.createpost_layout.imgMap
import kotlinx.android.synthetic.main.createpost_layout.txtLoc

class CreatePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val lat = intent.extras?.getString("EXTRA_Latitude")
        val lon = intent.extras?.getString("EXTRA_Longitude")
        val uri = intent.extras?.get("EXTRA_URI")

        txtLoc.text = "Latitude: $lat\nLongitude: $lon"

        /*
            Changes which corners are rounded based on which orientation the device is
        */
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Glide.with(this).load(uri).transform(CenterCrop(), GranularRoundedCorners(42F, 42F, 0F, 0F)).into(imgImage)
        } else {
            Glide.with(this).load(uri).transform(CenterCrop(), GranularRoundedCorners(42F, 0F, 0F, 42F)).into(imgImage)
        }

        /*
            Creates interaction with Maps
        */
        imgMap.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("geo:0,0?q=$lat,$lon")
            })
        }

        /*
            Sets result to OK (finishes the activity that started this one)
            creates a toast
            finishes
         */
        btnPost.setOnClickListener {
            Toast.makeText(it.context, "Photo posted", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
            finish()
        }
    }

}