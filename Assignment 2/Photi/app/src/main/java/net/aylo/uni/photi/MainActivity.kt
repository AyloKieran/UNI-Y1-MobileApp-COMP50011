package net.aylo.uni.photi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    /*
            Creates prefs and editor for prefs - used for day/night mode setting
            Loads data from API
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = prefs.edit()

        if (prefs.getString("theme", "DARK") == "DARK") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        loadData(getCols())

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val cameraIntent = Intent(this, Camera::class.java)
            startActivity(cameraIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /*
        When an item is selected, it will override default actions so that I can set my own
            On dayNight -> change the local storage value to the opposite of what it is (adding the default if empty) and reloads the activity to get the change to propagate.
            on refresh -> call load data - which clears current recycler view and reloads from API
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dayNight ->
            {
                if (prefs.getString("theme", "LIGHT").toString() == "LIGHT") {
                    editor.putString("theme", "DARK")
                } else {
                    editor.putString("theme", "LIGHT")
                }
                editor.apply()
                this.recreate()
                true
            }
            R.id.action_refresh ->
            {
                loadData(getCols());
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /*
        Returns the number of columns for the recycler view based on the orientation of the device
     */
    private fun getCols(): Int {
        return if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            1
        } else {
            3
        }
    }

    /*
        Takes the number of columns for the recycler view and loads data from the API into it
            If response is successful it will clear any error text that may have previously existed as well as setting the recycler view to be visible before loading data into it
            On any failure it will make the recycler view invisible and set text in the correct text view

        Also handles the correct rendering of elements if there is an error to provide feedback to the user
     */
    private fun loadData(cols: Int) {
        val service = ServiceBuilder.buildService(PostService::class.java)
        val requestCall = service.getPost()

        requestCall.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    txtError.text = ""
                    postsRecyclerView.visibility = View.VISIBLE
                    postsRecyclerView.layoutManager = StaggeredGridLayoutManager(cols, 1)
                    postsRecyclerView.adapter = PostAdapter(response.body()!!)
                } else {
                    postsRecyclerView.visibility = View.INVISIBLE
                    txtError.text = "Something went wrong.\nError:\n\n${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                postsRecyclerView.visibility = View.INVISIBLE
                txtError.text = "You are offline.\nReconnect to the internet to view your feed."
            }
        })
    }
}