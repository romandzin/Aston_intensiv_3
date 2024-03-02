package com.contacts.list

import android.os.Parcelable
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val arrayList: MutableList<Contact> =
        arrayListOf(Contact(1, "Nikita", "Romanenko", "789321"))
    val newList: MutableList<Contact> = mutableListOf()
    val deleteList: MutableList<Contact> = mutableListOf()
    var deleteState = false
    var index = 101
    var recyclerState: Parcelable? = null

    init {
        for (i in 2..100) {
            val contact = Contact(i, "Nikit$i", "Roma$i", "781432$i")
            arrayList.add(contact)
        }
    }

    fun updateIndex() {
        index = if (getArrayList().isEmpty()) 1
        else getArrayList().maxOf { it.id } + 1
    }

    fun getArrayList(): MutableList<Contact> {
        return arrayList
    }

    fun updateArrayList(list: MutableList<Contact>) {
        arrayList.clear()
        arrayList.addAll(list)
    }
}