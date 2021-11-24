package com.example.minesweeper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hiding action at the top of the screen
        supportActionBar?.hide()

        //getting layout elements
        var mineSweeperView = findViewById<MineSweeperView>(R.id.mineSweeperView)
        var reset_btn = findViewById<Button>(R.id.reset_btn)
        var mode_switch_btn = findViewById<Button>(R.id.mode_switch_btn)
        var currentMode = findViewById<TextView>(R.id.currentGameMode)
        var _main_layout = findViewById<LinearLayout>(R.id.main_layout)

        //game is restarted when Reset button is clicked
        reset_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                mineSweeperView.gameStart()
            }
        })

        //setting text of a switch button and current mode textview based on game mode
        mineSweeperView.setGameModeLabelsText()

        //textview color
        currentMode.setTextColor(Color.DKGRAY)

        //when button is clicked, game mode is switched
        mode_switch_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                //restricting game mode change when game is over
                if(!mineSweeperView.gameOver){
                    //changing mode
                    mineSweeperView.gameMode = mineSweeperView.gameMode.next()
                    mineSweeperView.gameModeBtnText = mineSweeperView.gameModeBtnText.next()
                    //updating labels
                    mineSweeperView.setGameModeLabelsText()
                }
                //if user clicks on a switch mode btn when game is over, snackbar is shown with an option to reset
                //or user can simply press a reset button
                else {
                    val snackbar = Snackbar.make(_main_layout, "Game Over, Reset the Game", Snackbar.LENGTH_LONG)
                    snackbar.setAction("RESET", View.OnClickListener {
                        mineSweeperView.gameStart()
                        snackbar.dismiss()
                    })
                    snackbar.show()
                }
            }
        })

    }
}