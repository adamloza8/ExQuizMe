package com.example.quizjava.MVVM

import androidx.lifecycle.ViewModel
import com.example.quizjava.MVVM.FirebaseRepository.OnFireStoreDataAdded
import androidx.lifecycle.MutableLiveData
import com.example.quizjava.Model.QuizModel
import com.example.quizjava.MVVM.FirebaseRepository
import androidx.lifecycle.LiveData
import java.lang.Exception

class QuizViewModel : ViewModel(), OnFireStoreDataAdded {
    var quizModelListData = MutableLiveData<List<QuizModel>>()
    var firebaseRepo = FirebaseRepository(this)
    val liveDatafromFireStore: LiveData<List<QuizModel>>
        get() = quizModelListData

    override fun quizDataAdded(quizModelList: List<QuizModel>) {
        quizModelListData.value = quizModelList
    }

    override fun OnError(e: Exception) {}

    init {
        firebaseRepo.getDataFromFireStore()
    }
}