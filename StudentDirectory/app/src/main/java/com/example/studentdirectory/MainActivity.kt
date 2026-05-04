package com.example.studentdirectory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.adapter.StudentAdapter
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.StudentDao
import com.example.studentdirectory.database.StudentEntity
import com.example.studentdirectory.utils.PrefManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dao: StudentDao
    private lateinit var prefManager: PrefManager
    private lateinit var adapter: StudentAdapter
    private lateinit var tvEmpty: TextView
    private var studentList: List<StudentEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        dao = AppDatabase.getInstance(this).studentDao()
        prefManager = PrefManager(this)

        tvEmpty = findViewById(R.id.tvEmpty)

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StudentAdapter(
            students = emptyList(),
            onItemClick = { student ->
                // Buka halaman detail
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("STUDENT_ID", student.id)
                startActivity(intent)
            },
            onDeleteClick = { student -> confirmDelete(student) },
            onEditClick = { student ->
                // Buka form edit
                val intent = Intent(this, FormActivity::class.java)
                intent.putExtra("STUDENT_ID", student.id)
                startActivity(intent)
            }
        )
        recyclerView.adapter = adapter

        // LANGKAH 9: Implementasi Swipe to Delete
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val student = studentList[position]
                confirmDelete(student)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)

        // FAB tambah
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, FormActivity::class.java))
        }

        // Search
        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString()?.trim() ?: ""
                if (keyword.isEmpty()) loadAllStudents()
                else searchStudents(keyword)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Insert sample data jika DB kosong, lalu load
        lifecycleScope.launch {
            if (dao.getStudentCount() == 0) {
                dao.insertAll(getSampleData())
            }
            loadAllStudents()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list setiap kali kembali ke halaman ini
        loadAllStudents()
    }

    private fun loadAllStudents() {
        lifecycleScope.launch {
            studentList = dao.getAllStudents()
            adapter.updateData(studentList)
            tvEmpty.visibility = if (studentList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun searchStudents(keyword: String) {
        lifecycleScope.launch {
            studentList = dao.searchStudents(keyword)
            adapter.updateData(studentList)
            tvEmpty.visibility = if (studentList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun confirmDelete(student: StudentEntity) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    dao.deleteById(student.id)
                    loadAllStudents()
                }
            }
            .setNegativeButton("Batal") { _, _ ->
                adapter.notifyDataSetChanged() // Kembalikan posisi item jika batal
            }
            .setCancelable(false)
            .show()
    }

    // Menu Logout di ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Logout")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    prefManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    // Data contoh awal (Email dan Semester ditambahkan sesuai spesifikasi baru)
    private fun getSampleData() = listOf(
        StudentEntity(name = "Ahmad Fauzi", nim = "2024001", prodi = "Teknik Informatika", email = "ahmad@mail.com", semester = "1"),
        StudentEntity(name = "Budi Santoso", nim = "2024002", prodi = "Sistem Informasi", email = "budi@mail.com", semester = "3"),
        StudentEntity(name = "Clara Wijaya", nim = "2024003", prodi = "Teknik Informatika", email = "clara@mail.com", semester = "5"),
        StudentEntity(name = "Dewi Rahayu", nim = "2024004", prodi = "Manajemen Informatika", email = "dewi@mail.com", semester = "1"),
        StudentEntity(name = "Eko Prasetyo", nim = "2024005", prodi = "Sistem Informasi", email = "eko@mail.com", semester = "7")
    )
}
