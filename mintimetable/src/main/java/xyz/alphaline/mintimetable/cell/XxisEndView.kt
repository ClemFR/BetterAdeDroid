package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import xyz.alphaline.mintablenew.R

@SuppressLint("ViewConstructor")
class XxisEndView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    protected lateinit var topMenuEndItem: LinearLayout
    protected lateinit var xXisEnd: TextView

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.x_xis_end, this, true)

        topMenuEndItem.layoutParams = LayoutParams(width, height)
        xXisEnd.text = text
        if(menuColor != 0)
            xXisEnd.setBackgroundColor(menuColor)
    }
}