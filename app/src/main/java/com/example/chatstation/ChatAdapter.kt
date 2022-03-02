package com.example.chatstation

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatstation.model.ChatClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(var context:Context, var chat_list:ArrayList<ChatClass>):RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private  val MESSAGE_TYPE_LEFT = -1
    private  val IMAGE_TYPE_LEFT = -2
    private  val MESSAGE_TYPE_RIGHT = 1
    private  val IMAGE_TYPE_RIGHT = 2

    var mesg = 0

    var firebaseUser:FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == MESSAGE_TYPE_RIGHT) {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.user_right, parent, false)
            return ViewHolder(view)
        }
        else if(viewType == MESSAGE_TYPE_LEFT)
        {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.user_left, parent, false)
            return ViewHolder(view)
        }
        else if (viewType == IMAGE_TYPE_RIGHT)
        {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.image_layout_right, parent, false)
            return ViewHolder(view)
        }
        else
        {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.image_layout_left, parent, false)
            return ViewHolder(view)

        }
    }

    override fun getItemCount(): Int {
        return chat_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat_dis = chat_list[position]

        if(mesg == 1)
            holder.disp_username.text = chat_dis.message
        else
            Glide.with(context).load(chat_dis.url).into(holder.img)

    }


    class ViewHolder(view: View):RecyclerView.ViewHolder(view)
    {

        var disp_username:TextView = view.findViewById(R.id.tvMessage)

   //     var disp_image:CircleImageView = view.findViewById(R.id.profilepic)

        var img = view.findViewById<ImageView>(R.id.imageView)
    }

    override fun getItemViewType(position: Int): Int {
       firebaseUser = FirebaseAuth.getInstance().currentUser


        if(chat_list[position].senderid == firebaseUser!!.uid)
        {
            if(chat_list[position].type == "M")
            {
                mesg = 1;
                return  MESSAGE_TYPE_RIGHT

            }

            else
            {
                mesg = 0;
                return   IMAGE_TYPE_RIGHT
            }


        }
        else
        {
            if(chat_list[position].type == "M") {
                mesg = 1
                return MESSAGE_TYPE_LEFT
            }
            else
            {    mesg = 0
                return   IMAGE_TYPE_LEFT

            }
        }
    }
}
