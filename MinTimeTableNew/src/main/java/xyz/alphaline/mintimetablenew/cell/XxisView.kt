package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.alphaline.mintimetablenew.databinding.XXisBinding

@SuppressLint("ViewConstructor")
class XxisView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        //inflater.inflate(R.layout.x_xis, this, true)
        var binding = XXisBinding.inflate(inflater, this, true)

        binding.topMenuItem.layoutParams = LayoutParams(width, height)
        binding.xXis.text = text
        if(menuColor != 0)
            binding.xXis.setBackgroundColor(menuColor)
    }
}