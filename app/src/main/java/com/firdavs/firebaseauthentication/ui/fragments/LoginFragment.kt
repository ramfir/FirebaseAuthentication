package com.firdavs.firebaseauthentication.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.firdavs.firebaseauthentication.R
import com.firdavs.firebaseauthentication.databinding.FragmentLoginBinding
import com.firdavs.firebaseauthentication.viewmodels.MainActivityViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupProgressDialog()

        binding.getCodeBtn.setOnClickListener { getCode() }

        sharedViewModel.codeSent.observe(viewLifecycleOwner) {
            if (it) {
                codeSentSuccessfully()
            }
        }
        sharedViewModel.verificationFailed.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Загрузка...")
        progressDialog.setCanceledOnTouchOutside(false)
    }
    private fun getCode() {
        val phone = "+7${binding.maskedEditText.unmaskedText}"
        progressDialog.setMessage("Проверка номера")
        progressDialog.show()
        sharedViewModel.startPhoneNumberVerification(phone, requireActivity())
    }
    private fun codeSentSuccessfully() {
        progressDialog.dismiss()
        Toast.makeText(activity, "Код был отправлен", Toast.LENGTH_SHORT).show()
        val phone = "+7${binding.maskedEditText.unmaskedText}"
        sharedViewModel.firebaseUser.phone = phone
        binding.getCodeBtn.findNavController().navigate(R.id.action_loginFragment_to_verifyCodeFragment)
    }
}