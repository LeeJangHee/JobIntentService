package com.example.jobintentservice.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.jobintentservice.BlankFragment
import com.example.jobintentservice.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {


    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        textView.text = viewModel.getTextString()

        buttonMain.setOnClickListener {
            Toast.makeText(requireContext(), "main", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, BlankFragment())
//                    .addToBackStack(null)
                    .commit()
        }

    }
}