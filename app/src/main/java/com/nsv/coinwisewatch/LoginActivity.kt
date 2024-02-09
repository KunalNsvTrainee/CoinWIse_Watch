package com.nsv.coinwisewatch

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nsv.coinwisewatch.databinding.ActivityLoginBinding
import java.util.Calendar

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding

    private var sp: SharedPreferences? = null

    private var prog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
         initialization();
        binding.tvOpenSigninPage.setOnClickListener(View.OnClickListener { v ->
            setLoginPage()
        })
        binding.tvOpenSignupPage.setOnClickListener(View.OnClickListener { v->
            setLoginPage()
        })
    }

    private fun initialization() {
        sp = getSharedPreferences("tme", MODE_PRIVATE)
    }

    fun setLoginPage(){
        if (binding.signupLayout.visibility == View.VISIBLE) {
            binding.signupLayout.visibility = View.GONE
            binding.loginLayout.visibility =View.VISIBLE
        } else {
            binding.signupLayout.visibility = View.VISIBLE
            binding.loginLayout.visibility = View.GONE
        }
    }

    private fun initializeLogic() {
        binding.scroll.isFillViewport = true
        binding.scroll.isVerticalScrollBarEnabled = false
        binding.etFirstName.filters = arrayOf<InputFilter>(LengthFilter(8))
        binding.etLastName.filters = arrayOf<InputFilter>(LengthFilter(8))
        binding.signupLayout.visibility = View.GONE
        binding.etRefferalCode.visibility = View.GONE
        binding.textview2.setTypeface(
            Typeface.createFromAsset(assets, "fonts/googlesansbold.ttf"),
            Typeface.BOLD
        )
        binding.textview6.setTypeface(
            Typeface.createFromAsset(assets, "fonts/googlesansbold.ttf"),
            Typeface.BOLD
        )
        _SX_CornerRadius_card(binding.btnLogin, "#F47F3C", 12.0)
        _SX_CornerRadius_card(binding.btnSignup, "#F47F3C", 12.0)
        _SX_CornerRadius_card(binding.etEmail, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etPassword, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etFirstName, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etLastName, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etRefferalCode, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etPasswordSignup, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etSignUpEmail, "#FFFFFF", 12.0)
        _SX_CornerRadius_card(binding.etphonenumber, "#FFFFFF", 12.0)
    }
    fun _rippleRoundStroke(view: View, focus: String?, pressed: String?, round: Double, stroke: Double, strokeclr: String) {
        val GG = GradientDrawable()
        GG.setColor(Color.parseColor(focus))
        GG.cornerRadius = round.toFloat()
        GG.setStroke(
            stroke.toInt(),
            Color.parseColor("#" + strokeclr.replace("#", ""))
        )
        val RE = RippleDrawable(
            ColorStateList(
                arrayOf(intArrayOf()),
                intArrayOf(Color.parseColor("#FF757575"))
            ), GG, null
        )
        view.background = RE
        view.elevation = 5f
    }
    fun _getTime() {
        sp?.edit()?.putString("time_", Calendar.getInstance().getTimeInMillis().toString())?.apply()
    }
    fun _SX_CornerRadius_card(_view: View, _color: String?, _value: Double) {
        val gd = GradientDrawable()
        gd.setColor(Color.parseColor(_color))
        gd.cornerRadius = _value.toInt().toFloat()
        _view.background = gd
        _view.elevation = 5f
    }

    fun _loadingdialog(_ifShow: Boolean, _title: String) {
        if (_ifShow) {
            if (prog == null) {
                prog = ProgressDialog(this)
                prog!!.max = 100
                prog!!.isIndeterminate = true
                prog!!.setCancelable(false)
                prog!!.setCanceledOnTouchOutside(false)
            }
            prog!!.setMessage(_title)
            prog!!.show()
        } else {
            prog?.dismiss()
        }
    }
}