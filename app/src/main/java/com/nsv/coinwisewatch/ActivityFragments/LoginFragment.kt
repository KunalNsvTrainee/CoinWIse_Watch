package com.nsv.coinwisewatch.ActivityFragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.nsv.coinwisewatch.MainActivity
import com.nsv.coinwisewatch.R
import com.nsv.coinwisewatch.databinding.ActivityLoginBinding
import com.nsv.coinwisewatch.databinding.FragmentLoginBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects
import java.util.Random


class LoginFragment : Fragment() {
    private val firebase = FirebaseDatabase.getInstance()
    private val users = firebase.getReference("users")
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private val i = Intent()

    private var prog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvOpenSigninPage.setOnClickListener(View.OnClickListener {
            setLoginPage()
        })
        binding.tvOpenSignupPage.setOnClickListener(View.OnClickListener {
            setLoginPage()
        })
        auth = Firebase.auth


        binding.btnLogin.setOnClickListener(View.OnClickListener {
            if (binding.etEmail.text.toString().trim { it <= ' ' } == "") {
                binding.etEmail.error = "Enter Email Address"
            } else {
                if (binding.etPassword.text.toString().trim { it <= ' ' } == "") {
                    binding.etPassword.error = "Enter Password"
                } else {
                    auth.signInWithEmailAndPassword(
                        binding.etEmail.text.toString().trim { it <= ' ' },
                        binding.etPassword.text.toString().trim { it <= ' ' })
                        ?.addOnCompleteListener(requireActivity()) { param1 ->
                            val success = param1.isSuccessful
                            if (success) {
                                loadingdialog(false, "")
                                if (Objects.requireNonNull<FirebaseUser>(auth.currentUser).isEmailVerified) {
                                    i.setClass(requireContext(), MainActivity::class.java)
                                    startActivity(i)
                                } else {
                                    binding.etPassword.setText("")
                                    FirebaseAuth.getInstance().signOut()
                                    val BottomsheetD = BottomSheetDialog(requireContext())
                                    val BottomsheetV: View =
                                        layoutInflater.inflate(R.layout.dialog2, null)
                                    BottomsheetD.setContentView(BottomsheetV)
                                    val yes = BottomsheetV.findViewById<View>(R.id.yes) as TextView
                                    BottomsheetD.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                                        .setBackgroundResource(android.R.color.transparent)
                                    BottomsheetD.setCancelable(true)
                                    yes.setOnClickListener { BottomsheetD.dismiss() }
                                    BottomsheetD.show()
                                }
                            }

                        }
                    loadingdialog(true, "Please wait...")
                }
            }
        })

        binding.tvForgot.setOnClickListener(View.OnClickListener {
            if (binding.etEmail.text.toString().trim { it <= ' ' } == "") {
                binding.etEmail.requestFocus()
                binding.etEmail.setError("Enter Register Email Address")
            } else {
                auth.sendPasswordResetEmail(binding.etEmail.text.toString().trim { it <= ' ' })
                    .addOnCompleteListener { param1 ->
                        val success = param1.isSuccessful
                        if (success) {
                            loadingdialog(false, "")
                            val BottomsheetD = BottomSheetDialog(requireActivity())
                            val BottomsheetV: View = layoutInflater.inflate(R.layout.dialog3, null)
                            BottomsheetD.setContentView(BottomsheetV)
                            val yes = BottomsheetV.findViewById<View>(R.id.yes) as TextView
                            BottomsheetD.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                                .setBackgroundResource(android.R.color.transparent)
                            BottomsheetD.setCancelable(true)
                            yes.setOnClickListener { BottomsheetD.dismiss() }
                            BottomsheetD.show()
                        } else {
                            loadingdialog(false, "")
                        }
                    }

                loadingdialog(true, "Please wait...")
            }
        })

