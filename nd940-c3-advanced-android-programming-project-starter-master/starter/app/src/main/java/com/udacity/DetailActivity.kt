package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Constants.FILE_NAME
import com.udacity.Constants.STATUS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        fileNameTv.text = intent.extras?.get(FILE_NAME).toString()
        statusTv.text = intent.extras?.get(STATUS).toString()

        setRadioButtonSelector()
    }

    private fun setRadioButtonSelector() {
        ok_detail_button.setOnClickListener {
            finish()
        }
    }
}
