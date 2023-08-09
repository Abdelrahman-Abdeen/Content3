package com.example.content3

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun onClickAddName(view: View?){
        val values = ContentValues()
        values.put(StudentsProvider.NAME,(findViewById<View>(R.id.editText2)as EditText).text.toString())
        values.put(StudentsProvider.GRADE,(findViewById<View>(R.id.editText3)as EditText).text.toString())
        val uri = contentResolver.insert(StudentsProvider.CONTENT_URI,values)
        Toast.makeText(baseContext,uri.toString(),Toast.LENGTH_LONG).show()
    }
    fun onClickRetrieveStudents(view: View?){
        val URL = "content://com.example.content3.StudentsProvider"
        val students = Uri.parse(URL)
        var c = contentResolver.query(students,null,null,null, null)
        if(c!=null){
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndex(StudentsProvider._ID)
                val nameIndex = c.getColumnIndex(StudentsProvider.NAME)
                val gradeIndex = c.getColumnIndex(StudentsProvider.GRADE)

                do {
                    val id = c.getInt(idIndex)
                    val name = c.getString(nameIndex)
                    val grade = c.getString(gradeIndex)

                    Toast.makeText(
                        this, "$id, $name, $grade", Toast.LENGTH_SHORT
                    ).show()
                } while (c.moveToNext())
            }
            c.close() // Close the cursor when you're done with it
        }
    }
}