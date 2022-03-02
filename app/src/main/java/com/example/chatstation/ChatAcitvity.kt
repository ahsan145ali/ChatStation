package com.example.chatstation

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatstation.model.ChatClass
import com.example.chatstation.model.NotificationData
import com.example.chatstation.model.PushNotification
import com.example.chatstation.model.UserClass
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat_acitvity.*
import kotlinx.android.synthetic.main.activity_chat_acitvity.dp
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

//import kotlinx.android.synthetic.main.activity_home_page.*

//import kotlinx.android.synthetic.main.activity_user_profile.*
//import kotlinx.android.synthetic.main.activity_home_page.*

class ChatAcitvity : AppCompatActivity() {

    var firebaseUser:FirebaseUser? = null
    var DBref:DatabaseReference? = null
    var storageRef: StorageReference? = null

    var chat_list  = ArrayList<ChatClass>()
    val RequestCode = 438
    var imageUri: Uri?= null

    var Rid:String ?= null

    var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_acitvity)

        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)


        var intent = getIntent()
        var userid = intent.getStringExtra("UserId")
        var userName = intent.getStringExtra("UserName")

        Rid = userid

        firebaseUser = FirebaseAuth.getInstance().currentUser
        DBref = FirebaseDatabase.getInstance().getReference("Users").child(userid!!)

        backimgg.setOnClickListener({ // click listener on back arrow image

            onBackPressed()
        })

        DBref!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(UserClass::class.java)
                tvUserName.text = user!!.name
                if (user.profileimage == "")
                {
                    dp.setImageResource(R.drawable.profilepic)
                }
                else
                {
                    Glide.with(this@ChatAcitvity).load(user.profileimage).into(dp)
                }
            }

        })

        btnSendImage.setOnClickListener({

            storageRef = FirebaseStorage.getInstance().reference.child("UserImages")
            pickImage();

        })

        btnSendMessage.setOnClickListener({
            var message:String = etMessage.text.toString()

            if(message.isEmpty())
            {
                Toast.makeText(this, "Please Type Something", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            }
            else
            {
                SendMessage(firebaseUser!!.uid,userid,message,"M","")
                etMessage.setText("")



//                topic = "/topics/$userid"
//                PushNotification(NotificationData(userName!!,message),topic).also {
//                    sendNotification(it)
//
//                }

            }
        })



        ReadMessage(firebaseUser!!.uid,userid)
    }
    private fun SendMessage(send_id:String,reciever_id:String,message:String,t:String,url_:String)
    {
        var DBref:DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        var chat_hashmap:HashMap<String,String> = HashMap()

        chat_hashmap.put("senderid",send_id)
        chat_hashmap.put("receiverdid",reciever_id)
        chat_hashmap.put("message",message)
        chat_hashmap.put("type",t)
        chat_hashmap.put("url",url_)
        DBref!!.child("Chat").push().setValue(chat_hashmap)

    }


    fun ReadMessage(sender_id:String,reciever_id:String)
    {
        val databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chat_list.clear()
                for(dataSnapShot:DataSnapshot in snapshot.children)
                {
                    val chat_check = dataSnapShot.getValue(ChatClass::class.java)

                    if(chat_check!!.senderid.equals(sender_id) && chat_check!!.receiverdid.equals(reciever_id) ||
                        chat_check!!.senderid.equals(reciever_id) && chat_check!!.receiverdid.equals(sender_id) )
                    {
                            chat_list.add(chat_check)
                    }
                }
                var chat_adapter = ChatAdapter(this@ChatAcitvity,chat_list)
                chatRecyclerView.adapter = chat_adapter

                chatRecyclerView.scrollToPosition(chat_list.size -1)

            }
            })
    }   // Read Message Ends here




    fun pickImage()
    {
        val intent  = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null)
        {
            imageUri = data.data
            Toast.makeText(this, "you iamge is uploading", Toast.LENGTH_SHORT).show()
            uploadImage()

        }

    }



    fun uploadImage()
    {

        val pdialoge =  ProgressDialog(this)
        pdialoge.setTitle("Please wait")
        pdialoge.setMessage("Image is uploading")
        pdialoge.show();

        if(imageUri!= null)
        {

            val sender = firebaseUser!!.uid
            val fileRef = storageRef!!.child(sender.toString()+"_"+System.currentTimeMillis().toString()+".jpg")

             var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)


            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful)
                    task.exception?.let{

                        throw it

                    }
                else
                    return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener({
                    task ->
                if(task.isSuccessful)
                {

                    Toast.makeText(this, "UPLOADED task is Succesfull", Toast.LENGTH_SHORT).show()
                    val url = task.result.toString()

                    if(Rid != null)
                        SendMessage(firebaseUser!!.uid, Rid!!,"","I",url)

                    pdialoge.dismiss()

                }

            })
        }
    }  // upload Image ends here



    private  fun sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch {

        try {

            val response = RetrofitInstance.api.postNotification(notification)

            if(response.isSuccessful)
            {
                Toast.makeText(this@ChatAcitvity, "Response: ${Gson().toJson(response)}", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this@ChatAcitvity, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
        }catch (e:Exception)
        {

    //        Toast.makeText(this@ChatAcitvity, "IT is in E message", Toast.LENGTH_SHORT).show()
//            Toast.makeText(this@ChatAcitvity, ""+e.message, Toast.LENGTH_SHORT).show()
        }

    }



}