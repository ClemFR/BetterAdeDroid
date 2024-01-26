package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import xyz.alphaline.mintablenew.R

@SuppressLint("ViewConstructor")
class ZeroPointView(context: Context, height: Int, width: Int, menuColor: Int) : LinearLayout(context) {
    init {
        initView(context, height, width, menuColor)
    }

    private lateinit var zeroLayout: LinearLayout
    private lateinit var zeroItem: View


    private fun initView(context: Context, height: Int, width: Int, menuColor: Int) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.zero_point, this, true)

        zeroItem = findViewById(R.id.zeroItem)
        zeroLayout = findViewById(R.id.zeroLayout)

        zeroLayout.layoutParams = LayoutParams(width, height)
        if(menuColor != 0)
            zeroItem.setBackgroundColor(menuColor)
    }
}