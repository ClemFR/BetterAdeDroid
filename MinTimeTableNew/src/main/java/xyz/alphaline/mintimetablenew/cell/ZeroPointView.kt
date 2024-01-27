package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.alphaline.mintimetablenew.databinding.ZeroPointBinding

@SuppressLint("ViewConstructor")
class ZeroPointView(context: Context, height: Int, width: Int, menuColor: Int) : LinearLayout(context) {
    init {
        initView(context, height, width, menuColor)
    }

    private fun initView(context: Context, height: Int, width: Int, menuColor: Int) {
        val inflater = LayoutInflater.from(context)
        // inflater.inflate(R.layout.zero_point, this, true)
        val binding = ZeroPointBinding.inflate(inflater, this, true)

        binding.zeroLayout.layoutParams = LayoutParams(width, height)
        if(menuColor != 0)
            binding.zeroItem.setBackgroundColor(menuColor)
    }
}