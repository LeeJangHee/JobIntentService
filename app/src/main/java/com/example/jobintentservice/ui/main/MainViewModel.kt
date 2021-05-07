package com.example.jobintentservice.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var _test: MutableLiveData<Int> = MutableLiveData()
    private val test get() = _test


    init {
        _test.value = 0
    }

    fun setTest(index: Int) {
        _test.value = index
    }

    fun getTest(): LiveData<Int> {
        return test
    }

    fun getTextString(): String {
        return test.value.toString()
    }
}