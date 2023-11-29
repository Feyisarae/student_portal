package com.example.studentportal.view.homeFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studentportal.R
import com.example.studentportal.roomDataBase.CardModel
import javax.inject.Inject

class HomeViewModel (
    private val context: Context
) : ViewModel() {
    internal var selectedItem: CardModel? = null

    fun getModules(): ArrayList<CardModel> {
        val items = ArrayList<CardModel>().apply {
            add(
                CardModel(
                    context.getString(R.string.register_student),
                    R.drawable.ic_add_students
                )
            )
            add(
                CardModel(
                    context.getString(R.string.view_student_profile),
                    R.drawable.ic_student_profile
                )
            )
            add(
                CardModel(
                    context.getString(R.string.analytics_view),
                    R.drawable.analytics_view
                )
            )
        }
        return items
    }

    class HomeViewModelFactory @Inject constructor(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(context) as T
        }
    }
}