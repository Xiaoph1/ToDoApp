package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.databinding.RowLayoutBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<ToDoData>()

//    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    class MyViewHolder(val binding: RowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
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
        val binding = RowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding) // 把 Binding 传入 ViewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = dataList[position] // 获取当前位置数据
        val binding = holder.binding // 简化 Binding 引用（也可以直接用 holder.binding.xxx）

        // 1. 设置标题和描述（注意：你之前调用了两次 bindTitle，应该区分 bindTitle 和 bindDescription）
        holder.bindTitle(currentData.title) // 绑定标题
        holder.bindDescription(currentData.description) // 绑定描述（需要在 ViewHolder 中新增这个方法）

        holder.binding.rowBackground.setOnClickListener{ view ->
            // 关键修正：用 Navigation.findNavController(view) 获取导航控制器
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            Navigation.findNavController(view)
                .navigate(action)
        }

        // 2. 根据优先级设置 priority_indicator 的背景色
        val priority = currentData.priority
        val indicatorColor = when (priority) {
            Priority.HIGH -> ContextCompat.getColor(binding.root.context, R.color.red) // 高优先级：红色
            Priority.MEDIUM -> ContextCompat.getColor(binding.root.context, R.color.yellow) // 中优先级：黄色
            Priority.LOW -> ContextCompat.getColor(binding.root.context, R.color.green) // 低优先级：绿色
//            else -> ContextCompat.getColor(binding.root.context, R.color.gray) // 默认：灰色（避免分支缺失）
        }
        // 通过 View Binding 访问 priority_indicator（CardView），设置背景色
        binding.priorityIndicator.setCardBackgroundColor(indicatorColor)

    }

    fun setData(toDoData: List<ToDoData>){
        this.dataList = toDoData
        notifyDataSetChanged()
    }

}