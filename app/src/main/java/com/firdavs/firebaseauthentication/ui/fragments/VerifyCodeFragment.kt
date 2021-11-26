package com.firdavs.firebaseauthentication.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.firdavs.firebaseauthentication.R
import com.firdavs.firebaseauthentication.databinding.FragmentVerifyCodeBinding
import com.firdavs.firebaseauthentication.viewmodels.MainActivityViewModel
import java.util.concurrent.TimeUnit

class VerifyCodeFragment : Fragment() {

    private lateinit var binding: FragmentVerifyCodeBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)

        setupProgressDialog()
        subscribeToTimer()

        binding.continueBtn.setOnClickListener { sendCodeToFirebase() }
        binding.resendBtn.setOnClickListener { resendCodeFromFirebase() }

        sharedViewModel.resetCodeSent()
        sharedViewModel.addOnSuccessListener.observe(viewLifecycleOwner) {
            if (it) {
                rightCodeSent()
            }
        }
        sharedViewModel.addOnFailureListener.observe(viewLifecycleOwner) { wrongCodeSent(it) }
        sharedViewModel.codeSent.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog.dismiss()
                Toast.makeText(activity, "Код был отправлен", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Загрузка...")
        progressDialog.setCanceledOnTouchOutside(false)
    }
    private fun subscribeToTimer() {
        sharedViewModel.currentTime.observe(viewLifecycleOwner) {
            val mm = TimeUnit.MILLISECONDS.toMinutes(it) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(it))
            val ss = TimeUnit.MILLISECONDS.toSeconds(it) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it))
            println("menii it=$it mm=$mm ss=$ss")
            binding.resendBtn.text = "Отправить еще раз через ${String.format("%02d:%02d", mm, ss)}"
        }
        sharedViewModel.isTimerFinished.observe(viewLifecycleOwner) {
            if (it) {
                binding.resendBtn.isEnabled = true
                binding.resendBtn.text = "Отправить еще раз"
            } else {
                binding.resendBtn.isEnabled = false
            }
        }
    }
    private fun sendCodeToFirebase() {
        val code = binding.codeEdText.text.toString().trim()
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(activity, "Введите код", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setMessage("Идентификация кода...")
            progressDialog.show()
            sharedViewModel.verifyPhoneNumberWithCode(sharedViewModel.firebaseUser.verificationId, code)
        }
    }
    private fun resendCodeFromFirebase() {
        sharedViewModel.isTimerStarted = false
        sharedViewModel.startTimer()
        progressDialog.setMessage("Повторная отправка кода...")
        progressDialog.show()
        sharedViewModel.resendVerificationCode(
            sharedViewModel.firebaseUser.phone,
            sharedViewModel.firebaseUser.token,
            requireActivity()
        )
    }
    private fun rightCodeSent() {
        progressDialog.dismiss()
        binding.continueBtn.findNavController()
            .navigate(R.id.action_verifyCodeFragment_to_profileFragment)
    }
    private fun wrongCodeSent(ex: Exception) {
        progressDialog.dismiss()
        Toast.makeText(activity, "${ex.message}", Toast.LENGTH_SHORT).show()
    }
    override fun onStart() {
        super.onStart()
        sharedViewModel.startTimer()
    }
}