package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.alphaline.mintimetablenew.databinding.YXisEndBinding

@SuppressLint("ViewConstructor")
class YxisEndView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : LinearLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        // inflater.inflate(R.layout.y_xis_end, this, true)

        val binding = YXisEndBinding.inflate(inflater, this, true)

        binding.leftMenuEndItem.layoutParams = LayoutParams(width, height)
        binding.yXisEnd.text = text
        if(menuColor != 0)
            binding.yXisEnd.setBackgroundColor(menuColor)
    }
}