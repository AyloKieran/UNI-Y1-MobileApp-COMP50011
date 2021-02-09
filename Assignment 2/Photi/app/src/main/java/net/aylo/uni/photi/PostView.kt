package net.aylo.uni.photi

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import kotlinx.android.synthetic.main.expandedpost_layout.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class PostView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        /*
            Maps Post object back to Data Class
         */
        val post = intent.extras?.get("EXTRA_POST_DATA") as Post

        val imagepath = "https://api.comp50011.uni.aylo.net/images/${post.image}"

        /*
            Changes which corners are rounded based on which orientation the device is
         */
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Glide.with(this).load(imagepath).transform(CenterCrop(), GranularRoundedCorners(42F, 42F, 0F, 0F)).into(imgImage)
        } else {
            Glide.with(this).load(imagepath).transform(CenterCrop(),GranularRoundedCorners(42F, 0F, 0F, 42F)).into(imgImage)
        }

        /*
            Get ISO 8601 datetime string from API and convert it into a TemporalAccessor, then create an
            instant from this and create a DateTimeFormattor object that can be used to format the date before
            writing the new string to screen
         */
        val ta = DateTimeFormatter.ISO_INSTANT.parse(post.createdAt)
        val i = Instant.from(ta)
        val formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.LONG ).withLocale( Locale.UK ).withZone( ZoneId.systemDefault() );

        txtTitle.text = post.title
        txtLikes.text = post.likes.toString()
        txtDescription.text = post.description
        txtPosted.text = formatter.format(i)
        txtLoc.text = "Latitude: " + post.lat + "\nLongitude: " + post.lon

        /*
            Creates interaction with Maps
         */
        imgMap.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("geo:0,0?q=" + post.lat + "," + post.lon)
            })
        }

        /*
            Creates interaction with Share
         */
        imgShare.setOnClickListener {
            startActivity(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Hey, look at this post on Photi: https://photi.aylo.net/" + post.image)
            })
        }

    }
}