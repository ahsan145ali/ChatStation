package com.example.chatstation

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatstation.model.UserClass
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(var context:Context,var user_list:ArrayList<UserClass>):RecyclerView.Adapter<UserAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.users_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return user_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user_dis = user_list[position]
        holder.textTemp.text = " "//user_dis.user_id
        holder.disp_username.text = user_dis.name
        Glide.with(context).load(user_dis.profileimage).placeholder(R.drawable.profilepic).into(holder.disp_image)

        holder.layoutUser.setOnClickListener{

            val intent  = Intent(context,ChatAcitvity::class.java)
            intent.putExtra("UserId",user_dis.user_id)
            intent.putExtra("UserName",user_dis.name)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        var disp_username:TextView = view.findViewById(R.id.user_username)
        var textTemp:TextView = view.findViewById(R.id.temp)
        var disp_image:CircleImageView = view.findViewById(R.id.profilepic)
        var layoutUser:LinearLayout = view.findViewById(R.id.layoutUser)

    }

}