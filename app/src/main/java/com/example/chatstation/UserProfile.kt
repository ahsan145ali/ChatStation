package com.example.chatstation

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatstation.model.UserClass
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.activity_home_page.backimg
import kotlinx.android.synthetic.main.activity_home_page.dp
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.net.URI
import java.util.*

class UserProfile : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var DBref: DatabaseReference
    private var filePath:Uri?=null
    private val choose_image_request:Int = 2021
    private lateinit var online_storage:FirebaseStorage
    private lateinit var online_storage_Ref:StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        DBref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        online_storage = FirebaseStorage.getInstance()
        online_storage_Ref = online_storage.reference

        DBref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user_check = snapshot.getValue(UserClass::class.java)
                user_username.setText(user_check!!.name)

                if (user_check.profileimage == "")
                {
                    dp.setImageResource(R.drawable.profilepic)

                }
                else
                {
                    Glide.with(this@UserProfile).load(user_check.profileimage).into(dp)
                }
            }


        })
        backimg.setOnClickListener({ // click listener on back arrow image

            onBackPressed()
        })
        dp.setOnClickListener({
                ChooseImageFromGallery()
          /*  var p: Intent = Intent(this,HomePage::class.java)
            startActivity(p)
            finish() */

        })
        savebtn.setOnClickListener{
            UploadImage()
            progressbar.visibility = View.VISIBLE
        }


    }

    private fun ChooseImageFromGallery()
    {
        val picIntent:Intent = Intent()
        picIntent.type= "image/*"
        picIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(picIntent,"Choose Image"),choose_image_request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == choose_image_request && requestCode != null)
        {
            filePath = data!!.data

                try
                    {
                        var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                        dp.setImageBitmap(bitmap)

                        savebtn.visibility = View.VISIBLE
                    }
                catch (e:IOException)
                    {
                        e.printStackTrace()
                    }

        }
    }

    private fun UploadImage()
    {
            if(filePath!=null)
            {
                var ref:StorageReference = online_storage_Ref.child("img/"+UUID.randomUUID().toString())
                ref.putFile(filePath!!).addOnSuccessListener{


                    // update children in Database
                    var store:HashMap<String,String> = HashMap()
                    store.put("name",user_username.text.toString())
                    store.put("profileimage",filePath.toString())
                    DBref.updateChildren(store as Map<String, Any>)

                                 progressbar.visibility = View.GONE
                                 Toast.makeText(applicationContext, "Upload Complete ", Toast.LENGTH_SHORT).show()
                                 savebtn.visibility = View.GONE

                        }
                    .addOnFailureListener{

                        progressbar.visibility = View.GONE
                                Toast.makeText(applicationContext, "Upload Failed " + it.message, Toast.LENGTH_SHORT).show()

                    }


            }
    }


}