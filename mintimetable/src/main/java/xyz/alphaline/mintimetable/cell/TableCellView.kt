package xyz.alphaline.mintablenew.cell

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import xyz.alphaline.mintablenew.R
import xyz.alphaline.mintablenew.tableinterface.OnTimeCellClickListener

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
) : LinearLayout(context) {
    init {
        initView(context, height, width, marginLeft, marginTop, cellColor, timeCellClickListener, scheduleDay, time)
    }

    protected lateinit var cell: LinearLayout
    protected lateinit var cellItem: View

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
        inflater.inflate(R.layout.item_table_cell, this, true)

        cell = findViewById(R.id.cell)
        cellItem = findViewById(R.id.cellItem)

        cell.setOnClickListener {
            timeCellClickListener?.timeCellClicked(scheduleDay, time)
        }

        val layoutSetting = LayoutParams(width, height)
        layoutSetting.leftMargin = marginLeft
        layoutSetting.topMargin = marginTop
        cell.layoutParams = layoutSetting
        if(cellColor != 0)
            cellItem.setBackgroundColor(cellColor)
    }
}
