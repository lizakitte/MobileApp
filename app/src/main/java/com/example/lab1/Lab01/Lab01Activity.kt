package com.example.lab1.Lab01

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.R

class Lab01Activity : AppCompatActivity() {
    lateinit var mLayout: LinearLayout
    lateinit var mTitle: TextView
    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab01)
        mLayout = findViewById(R.id.main)

        mTitle = TextView(this)
        mTitle.text = "Laboratorium 1"
        mTitle.textSize = 24f
        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 20, 20, 20)
        mTitle.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        mTitle.layoutParams = params
        mLayout.addView(mTitle)

        for (i in 1..6) {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            row.orientation = LinearLayout.HORIZONTAL

            val checkBox = CheckBox(this)
            checkBox.text = "Zadanie ${i}"
            checkBox.isEnabled = false
            mBoxes.add(checkBox)

            val testButton = Button(this)
//            testButton.setBackgroundColor(getColor(R.color.wsei_green))
            testButton.text = "Testuj"
            mButtons.add(testButton)

            row.addView(checkBox)
            row.addView(testButton)
            mLayout.addView(row)
        }

        val mProgress = ProgressBar(
            this,
            null,
            androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        )
        mLayout.addView(mProgress)

        mButtons[0].setOnClickListener({
            if (
                task11(4, 6) in 0.666665..0.666667 &&
                task11(7, -6) in -1.1666667..-1.1666665
            ) {
                mBoxes[0].isChecked = true
                mProgress.progress += 100 / 6
            }
        })

        mButtons[1].setOnClickListener({
            if (
                task12(7U, 6U) == "7 + 6 = 13" &&
                task12(12U, 15U) == "12 + 15 = 27"
            ) {
                mBoxes[1].isChecked = true
                mProgress.progress += 100 / 6
            }
        })

        mButtons[2].setOnClickListener({
            if (
                task13(0.0, 5.4f) && !task13(7.0, 5.4f) &&
                !task13(-6.0, -1.0f) && task13(6.0, 9.1f) &&
                !task13(6.0, -1.0f) && task13(1.0, 1.1f)
            ) {
                mBoxes[2].isChecked = true
                mProgress.progress += 100 / 6
            }
        })

        mButtons[3].setOnClickListener({
            if (
                task14(-2, 5) == "-2 + 5 = 3" &&
                task14(-2, -5) == "-2 - 5 = -7"
            ) {
                mBoxes[3].isChecked = true
                mProgress.progress += 100 / 6
            }
        })

        mButtons[4].setOnClickListener({
            if (
                task15("DOBRY") == 4 &&
                task15("barDzo dobry") == 5 &&
                task15("doStateczny") == 3 &&
                task15("Dopuszczający") == 2 &&
                task15("NIEDOSTATECZNY") == 1 &&
                task15("XYZ") == -1
            ){
                mBoxes[4].isChecked = true
                mProgress.progress += 100 / 6
            }
        })

        mButtons[5].setOnClickListener({
            if (task16(
                    mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                    mapOf("A" to 1U, "B" to 2U)
                ) == 2U
                &&
                task16(
                    mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                    mapOf("F" to 1U, "G" to 2U)
                ) == 0U
                &&
                task16(
                    mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                    mapOf("A" to 1U, "B" to 2U, "C" to 4U)
                ) == 7U
            ) {
                mBoxes[5].isChecked = true
                mProgress.progress += 100 / 6
            }
        })
    }

    // Dzielenie niecałkowite
    private fun task11(a: Int, b: Int): Double {
        return (a.toDouble() / b.toDouble())
    }

    private fun task12(a: UInt, b: UInt): String {
        return "$a + $b = ${a + b}"
    }

    fun task13(a: Double, b: Float): Boolean {
        return a < b && a >= 0
    }

    fun task14(a: Int, b: Int): String {
        var bStr = "+ $b"
        if (b < 0) {
            bStr = "- ${Math.abs(b)}"
        }
        return "$a $bStr = ${a + b}"
    }

    fun task15(degree: String): Int {
        return when(degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        var amounts = mutableListOf<UInt>()
        for((key, value) in asset) {
            val amount = store.get(key) ?: 0u
            amounts.add(amount / value)
        }
        return amounts.min()
    }
}