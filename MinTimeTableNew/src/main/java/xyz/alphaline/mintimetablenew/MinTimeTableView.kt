package xyz.alphaline.mintimetablenew

import android.content.Context
import android.util.AttributeSet
import xyz.alphaline.mintimetablenew.cell.XxisEndView
import xyz.alphaline.mintimetablenew.cell.XxisView
import xyz.alphaline.mintimetablenew.cell.ZeroPointView
import xyz.alphaline.mintimetablenew.model.ScheduleEntity
import xyz.alphaline.mintimetablenew.schedule.ScheduleView
import xyz.alphaline.mintimetablenew.tableinterface.OnScheduleClickListener
import xyz.alphaline.mintimetablenew.tableinterface.OnScheduleLongClickListener
import xyz.alphaline.mintimetablenew.tableinterface.OnTimeCellClickListener
import xyz.alphaline.mintimetablenew.utils.dpToPx
import xyz.alphaline.mintimetablenew.utils.getWindowWidth
import kotlin.math.roundToInt

class MinTimeTableView : BaseTimeTable {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //table setting function

    fun baseSetting(topMenuHeight: Int, leftMenuWidth: Int, cellHeight: Int) {
        super.topMenuHeight = topMenuHeight
        super.leftMenuWidth = leftMenuWidth
        super.cellHeight = cellHeight
        super.isRatio = false
    }

    fun ratioCellSetting(topMenuHeight: Int, leftMenuWidth: Int, cellRatio: Float) {
        super.topMenuHeight = topMenuHeight
        super.leftMenuWidth = leftMenuWidth
        super.cellRatio = cellRatio
        super.isRatio = true
    }

    fun initTable(dayList: Array<String>) {
        super.tableStartTime = 8
        super.tableEndTime = 19
        super.dayList = dayList

        super.topMenuHeightPx = dpToPx(
            super.tableContext,
            super.topMenuHeight.toFloat()
        )
        super.leftMenuWidthPx = dpToPx(
            super.tableContext,
            super.leftMenuWidth.toFloat()
        )
        super.widthPaddingPx = dpToPx(
            super.tableContext,
            super.widthPadding.toFloat()
        )

        super.averageWidth = if (super.isFullScreen)
            (getWindowWidth(super.tableContext) - (super.widthPaddingPx.roundToInt() * 2) - super.leftMenuWidthPx.roundToInt()) / (super.dayList).size
        else
            (binding.timetable.width - super.leftMenuWidthPx.roundToInt()) / (super.dayList).size

        if (super.isFullScreen) {
            binding.timetable.setPadding(super.widthPaddingPx.roundToInt(), 0, super.widthPaddingPx.roundToInt(), 0)
        }

        super.cellHeightPx = if (super.isRatio) super.averageWidth * super.cellRatio
        else dpToPx(
            super.tableContext,
            super.cellHeight.toFloat()
        )

        binding.leftMenu.layoutParams = LayoutParams(super.leftMenuWidthPx.roundToInt(), LayoutParams.WRAP_CONTENT)
        binding.topMenu.layoutParams =  LayoutParams(LayoutParams.WRAP_CONTENT, super.topMenuHeightPx.roundToInt())
        binding.mainTable.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (super.border) {
            binding.borderBox.setBackgroundColor(super.lineColor)
            binding.borderBox.setPadding(
                dpToPx(
                    super.tableContext,
                    1f
                ).roundToInt(),
                dpToPx(
                    super.tableContext,
                    1f
                ).roundToInt(),0,0)
            averageWidth -= 1
        }
        xyz.alphaline.mintimetablenew.utils.removeViews(
            arrayOf(
                binding.zeroPoint,
                binding.topMenu,
                binding.timeCell,
                binding.mainTable
            )
        )

        binding.zeroPoint.addView(
            ZeroPointView(
                super.tableContext,
                super.topMenuHeightPx.roundToInt(),
                super.leftMenuWidthPx.roundToInt(),
                super.menuColor
            )
        )

        for(i in (super.dayList).indices) {
            if (super.xEndLine) binding.topMenu.addView(
                XxisView(
                    super.tableContext,
                    super.topMenuHeightPx.roundToInt(),
                    super.averageWidth,
                    dayList[i],
                    super.menuColor,
                    super.menuTextColor,
                    super.menuTextSize
                )
                )
            else {
                if (i == (super.dayList).size - 1) binding.topMenu.addView (
                    XxisEndView(
                        super.tableContext,
                        super.topMenuHeightPx.roundToInt(),
                        super.averageWidth,
                        (super.dayList)[(super.dayList).size - 1],
                        super.menuColor,
                        super.menuTextColor,
                        dpToPx(tableContext, super.menuTextSize)
                    )
                        )
                else binding.topMenu.addView(
                    XxisView(
                        super.tableContext,
                        super.topMenuHeightPx.roundToInt(),
                        super.averageWidth,
                        dayList[i],
                        super.menuColor,
                        super.menuTextColor,
                        dpToPx(tableContext, super.menuTextSize)
                    )
                )
            }
        }

        super.recycleTimeCell()
    }

    fun updateSchedules(schedules: ArrayList<ScheduleEntity>) {
        super.schedules = schedules
        if (super.schedules.size == 0) {
            return
        }

        // On force la timetable de 8h à 19h, donc on ne recalcule pas le timing de la table en fonction des horaires des events
        // super.calculateTime(super.schedules)

        xyz.alphaline.mintimetablenew.utils.removeViews(
            arrayOf(
                binding.timeCell,
                binding.mainTable
            )
        )
        super.recycleTimeCell()

        super.schedules.map {entity ->
            binding.mainTable.addView(
                    ScheduleView(
                        super.tableContext,
                        entity,
                        super.cellHeightPx.roundToInt(),
                        super.averageWidth,
                        super.scheduleClickListener,
                        super.scheduleLongClickListener,
                        super.tableStartTime,
                        super.radiusStyle
                    )
                )
        }
    }


    fun setOnScheduleClickListener(listener: OnScheduleClickListener) {
        super.scheduleClickListener = listener
    }

    fun setOnScheduleLongClickListener(listener: OnScheduleLongClickListener) {
        super.scheduleLongClickListener = listener
    }

    fun setOnTimeCellClickListener(listener: OnTimeCellClickListener) {
        super.timeCellClickListener = listener
    }


    //attrs setting function

    fun radiusOption(option: Int) {
        super.radiusStyle = option
        updateSchedules(super.schedules)
    }

    fun cellColor(color: Int) {
        super.cellColor = color
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun menuColor(color: Int) {
        super.menuColor = color
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun lineColor(color: Int) {
        super.lineColor = color
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun isTwentyFourHourClock(boolean: Boolean) {
        super.isTwentyFourHourClock = boolean
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun isFullWidth(boolean: Boolean) {
        super.isFullScreen = boolean
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun widthPadding(dp: Int) {
        super.widthPadding = dp
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun xEndLine(boolean: Boolean) {
        super.xEndLine = boolean
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun yEndLine(boolean: Boolean) {
        super.yEndLine = boolean
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }

    fun border(boolean: Boolean) {
        super.border = boolean
        initTable(super.dayList)
        updateSchedules(super.schedules)
    }
}