        binding.tvOpenSignupPage.setOnClickListener {
            binding.loginLayout.visibility = View.GONE
            binding.signupLayout.visibility = View.VISIBLE
        }
        binding.btnSignup.setOnClickListener(View.OnClickListener {
            if (binding.etFirstName.text.toString().trim { it <= ' ' } == "") {
                binding.etFirstName.error = "Enter First Name"
            } else {
                if (binding.etLastName.text.toString().trim { it <= ' ' } == "") {
                    binding.etLastName.error = "Enter Last Name"
                } else {
                    if (binding.etSignUpEmail.text.toString().trim { it <= ' ' } == "") {
                        binding.etSignUpEmail.error = "Enter Email Address"
                    } else {
                        if (binding.etphonenumber.text.toString()
                                .trim { it <= ' ' } == "" || binding.etphonenumber.text.toString()
                                .trim { it <= ' ' }.length != 10
                        ) {
                            binding.etphonenumber.error = "Enter valid Phonenumber"
                        } else {
                            if (binding.etPasswordSignup.text.toString().trim { it <= ' ' } == "") {
                                binding.etPasswordSignup.error = "Enter Password"
                            } else {
                                auth.createUserWithEmailAndPassword(
                                    binding.etSignUpEmail.text.toString(),
                                    binding.etPasswordSignup.text.toString()
                                ).addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        var usermap = HashMap<String, Any>()
                                        usermap["firstname"] =
                                            binding.etFirstName.text.toString().trim { it <= ' ' }
                                        usermap["lastname"] =
                                            binding.etLastName.text.toString().trim { it <= ' ' }
                                        usermap["email"] =
                                            binding.etSignUpEmail.text.toString().trim { it <= ' ' }
                                        usermap["phonenumber"] =
                                            binding.etphonenumber.text.toString().trim { it <= ' ' }
                                        usermap["password"] =
                                            binding.etPasswordSignup.text.toString()
                                                .trim { it <= ' ' }
                                        usermap["referralcode"] = getRefferalCode()
                                        usermap["account"] = getAccountNumber()
                                        usermap["avatar"] = "null"
                                        usermap["Free"] = "true"
                                        usermap["Silver"] = "false"
                                        usermap["Gold"] = "false"
                                        usermap["Diamond"] = "false"
                                        usermap["join"] = SimpleDateFormat("dd/MM/yyyy").format(
                                            Calendar.getInstance().getTime()
                                        )
                                        usermap["money"] = "0"
                                        usermap["balance"] = "0"
                                        usermap["block"] = "false"
                                        usermap["limit"] = "05"
                                        usermap["click"] = "0"
                                        Toast.makeText(
                                            context,
                                            "FirebaseAuth.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        usermap["uid"] =
                                            FirebaseAuth.getInstance().currentUser!!.uid
                                        users.child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .updateChildren(usermap)
                                        usermap.clear()
                                        loadingdialog(false, "")
                                        auth.currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener(OnCompleteListener<Void?> { })
                                        binding.etEmail.setText(
                                            binding.etSignUpEmail.getText().toString()
                                                .trim { it <= ' ' })
                                        binding.etPassword.setText(
                                            binding.etPasswordSignup.getText().toString()
                                                .trim { it <= ' ' })
                                        val BottomsheetD = BottomSheetDialog(requireContext())
                                        val BottomsheetV: View
                                        BottomsheetV = layoutInflater.inflate(R.layout.dialog, null)
                                        BottomsheetD.setContentView(BottomsheetV)
                                        val yes =
                                            BottomsheetV.findViewById<View>(R.id.yes) as TextView
                                        BottomsheetD.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                                            .setBackgroundResource(android.R.color.transparent)
                                        BottomsheetD.setCancelable(true)
                                        yes.setOnClickListener {
                                            BottomsheetD.dismiss()
                                            binding.loginLayout.visibility = View.VISIBLE
                                            binding.signupLayout.visibility = View.GONE
                                        }
                                        BottomsheetD.show()
                                    } else {
                                        loadingdialog(false, "")
                                    }


                                }

                            }
                            loadingdialog(true, "Please wait...")
                        }
                    }
                }
            }
        })
        binding.tvOpenSigninPage.setOnClickListener(View.OnClickListener {
            binding.loginLayout.visibility = View.VISIBLE
            binding.signupLayout.visibility = View.GONE
        })
    }


    private fun getRefferalCode(): Any {
        return binding.etFirstName.text.toString().trim { it <= ' ' }
            .substring(0, 1) + binding.etLastName.text.toString().trim { it <= ' ' }
            .substring(0, 1).uppercase(
                Locale.getDefault()
            ) + (getRandom(
            0, 99999
        ).toLong()).toString()

    }

    private fun getAccountNumber(): Any {
        return (getRandom(0, 9).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString() + (getRandom(
            0, 9
        ).toLong()).toString()
    }

    private fun initializeLogic() {
        binding.etFirstName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
        binding.etLastName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
        binding.signupLayout.visibility = View.GONE

    }


    fun loadingdialog(ifShow: Boolean, title: String) {
        if (ifShow) {
            if (prog == null) {
                prog = ProgressDialog(context)
                prog!!.max = 100
                prog!!.isIndeterminate = true
                prog!!.setCancelable(false)
                prog!!.setCanceledOnTouchOutside(false)
            }
            prog!!.setMessage(title)
            prog!!.show()
        } else {
            prog?.dismiss()
        }
    }


    fun getRandom(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min + 1) + min
    }

    private fun setLoginPage() {
        if (binding.signupLayout.visibility == View.VISIBLE) {
            binding.signupLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
        } else {
            binding.signupLayout.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.GONE
        }
    }
}