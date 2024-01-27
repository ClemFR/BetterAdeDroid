package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.alphaline.mintimetablenew.databinding.XXisEndBinding

@SuppressLint("ViewConstructor")
class XxisEndView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) {
        val inflater = LayoutInflater.from(context)
        // inflater.inflate(R.layout.x_xis_end, this, true)
        val binding = XXisEndBinding.inflate(inflater, this, true)

        binding.topMenuEndItem.layoutParams = LayoutParams(width, height)
        binding.xXisEnd.text = text
        if(menuColor != 0)
            binding.xXisEnd.setBackgroundColor(menuColor)
    }
}