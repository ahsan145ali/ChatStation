package com.example.chatstation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatstation.model.UserClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.activity_home_page.backimg
import kotlinx.android.synthetic.main.activity_home_page.dp
import kotlinx.android.synthetic.main.activity_user_profile.*

class HomePage : AppCompatActivity() {

    var List_of_users  = ArrayList<UserClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }


        recycleview.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)

        backimg.setOnClickListener({ // click listener on back arrow image

            onBackPressed()
        })
        dp.setOnClickListener({

            var p: Intent = Intent(this,UserProfile::class.java)
            startActivity(p)

        })

        getUsersList()

    } // on crete ends here



    fun getUsersList()
    {
        val reg_user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        var userid = reg_user.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        databaseReference.addValueEventListener(object:ValueEventListener{
              override fun onCancelled(error: DatabaseError) {
                  Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
              }

              override fun onDataChange(snapshot: DataSnapshot) {
                  List_of_users.clear()
                  val current_User = snapshot.getValue(UserClass::class.java)
                  if (current_User!!.profileimage == "")
                  {
                      dp.setImageResource(R.drawable.profilepic)
                  }
                  else
                  {
                      Glide.with(this@HomePage).load(current_User.profileimage).into(dp)
                  }
                  for(dataSnapShot:DataSnapshot in snapshot.children)
                  {
                     val user_check = dataSnapShot.getValue(UserClass::class.java)


                      //Toast.makeText(this@HomePage, "user: "+ user!!.user_id, Toast.LENGTH_LONG).show()
                        if(dataSnapShot.key != reg_user.uid)
                        {
                            if (user_check != null) {

                                List_of_users.add(user_check)
                            }

                        }
                  }

                  var User_Adapter = UserAdapter(this@HomePage,List_of_users)
                  recycleview.adapter = User_Adapter
              }


          }) // call ends here
    }

} // class ends here




