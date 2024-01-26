package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import xyz.alphaline.mintablenew.R

@SuppressLint("ViewConstructor")
class XxisView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    protected lateinit var topMenuItem: LinearLayout
    protected lateinit var xXis: TextView

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.x_xis, this, true)

        topMenuItem = findViewById(R.id.topMenuItem)
        xXis = findViewById(R.id.xXis)

        topMenuItem.layoutParams = LayoutParams(width, height)
        xXis.text = text
        if(menuColor != 0)
            xXis.setBackgroundColor(menuColor)
    }
}