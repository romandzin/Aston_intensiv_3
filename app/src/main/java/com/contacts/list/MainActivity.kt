package com.contacts.list

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.contacts.list.adapters.ContactDelegateAdapter
import com.contacts.list.databinding.ActivityMainBinding
import com.contacts.list.databinding.LayoutDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var newContact = Contact()
    private lateinit var contactAdapter: ContactDelegateAdapter

    private val itemTouchHelper by lazy {
        val itemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val recyclerviewAdapter = recyclerView.adapter as ContactDelegateAdapter
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    recyclerviewAdapter.moveItem(fromPosition, toPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    val recyclerviewAdapter = recyclerView.adapter as ContactDelegateAdapter
                    recyclerviewAdapter.useMoveData()
                }
            }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        contactAdapter =
            ContactDelegateAdapter(mainViewModel.getArrayList() as ArrayList<Contact>, this)
        itemTouchHelper.attachToRecyclerView(binding.contactRecyclerView)
        updateNewList()
        binding.addButton.setOnClickListener {
            addNewElement()
        }
        binding.deleteButton.setOnClickListener {
            if (contactAdapter.isEnable) {
                onStandartUI()
            } else {
                onDeleteUI()
            }
        }
        binding.cancelButton.setOnClickListener {
            onStandartUI()
        }
        if (mainViewModel.deleteState) onDeleteUI()
        binding.contactRecyclerView.adapter = contactAdapter
    }

    private fun onStandartUI() {
        binding.cancelButton.isVisible = false
        binding.addButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_plus,
                null
            )
        )
        binding.addButton.setOnClickListener {
            addNewElement()
        }
        mainViewModel.deleteList.clear()
        contactAdapter.isEnable = false
        mainViewModel.deleteState = false
        binding.contactRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun onDeleteUI() {
        binding.cancelButton.isVisible = true
        binding.addButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_delete,
                null
            )
        )
        binding.addButton.setOnClickListener {
            deleteElement()
        }
        contactAdapter.isEnable = true
        mainViewModel.deleteState = true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mainViewModel.recyclerState =
            binding.contactRecyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        binding.contactRecyclerView.layoutManager?.onRestoreInstanceState(mainViewModel.recyclerState)
    }

    private fun deleteElement() {
        updateNewList()
        mainViewModel.deleteState = false
        mainViewModel.newList.removeAll(mainViewModel.deleteList)
        mainViewModel.deleteList.clear()
        contactAdapter.setData(
            mainViewModel.getArrayList() as ArrayList<Contact>,
            mainViewModel.newList as ArrayList<Contact>
        )
        mainViewModel.updateIndex()
    }


    fun updateNewList() {
        mainViewModel.newList.clear()
        mainViewModel.newList.addAll(mainViewModel.getArrayList())
    }

    private fun addNewElement() {
        getNewDataFromDialog()
        mainViewModel.updateArrayList(mainViewModel.newList)
    }

    fun updateOldElement(id: Int, position: Int) {
        getNewDataFromDialog(id, position)
        mainViewModel.updateArrayList(mainViewModel.newList)
    }

    private fun getNewDataFromDialog(id: Int, position: Int) {
        val (dialog, dialogBinding) = prepareDialog()
        setDataToDialog(dialogBinding, position)
        dialog.show()
        dialogBinding.confirmButton.setOnClickListener {
            newContact = Contact(
                id,
                dialogBinding.name.text.toString(),
                dialogBinding.secondName.text.toString(),
                dialogBinding.phoneNumber.text.toString()
            )
            mainViewModel.newList[position] = newContact
            contactAdapter.setData(
                mainViewModel.getArrayList() as ArrayList<Contact>,
                mainViewModel.newList as ArrayList<Contact>
            )
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setDataToDialog(
        dialogBinding: LayoutDialogBinding,
        position: Int
    ) {
        dialogBinding.name.setText(mainViewModel.newList[position].name)
        dialogBinding.secondName.setText(mainViewModel.newList[position].secondName)
        dialogBinding.phoneNumber.setText(mainViewModel.newList[position].phoneNumber)
    }

    private fun getNewDataFromDialog() {
        val (dialog, dialogBinding) = prepareDialog()
        dialog.show()
        dialogBinding.confirmButton.setOnClickListener {
            newContact = Contact(
                mainViewModel.index,
                dialogBinding.name.text.toString(),
                dialogBinding.secondName.text.toString(),
                dialogBinding.phoneNumber.text.toString()
            )
            mainViewModel.newList.add(newContact)
            mainViewModel.index++
            contactAdapter.setData(
                mainViewModel.getArrayList() as ArrayList<Contact>,
                mainViewModel.newList as ArrayList<Contact>
            )
            mainViewModel.updateArrayList(mainViewModel.newList)
            dialog.dismiss()
        }
        val cancel = dialog.findViewById<AppCompatButton>(R.id.cancel_button)
        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun prepareDialog(): Pair<Dialog, LayoutDialogBinding> {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = LayoutDialogBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(1000, 1000)
        return Pair(dialog, dialogBinding)
    }
}