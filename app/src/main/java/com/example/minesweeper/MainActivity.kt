package com.example.minesweeper

import android.graphics.Color
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

        supportActionBar?.hide()

        var mineSweeperView = findViewById<MineSweeperView>(R.id.mineSweeperView)
        var reset_btn = findViewById<Button>(R.id.reset_btn)
        var mode_switch_btn = findViewById<Button>(R.id.mode_switch_btn)
        var currentMode = findViewById<TextView>(R.id.currentGameMode)

        var gameMode = GameMode.MARKING_MODE

        //game is restarted when Reset button is clicked
        reset_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                mineSweeperView.gameStart()
            }
        })

        mode_switch_btn.apply {
            text = gameMode.label
        }

        //currentMode.setText("Current mode: " + mineSweeperView.gameMode.toString())
        currentMode.setTextColor(Color.DKGRAY)

        mode_switch_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                gameMode = gameMode.next()
                mode_switch_btn.apply {
                    text = gameMode.label
                }
                mineSweeperView.gameMode = gameMode.next();
                currentMode.setText("Current mode: " + mineSweeperView.gameMode.toString())
            }
        })

    }
}