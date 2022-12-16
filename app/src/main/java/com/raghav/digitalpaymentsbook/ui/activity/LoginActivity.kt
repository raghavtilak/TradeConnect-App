package com.raghav.digitalpaymentsbook.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.raghav.digitalpaymentsbook.data.model.Customer
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityLoginBinding
import com.raghav.digitalpaymentsbook.ui.dialog.ChooseRoleDialog
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.util.Constants
import com.raghav.digitalpaymentsbook.util.NetworkUtils
import com.raghav.digitalpaymentsbook.util.add
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    lateinit var binding: ActivityLoginBinding
    val dialog = LoadingDialog()
    val auth = FirebaseAuth.getInstance()
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            dialog.dismiss()

            val code: String? = credential.smsCode

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                binding.editTextOtp.text = Editable.Factory.getInstance().newEditable(code)
                //verifying the code
                verifyVerificationCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {

            dialog.dismiss()
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(
                    this@LoginActivity,
                    "Can't verify phone right now",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(
                    this@LoginActivity,
                    "Can't verify phone right now",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            dialog.dismiss()
            binding.loginRelative.visibility = View.GONE
            binding.otpRelative.visibility = View.VISIBLE


            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginRelative.visibility = View.VISIBLE
        binding.otpRelative.visibility = View.GONE
        binding.nextBtn.isEnabled = false
        binding.nextBtn.isActivated = false

        binding.editTextPhone.doOnTextChanged { text, start, before, count ->
            if (text?.length != 10) {
                binding.TextFieldPhone.isErrorEnabled = true
                binding.nextBtn.isEnabled = false
                binding.nextBtn.isActivated = false
                binding.TextFieldPhone.error = "Enter a valid phone number"
            } else {
                binding.nextBtn.isEnabled = true
                binding.nextBtn.isActivated = true
                binding.TextFieldPhone.isErrorEnabled = false
            }
        }

        binding.nextBtn.setOnClickListener {
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "loading")
            binding.editTextPhone.text?.let {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$it")
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        binding.verifyBtn.setOnClickListener {
            verifyVerificationCode(binding.editTextOtp.text.toString())
        }

    }

    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        dialog.show(supportFragmentManager, "loading")

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user = task.result?.user

                    val map = mutableMapOf<String?, RequestBody?>()
                    if (user?.phoneNumber != null)
                        map["phone"] = user.phoneNumber?.let { it1 ->
                            RequestBody
                                .create(MediaType.parse("text/plain"), it1.substring(1))
                        }
                    map["name"] = RequestBody.create(MediaType.parse("text/plain"), "Undefined")

                    val handler = CoroutineExceptionHandler { _, throwable ->
                        Log.d("TAG", "ERROR- ${throwable.message}")
                    }

                    //testing
//                    dialog.dismiss()
//                    val roleDialog = ChooseRoleDialog()
//                    roleDialog.show(supportFragmentManager,"roleDialog")
//                    roleDialog.isCancelable=false


                    if (NetworkUtils.isInternetAvailable(this)) {

                        lifecycleScope.launch(handler) {

                            if (user != null && user.phoneNumber != null) {

                                val result = RetrofitHelper.userAPI.getUser(user.phoneNumber!!.substring(1))

                                //user already has an account as customer
                                if(result.isSuccessful && result.body()!=null){
                                    dialog.dismiss()
                                    val sharedPref =
                                        getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

                                    val u = result.body()!!
                                    if(u.role == Constants.CUSTOMER_STR){
                                        sharedPref.add(Customer(u.name,u.password,u.phone,u.address,u.id))
                                    }else{
                                        sharedPref.add(Retailer(u.name,u.password,u.phone,u.address,u.shopName,u.id))
                                    }

                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }else{
                                    dialog.dismiss()
                                    val roleDialog = ChooseRoleDialog()
                                    roleDialog.show(supportFragmentManager,"roleDialog")
                                    roleDialog.isCancelable=false
                                }
                            }
                        }
                    } else {

                        dialog.dismiss()
                        Toast.makeText(
                            this@LoginActivity,
                            "Please make sure you have internet access",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    dialog.dismiss()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.TextFieldOtp.isErrorEnabled = true
                        binding.TextFieldOtp.error = "Invalid OTP"
                    }
                }
            }
    }
}