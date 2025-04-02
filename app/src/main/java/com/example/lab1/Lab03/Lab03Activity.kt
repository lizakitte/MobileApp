package com.example.lab1.Lab03

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab1.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    lateinit var mBoard: GridLayout
    var columns: Int = 0
    var rows: Int = 0
    var isSound: Boolean = true;
    lateinit var completionPlayer: MediaPlayer
    lateinit var negativePLayer: MediaPlayer
    lateinit var mBoardModel: MemoryBoardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        columns = intent.getIntExtra("columns", 2)
        rows = intent.getIntExtra("rows", 3)

        mBoard = findViewById(R.id.game_grid)
        mBoard.columnCount = columns
        mBoard.rowCount = rows

        mBoardModel = MemoryBoardView(mBoard, columns, rows)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mBoardModel.setOnGameChangeListener { e ->
            run {
                when (e.state) {
                    GameStates.Matching -> {
                        for (tile in e.tiles) {
                            tile.revealed = true
                        }
                    }
                    GameStates.Match -> {
                        if(isSound) {
                            completionPlayer.start();
                        }
                        for (tile in e.tiles) {
                            tile.revealed = true
                        }
                    }
                    GameStates.NoMatch -> {
                        if(isSound) {
                            negativePLayer.start();
                        }
                        for (tile in e.tiles) {
                            tile.revealed = true
                            Timer().schedule(1000) {
                                runOnUiThread() {
                                    tile.revealed = false;
                                }
                            }
                        }
                    }
                    GameStates.Finished -> {
                        if(isSound) {
                            completionPlayer.start();
                        }
                        for (tile in e.tiles) {
                            tile.revealed = true
                        }
                        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        if (savedInstanceState != null) {
            val state = savedInstanceState.getIntArray("state")
            mBoardModel.setState(state!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("state", mBoardModel.getState())
    }

    override protected fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePLayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }


    override protected fun onPause() {
        super.onPause();
        completionPlayer.release()
        negativePLayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean  {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.getItemId()){
            R.id.board_activity_sound -> {
                if (isSound) {
                    Toast.makeText(this, "Sound turned off", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.baseline_do_not_disturb_on_24)
                    isSound = false;
                } else {
                    Toast.makeText(this, "Sound turned on", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_do_not_disturb_off_24)
                    isSound = true
                }
            }
        }
        return false
    }
}