package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.databinding.RowLayoutBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<ToDoData>()

//    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    class MyViewHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(toDoData: ToDoData){
            binding.toDoData = toDoData
            binding.executePendingBindings()
        }

    companion object{
        fun from(parent: ViewGroup):MyViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RowLayoutBinding.inflate(layoutInflater,parent,false)
            return MyViewHolder(binding)
        }
    }
        // 可选：提供对外访问控件的方法（简化 onBindViewHolder 代码）
        fun bindTitle(text: String) {
            binding.titleTxt.text = text // titleTxt 是 View Binding 生成的属性（id 为 title_txt 转小驼峰）
        }
        // 新增：绑定描述（对应 row_layout 中的 description_txt 控件）
        fun bindDescription(description: String) {
            binding.descriptionTxt.text = description // 假设布局中有 id 为 description_txt 的 TextView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent) // 把 Binding 传入 ViewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    fun setData(toDoData: List<ToDoData>){
        val toDoDiffUtil = ToDoDiffUtil(dataList,toDoData)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)

        this.dataList = toDoData
        toDoDiffResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

}