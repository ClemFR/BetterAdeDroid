package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import xyz.alphaline.mintablenew.R

@SuppressLint("ViewConstructor")
class YxisView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private lateinit var leftMenuItem: LinearLayout
    private lateinit var yXis: TextView

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.y_xis, this, true)

        leftMenuItem = findViewById(R.id.leftMenuItem)
        yXis = findViewById(R.id.yXis)

        leftMenuItem.layoutParams = LayoutParams(width, height)
        yXis.text = text
        if(menuColor != 0)
            yXis.setBackgroundColor(menuColor)

        if (menuTextColor != 0)
            yXis.setTextColor(menuTextColor)
        if (menuTextSize != 0f)
            yXis.textSize = menuTextSize
    }
}