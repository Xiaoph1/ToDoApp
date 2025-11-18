package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mToDoViewModel:ToDoViewModel by viewModels()

    private val mSharedViewModel:SharedViewModel by viewModels()

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!! // 非空断言，确保在视图销毁后不再使用

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 只使用ViewBinding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

//        setupRecyclerview()

        setHasOptionsMenu(true)

        // 通过binding初始化RecyclerView
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply{
            addDuration=300
        }
        //Swipe to Delete
        swipeToDelete(binding.recyclerView)

        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer{ data->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })



        //set menu
        setHasOptionsMenu(true)


        return  return binding.root
    }

    // 关键补充：视图销毁时解绑 Binding，避免内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                //Delete
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
//                Toast.makeText(requireContext(),"Successfully Removed : ${deletedItem.title}",Toast.LENGTH_SHORT).show()

                //Restore
                restoreDeletedData(viewHolder.itemView,deletedItem,viewHolder.adapterPosition)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun restoreDeletedData(view: View,deletedItem:ToDoData,position:Int){
        val snackbar = Snackbar.make(
            view,"Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Undo"){
            mToDoViewModel.insertData(deletedItem)
//            adapter.notifyItemChanged(position)
        }
        snackbar.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId ){
            R.id.menu_delete_all-> confirmRemoval()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(this,Observer{ adapter.setData(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(this,Observer{ adapter.setData(it) })

            }
        return super.onOptionsItemSelected(item)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery:String = query
        searchQuery = "%$searchQuery%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this,Observer{list->
            list?.let {
                adapter.setData(it)
            }
        })
    }


    //show AlertDialog to confirm removal of all items from database table
    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed Everything!",
                Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }



}