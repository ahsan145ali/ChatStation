package com.example.chatstation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        var enter_email:EditText = findViewById(R.id.enter_email_signin)
        var enter_pass:EditText = findViewById(R.id.enter_password_signin)
        var login_but:Button = findViewById(R.id.login)
        var sigin_up_But:Button = findViewById(R.id.sign_up_again)

        auth = FirebaseAuth.getInstance()
     //  firebaseUser = auth.currentUser!!

       if(auth.currentUser!=null) // check if user is already signed in then navigate to home page
        {
            var shift_to_homepage:Intent = Intent(this,HomePage::class.java)
            startActivity(shift_to_homepage)
            finish()
        }

        login_but.setOnClickListener({
            var email = enter_email.text.toString()
            var pass = enter_pass.text.toString()

            if(email.length > 0 && pass.length > 0)
            {
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this)
                {
                    if(it.isSuccessful)
                    {
                        // GO TO HomePage
                        var shift_to_homepage:Intent = Intent(this,HomePage::class.java)
                        startActivity(shift_to_homepage)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, "Email or Password Invalid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_SHORT).show()
            }

        })

        sigin_up_But.setOnClickListener({

            var signUp_agai:Intent = Intent(this,SignUp::class.java)
            startActivity(signUp_agai)
            finish()

        })
    } // oncreate ends

}// class ends