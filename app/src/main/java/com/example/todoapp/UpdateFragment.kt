package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private val mSharedViewModel:SharedViewModel by viewModels()

    private val mToDoViewModel:ToDoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!! // 非空断言，确保在视图销毁后不再使用


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args
        setHasOptionsMenu(true)

//        binding.currentTitleEt.setText(args.currentItem.title)
//        binding.currentDescriptionEt.setText(args.currentItem.description)
//        binding.currentPrioritiesSpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root
    }

    // 关键补充：视图销毁时解绑 Binding，避免内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save->updateItem()
            R.id.menu_delete->confirmItemRemoval()

        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateItem() {
        val title = binding.currentTitleEt.text.toString()
        val description = binding.currentDescriptionEt.text.toString()
        val getPriority = binding.currentPrioritiesSpinner.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title,description)
        if(validation){
            //update current Item
            val updateItem= ToDoData(
                args.currentItem.id,
                title,
                mSharedViewModel.parsePriority(getPriority),
                description,
            )
            mToDoViewModel.updateData(updateItem)
            Toast.makeText(requireContext(),"Successfully updated",Toast.LENGTH_SHORT).show()
            //navigate back
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{

            Toast.makeText(requireContext(),"Please fill out all fields.",Toast.LENGTH_SHORT).show()

        }
    }

    //show AlertDialog to confirm removal
    private fun confirmItemRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewModel.deleteItem(args.currentItem)
            Toast.makeText(
                requireContext(),
                "Successfully Removed:${args.currentItem.title}",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete ${args.currentItem.title}?")
        builder.setMessage("Are you sure you want to remove ${args.currentItem.title}?")
        builder.create().show()
    }


}