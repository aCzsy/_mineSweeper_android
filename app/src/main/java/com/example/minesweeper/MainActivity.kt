package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mineSweeperView = findViewById<MineSweeperView>(R.id.mineSweeperView)
        var reset_btn = findViewById<Button>(R.id.reset_btn)

        supportActionBar?.hide()

        //game is restarted when Reset button is clicked
        reset_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                mineSweeperView.gameStart()
            }
        })
    }
}