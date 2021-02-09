package net.aylo.uni.photi

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import kotlinx.android.synthetic.main.post_layout.view.*

/*
    Implements and overrides default RecyclerView Adapter Methods to fit my specific API purposes

    Uses Glide for images - better performance, caching and rendering capabilities
 */
class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thePost = posts[position]

        val imagepath = "https://api.comp50011.uni.aylo.net/images/${thePost.image}"

        holder.title.text = thePost.title
        holder.description.text = thePost.description
        holder.likes.text = thePost.likes.toString()

        Glide.with(holder.itemView).load(imagepath).transform(CenterInside(), GranularRoundedCorners(42F, 42F, 0F, 0F)).into(holder.image)

        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, PostView::class.java).apply {
                putExtra("EXTRA_POST_DATA", thePost)
            })
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.txtTitle
        val image: ImageView = view.imgImage
        val description: TextView = view.txtDescription
        val likes: TextView = view.txtLikes
    }
}



