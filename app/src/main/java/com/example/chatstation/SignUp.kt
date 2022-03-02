package com.example.chatstation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.sign

class SignUp : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var DBref:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        var input_name:EditText = findViewById(R.id.enter_name)
        var input_email:EditText = findViewById(R.id.enter_email)
        var input_pass:EditText = findViewById(R.id.enter_password)
        var input_confrimpass:EditText = findViewById(R.id.enter_confirmpassword)
        var signup_but:Button = findViewById(R.id.signup)
        var sigin_but:Button = findViewById(R.id.signin)

        auth = FirebaseAuth.getInstance()

        signup_but.setOnClickListener({

            var name  =  input_name.text.toString()
            var email = input_email.text.toString()
            var pass = input_pass.text.toString()
            var confirm_pass = input_confrimpass.text.toString()

            if(name.length > 0 && email.length > 0 && pass.length > 0 && confirm_pass.length > 0) // Check if all the fields are filled
            {
                if (pass != confirm_pass)  // Check if Passwords match
                {
                    Toast.makeText(this, "Passwords Do Not Match!", Toast.LENGTH_SHORT).show()
                }
                else // proceed to signup function
                {
                    UserRegistration(name,email,pass)
                }
            }
            else // Give Error that all fields are not filled
            {
                Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_SHORT).show()
            }

        })

        sigin_but.setOnClickListener({
            var sigin_in:Intent = Intent(this,SignIn::class.java)
            startActivity(sigin_in)
            finish()
        })

    } // OnCreate Ends

    fun UserRegistration(name:String,email:String , pass:String)
    {
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this){
                if(it.isSuccessful)
                {
                    Toast.makeText(this, "SignUp Successful \n Now You Can SignIn", Toast.LENGTH_SHORT).show()
                    var user:FirebaseUser? = auth.currentUser
                    var UserID:String = user!!.uid

                    // To Store in DataBase
                    DBref = FirebaseDatabase.getInstance().getReference("Users").child(UserID)
                    var store:HashMap<String,String> = HashMap()
                    store.put("profileimage","")
                    store.put("user_id",UserID)
                    store.put("name",name)
                    store.put("Password",pass)
                    store.put("email",email)
                    DBref.setValue(store).addOnCompleteListener(this)
                    {
                        if(it.isSuccessful)
                        {
                                        //Toast.makeText(this, "Now You can SignIn", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(Intent(this,HomePage::class.java)))

                        }
                    }
                }
              else
                {
                    Toast.makeText(this, "SignUp Failed \n " +it.exception.toString() , Toast.LENGTH_LONG).show()
                }
            }
    }


} // Class Ends