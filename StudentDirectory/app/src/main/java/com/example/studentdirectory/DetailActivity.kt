package com.example.studentdirectory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.StudentEntity
import com.example.studentdirectory.utils.FileHelper
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var etNote: EditText
    private lateinit var tvNoteStatus: TextView
    private var currentStudent: StudentEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Detail Mahasiswa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etNote = findViewById(R.id.etNote)
        tvNoteStatus = findViewById(R.id.tvNoteStatus)

        val studentId = intent.getIntExtra("STUDENT_ID", -1)
        if (studentId == -1) { finish(); return }

        val dao = AppDatabase.getInstance(this).studentDao()
        lifecycleScope.launch {
            val student = dao.getStudentById(studentId)
            student?.let {
                currentStudent = it
                displayStudentInfo(it)
                autoLoadNote(it.nim)
            } ?: finish()
        }

        findViewById<Button>(R.id.btnSaveNote).setOnClickListener { saveNote() }
        findViewById<Button>(R.id.btnLoadNote).setOnClickListener { loadNote() }
    }

    private fun displayStudentInfo(student: StudentEntity) {
        val initial = student.name.split(" ").take(2).joinToString("") { it.first().uppercase() }
        findViewById<TextView>(R.id.tvInitial).text = initial
        findViewById<TextView>(R.id.tvName).text = student.name
        findViewById<TextView>(R.id.tvNim).text = "NIM: ${student.nim}"
        findViewById<TextView>(R.id.tvProdi).text = student.prodi
    }

    // Otomatis muat catatan jika sudah ada
    private fun autoLoadNote(nim: String) {
        if (FileHelper.isNoteExists(this, nim)) {
            val note = FileHelper.loadNote(this, nim)
            etNote.setText(note)
            val size = FileHelper.getNoteSize(this, nim)
            tvNoteStatus.text = "Catatan tersimpan ($size bytes)"
        } else {
            tvNoteStatus.text = "Belum ada catatan"
        }
    }

    private fun saveNote() {
        val student = currentStudent ?: return
        val content = etNote.text.toString()
        val success = FileHelper.saveNote(this, student.nim, content)
        if (success) {
            val size = FileHelper.getNoteSize(this, student.nim)
            tvNoteStatus.text = "Tersimpan ($size bytes)"
            Toast.makeText(this, "Catatan disimpan!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNote() {
        val student = currentStudent ?: return
        if (FileHelper.isNoteExists(this, student.nim)) {
            val note = FileHelper.loadNote(this, student.nim)
            etNote.setText(note)
            val size = FileHelper.getNoteSize(this, student.nim)
            tvNoteStatus.text = "Dimuat ($size bytes)"
            Toast.makeText(this, "Catatan dimuat!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Belum ada catatan tersimpan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
