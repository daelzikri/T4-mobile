package com.example.studentdirectory

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.StudentEntity
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etNim: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSemester: EditText
    private lateinit var spinnerProdi: Spinner
    private var editStudentId: Int = -1 // -1 = mode tambah, > 0 = mode edit

    private val prodiList = listOf(
        "Teknik Informatika",
        "Sistem Informasi",
        "Manajemen Informatika",
        "Teknik Komputer",
        "Rekayasa Perangkat Lunak"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etNim = findViewById(R.id.etNim)
        etEmail = findViewById(R.id.etEmail)
        etSemester = findViewById(R.id.etSemester)
        spinnerProdi = findViewById(R.id.spinnerProdi)

        // Setup Spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prodiList)
        spinnerProdi.adapter = spinnerAdapter

        // Cek apakah mode edit (ada STUDENT_ID di intent)
        editStudentId = intent.getIntExtra("STUDENT_ID", -1)
        if (editStudentId != -1) {
            supportActionBar?.title = "Edit Mahasiswa"
            loadStudentData(editStudentId)
        } else {
            supportActionBar?.title = "Tambah Mahasiswa"
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveStudent()
        }
    }

    private fun loadStudentData(id: Int) {
        val dao = AppDatabase.getInstance(this).studentDao()
        lifecycleScope.launch {
            val student = dao.getStudentById(id)
            student?.let {
                etName.setText(it.name)
                etNim.setText(it.nim)
                etEmail.setText(it.email)
                etSemester.setText(it.semester)
                val prodiIndex = prodiList.indexOf(it.prodi)
                if (prodiIndex >= 0) spinnerProdi.setSelection(prodiIndex)
            }
        }
    }

    private fun saveStudent() {
        val name = etName.text.toString().trim()
        val nim = etNim.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val semester = etSemester.text.toString().trim()
        val prodi = spinnerProdi.selectedItem.toString()

        // Validasi
        if (name.isEmpty()) { etName.error = "Nama tidak boleh kosong"; return }
        if (nim.isEmpty()) { etNim.error = "NIM tidak boleh kosong"; return }
        if (email.isEmpty()) { etEmail.error = "Email tidak boleh kosong"; return }
        if (semester.isEmpty()) { etSemester.error = "Semester tidak boleh kosong"; return }

        val dao = AppDatabase.getInstance(this).studentDao()
        lifecycleScope.launch {
            if (editStudentId == -1) {
                // Mode tambah
                dao.insert(StudentEntity(name = name, nim = nim, prodi = prodi, email = email, semester = semester))
                Toast.makeText(this@FormActivity, "Mahasiswa berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            } else {
                // Mode edit
                dao.update(StudentEntity(id = editStudentId, name = name, nim = nim, prodi = prodi, email = email, semester = semester))
                Toast.makeText(this@FormActivity, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }
            finish() // Kembali ke MainActivity
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
