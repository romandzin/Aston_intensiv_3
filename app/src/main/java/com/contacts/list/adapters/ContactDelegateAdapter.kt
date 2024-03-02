package com.contacts.list.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.contacts.list.Contact
import com.contacts.list.MainActivity
import com.contacts.list.R


class ContactDelegateAdapter(var arrayList: ArrayList<Contact>, val context: Context): RecyclerView.Adapter<ContactDelegateAdapter.ContactViewHolder>() {

    var isEnable = (context as MainActivity).mainViewModel.deleteState
    private val deleteLocalCollection = (context as MainActivity).mainViewModel.deleteList.toMutableList()
    private var moveList = arrayList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bindData(arrayList[position])
        holder.itemView.setOnClickListener {
            if (isEnable) {
                clickItem(holder)
            }
            else {
                updateOldElement(holder.getId(), holder.adapterPosition)
            }
        }
        if (isEnable) {
            for (i in deleteLocalCollection) {
                if (i.id == holder.getId()) setCheckboxVisible(holder)
            }
        }
    }

    private fun updateOldElement(id: Int, position: Int) {
        (context as MainActivity).updateOldElement(id, position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        moveList = arrayList.toMutableList()
        val fromItem = moveList[fromPosition]
        moveList.removeAt(fromPosition)
        if (toPosition < fromPosition) {
            moveList.add(toPosition, fromItem)
        } else {
            moveList.add(toPosition, fromItem)
        }
    }

    private fun clickItem(holder: ViewHolder) {
        val s: Contact = arrayList[holder.adapterPosition]
        val currentHolder = holder as ContactViewHolder
        if (!currentHolder.checkBox.isChecked) {
            currentHolder.checkBox.isVisible = true
            currentHolder.checkBox.isChecked = true
            (context as MainActivity).mainViewModel.deleteList.add(s)
        } else {
            currentHolder.checkBox.isVisible = false
            currentHolder.checkBox.isChecked = false
            (context as MainActivity).mainViewModel.deleteList.remove(s)
        }
    }

    private fun setCheckboxVisible(holder: ContactViewHolder) {
        holder.checkBox.isVisible = true
        holder.checkBox.isChecked = true
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setData(oldList: ArrayList<Contact>, newList: ArrayList<Contact>) {
        val diffCallback = DiffCallback(oldList, newList)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        diffCourses.dispatchUpdatesTo(this)
        arrayList.clear()
        arrayList.addAll(newList)
        (context as MainActivity).mainViewModel.updateArrayList(newList)
        context.updateNewList()
    }

    fun useMoveData() {
        setData(arrayList, moveList as ArrayList<Contact>)
    }

    inner class ContactViewHolder(itemView: View): ViewHolder(itemView) {
        private val idTextView: TextView
        private val name: TextView
        private val secondName: TextView
        private val phone: TextView
        val checkBox: CheckBox

        init {
            idTextView = itemView.findViewById(R.id.contactId)
            name = itemView.findViewById(R.id.name)
            secondName = itemView.findViewById(R.id.secondName)
            phone = itemView.findViewById(R.id.phoneNumber)
            checkBox = itemView.findViewById(R.id.checkBox)
        }

        fun bindData(contact: Contact) {
            idTextView.text = contact.id.toString()
            name.text = contact.name
            secondName.text = contact.secondName
            phone.text = contact.phoneNumber
            checkBox.isVisible = false
            checkBox.isChecked = false
        }

        fun getId(): Int {
            return idTextView.text.toString().toInt()
        }
    }

    inner class DiffCallback(private val oldList: ArrayList<Contact>, private val newList: ArrayList<Contact>): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}