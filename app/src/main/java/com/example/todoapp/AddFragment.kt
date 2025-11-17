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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentAddBinding


class AddFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()
    // 声明视图绑定变量
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!! // 非空断言，确保在视图销毁后不再使用

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_add, container, false)
//
//        return view
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root // 返回布局根视图
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        // 正确写法
        val mTitle = binding.titleEt.text.toString().trim() // title_et 对应 binding.titleEt
        val mPriority = binding.prioritiesSpinner.selectedItem?.toString() ?: "Low Priority" // priorities_spinner 对应 binding.prioritiesSpinner
        val mDescription = binding.descriptionEt.text.toString().trim() // description_et 对应 binding.descriptionEt

        val validation = mSharedViewModel.verifyDataFromUser(mTitle,mDescription)
        if(validation){
            //inset data
            val newData = ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_SHORT).show()

            //Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Please fill out all fields.",Toast.LENGTH_SHORT).show()
        }
    }





}