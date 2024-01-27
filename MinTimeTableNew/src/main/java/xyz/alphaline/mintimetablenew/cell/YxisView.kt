package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.alphaline.mintimetablenew.databinding.YXisBinding

@SuppressLint("ViewConstructor")
class YxisView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float) : ConstraintLayout(context) {
    init {
        initView(context, height, width, text, menuColor, menuTextColor, menuTextSize)
    }

    private fun initView(context: Context, height: Int, width: Int, text: String, menuColor: Int, menuTextColor: Int, menuTextSize: Float){
        val inflater = LayoutInflater.from(context)
        // inflater.inflate(R.layout.y_xis, this, true)
        val binding = YXisBinding.inflate(inflater, this, true)

        binding.leftMenuItem.layoutParams = LayoutParams(width, height)
        binding.yXis.text = text
        if(menuColor != 0)
            binding.yXis.setBackgroundColor(menuColor)

        if (menuTextColor != 0)
            binding.yXis.setTextColor(menuTextColor)
        if (menuTextSize != 0f)
            binding.yXis.textSize = menuTextSize
    }
}