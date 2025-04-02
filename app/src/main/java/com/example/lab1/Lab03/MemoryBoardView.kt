package com.example.lab1.Lab03

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.GameState
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import com.example.lab1.R
import java.util.Random
import java.util.Stack

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_balance_24,
        R.drawable.baseline_anchor_24,
        R.drawable.baseline_ac_unit_24,
        R.drawable.baseline_airplanemode_active_24,
        R.drawable.baseline_attractions_24,
        R.drawable.baseline_beach_access_24,
        R.drawable.baseline_brightness_7_24,
        R.drawable.baseline_cookie_24,
        R.drawable.baseline_auto_fix_high_24,
        R.drawable.baseline_bakery_dining_24,
        R.drawable.baseline_battery_charging_full_24,
        R.drawable.baseline_auto_stories_24,
        R.drawable.baseline_bluetooth_connected_24,
        R.drawable.baseline_celebration_24,
        R.drawable.baseline_color_lens_24,
        R.drawable.baseline_construction_24,
        R.drawable.baseline_compost_24,
        R.drawable.baseline_cottage_24
    )

    private var state: MutableList<MutableList<Int>> = MutableList(rows) {MutableList(cols) { -1 } }
    private val deckResource: Int = R.drawable.playing_card_back
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { (e) -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
        }

        for(row in 0 ..< rows) {
            for(col in 0 ..< cols) {
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    it.setImageResource(deckResource)
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    addTile(it, shuffledIcons.removeLast())
                    gridLayout.addView(it)
                }
            }
        }
    }

    fun getState() : IntArray {
        return state.flatten().toIntArray()
    }

    fun setState(state: IntArray): Unit {
        var i = 0;
        for (r in 0..<rows) {
            for (c in 0..<cols) {
                this.state[r][c] = state[i]
                val tag = "${r}x${c}"
                if (state[i] == 1) {
                    tiles[tag]!!.button.alpha = 0.0f
                }
                else if (state[i] == 2) {
                    tiles[tag]!!.revealed = true
                    onClickTile(tiles[tag]!!.button)
                }
                ++i
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag]
        if (matchedPair.lastOrNull() == tile) return

        matchedPair.push(tile)
        val matchResult = logic.process {
            tile?.tileResource?:-1
        }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))
        when (matchResult) {
            GameStates.Matching -> {
                for (v in matchedPair) {
                    val tag = v.button.tag as String
                    val row = tag.substring(0, 1).toInt()
                    val col = tag.substring(2, 3).toInt()
                    state[row][col] = 2
                }
            }
            GameStates.NoMatch -> {
                for (v in matchedPair) {
                    val tag = v.button.tag as String
                    val row = tag.substring(0, 1).toInt()
                    val col = tag.substring(2, 3).toInt()
                    state[row][col] = -1
                }
            }
            GameStates.Match -> {
                for (v in matchedPair) {
                    val tag = v.button.tag as String
                    val row = tag.substring(0, 1).toInt()
                    val col = tag.substring(2, 3).toInt()
                    state[row][col] = 1
                }
            }
            else -> {

            }
        }

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = { e ->
            if (e.state == GameStates.Match || e.state == GameStates.Finished) {
                for (tile in e.tiles) {
                    animatePairedButton(tile.button, { listener(e) })
                }
            } else if(e.state == GameStates.NoMatch) {
                for (tile in e.tiles) {
                    animateNotPairedButton(tile.button, { listener(e) })
                }
            }
            else {
                listener(e)
            }
        }
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable ) {
        val set = AnimatorSet()
        val random = Random()
        button.pivotX = random.nextFloat() * 200f
        button.pivotY = random.nextFloat() * 200f

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scallingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scallingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        set.startDelay = 500
        set.duration = 1000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scallingX, scallingY, fade)
        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
                action.run();
            }

            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }

    private fun animateNotPairedButton(button: ImageButton, action: Runnable ) {
        val set = AnimatorSet()

        val moveLeft = ObjectAnimator.ofFloat(button, "x", button.x, button.x - 50)
        val moveRight = ObjectAnimator.ofFloat(button, "x", button.x - 50, button.x + 50)
        val moveBack = ObjectAnimator.ofFloat(button, "x", button.x + 50, button.x)
        set.startDelay = 0
        set.duration = 200
        set.interpolator = DecelerateInterpolator()
        set.playSequentially(moveLeft, moveRight, moveBack)
        set.addListener(object: Animator.AnimatorListener {

            override fun onAnimationStart(animator: Animator) {
                action.run();
            }

            override fun onAnimationEnd(animator: Animator) {

            }

            override fun onAnimationCancel(animator: Animator) {
            }

            override fun onAnimationRepeat(animator: Animator) {
            }
        })
        set.start()
    }
}