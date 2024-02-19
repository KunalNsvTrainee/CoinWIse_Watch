package com.nsv.coinwisewatch.ActivityFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nsv.coinwisewatch.R
import com.nsv.coinwisewatch.databinding.FragmentMembershipBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject

class MembershipFragment : Fragment(R.layout.fragment_membership),PaymentResultListener{
    private lateinit var binding: FragmentMembershipBinding
    private var amount:Float = 0F
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentMembershipBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arrayOf(100, 200, 300)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner1.adapter = adapter

        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) { // Handle item selection
                val selectedItem = parent.getItemAtPosition(position).toString()
                amount = selectedItem.toFloat()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.button1.setOnClickListener{
            val checkout = Checkout()
            checkout.setKeyID("Enter your key id here")
            checkout.setImage(R.drawable.income)
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", "CoinWise Earn")
                jsonObject.put("description", "Membership")
                jsonObject.put("currency", "INR")
                jsonObject.put("theme.color", "#F47F3C")
                jsonObject.put("amount", (amount*100).toInt())
                jsonObject.put("prefill.contact", "7779015927")
                jsonObject.put("prefill.email", "kunalsabhadiya50@gmail.com")
                checkout.setKeyID("rzp_test_8AobmysSu7HnCd")
                checkout.open(requireActivity(), jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(requireContext(),"payment succesfull",Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
    }

}