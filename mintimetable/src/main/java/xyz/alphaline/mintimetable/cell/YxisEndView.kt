package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import xyz.alphaline.mintablenew.R

@SuppressLint("ViewConstructor")
class YxisEndView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private lateinit var leftMenuEndItem: LinearLayout
    private lateinit var yXisEnd: TextView

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.y_xis_end, this, true)

        leftMenuEndItem = findViewById(R.id.leftMenuEndItem)
        yXisEnd = findViewById(R.id.yXisEnd)

        leftMenuEndItem.layoutParams = LayoutParams(width, height)
        yXisEnd.text = text
        if(menuColor != 0)
            yXisEnd.setBackgroundColor(menuColor)
    }
}