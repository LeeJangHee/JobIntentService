package com.example.jobintentservice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jobintentservice.ui.main.MainFragment
import com.example.jobintentservice.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlin.random.Random


class BlankFragment : Fragment() {

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        var num: Int = viewModel.getTextString().toInt()

        viewModel.getTest().observe(viewLifecycleOwner, object : Observer<Int> {
            override fun onChanged(t: Int?) {
                if (t != num) {
                    Toast.makeText(requireContext(), "blank", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, MainFragment())
//                .addToBackStack(null)
                        .commit()
                }
            }
        })


        buttonBlank.setOnClickListener {
            viewModel.setTest(Random.nextInt(0, 100))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            BlankFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}