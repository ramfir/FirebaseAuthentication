package com.firdavs.firebaseauthentication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.firdavs.firebaseauthentication.databinding.FragmentProfileBinding
import com.firdavs.firebaseauthentication.viewmodels.MainActivityViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        fillFirebaseCredentials()

        makeRequest()
        observeResponse()

        setupProgressBar()

        return binding.root
    }
    private fun fillFirebaseCredentials() {
        val phone = sharedViewModel.firebaseAuth.currentUser.phoneNumber
        Toast.makeText(activity, "Вы вошли как $phone", Toast.LENGTH_SHORT).show()
        binding.profileTxtView.text = "Your phone: ${sharedViewModel.firebaseUser.phone}\n\n" +
                "Your verificationId: ${sharedViewModel.firebaseUser.verificationId}\n\n" +
                "Your token: ${sharedViewModel.firebaseUser.token}"
    }
    private fun makeRequest() {
        sharedViewModel.checkUser(sharedViewModel.firebaseUser.phone, sharedViewModel.firebaseUser.verificationId)
    }
    private fun observeResponse() {
        sharedViewModel.response.observe(requireActivity()) {
            binding.backendTxtView.text = "Request code: ${it.code()}\n\nRequest body: ${it.body()}"
        }
    }
    private fun setupProgressBar() {
        sharedViewModel.hideProgressBar.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }
}