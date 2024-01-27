package xyz.alphaline.mintimetablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.alphaline.mintimetablenew.tableinterface.OnTimeCellClickListener
import xyz.alphaline.mintimetablenew.databinding.ItemTableCellBinding

@SuppressLint("ViewConstructor")
class TableCellView(context: Context,
                    height: Int,
                    width: Int,
                    marginLeft: Int,
                    marginTop: Int,
                    cellColor: Int,
                    timeCellClickListener: OnTimeCellClickListener?,
                    scheduleDay: Int,
                    time: Int
) : ConstraintLayout(context) {
    init {
        initView(context, height, width, marginLeft, marginTop, cellColor, timeCellClickListener, scheduleDay, time)
    }

    private fun initView(context: Context,
                         height: Int,
                         width: Int,
                         marginLeft: Int,
                         marginTop: Int,
                         cellColor: Int,
                         timeCellClickListener: OnTimeCellClickListener?,
                         scheduleDay: Int,
                         time: Int
    ){
        val inflater = LayoutInflater.from(context)
        // inflater.inflate(R.layout.item_table_cell, this, true)
        val binding = ItemTableCellBinding.inflate(inflater, this, true)

        binding.cell.setOnClickListener {
            timeCellClickListener?.timeCellClicked(scheduleDay, time)
        }

        val layoutSetting = LayoutParams(width, height)
        layoutSetting.leftMargin = marginLeft
        layoutSetting.topMargin = marginTop

        layoutSetting.topToTop = LayoutParams.PARENT_ID
        layoutSetting.leftToLeft = LayoutParams.PARENT_ID
        layoutSetting.rightToRight = LayoutParams.PARENT_ID

        binding.cell.layoutParams = layoutSetting
        if(cellColor != 0)
            binding.cellItem.setBackgroundColor(cellColor)
    }
}
