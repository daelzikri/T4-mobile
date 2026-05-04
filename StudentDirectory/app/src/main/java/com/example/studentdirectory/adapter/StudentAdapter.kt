package com.example.studentdirectory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.R
import com.example.studentdirectory.database.StudentEntity

class StudentAdapter(
    private var students: List<StudentEntity>,
    private val onItemClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvNim: TextView = view.findViewById(R.id.tvNim)
        val tvProdi: TextView = view.findViewById(R.id.tvProdi)
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text = student.name
        holder.tvNim.text = student.nim
        holder.tvProdi.text = student.prodi
        // Inisial: ambil huruf pertama setiap kata, maks 2 huruf
        holder.tvInitial.text = student.name
            .split(" ")
            .take(2)
            .joinToString("") { it.first().uppercase() }

        holder.itemView.setOnClickListener { onItemClick(student) }
        holder.btnEdit.setOnClickListener { onEditClick(student) }
        holder.btnDelete.setOnClickListener { onDeleteClick(student) }
    }

    override fun getItemCount() = students.size

    fun updateData(newList: List<StudentEntity>) {
        students = newList
        notifyDataSetChanged()
    }
}
