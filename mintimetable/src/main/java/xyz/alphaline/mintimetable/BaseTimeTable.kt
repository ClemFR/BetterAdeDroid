package xyz.alphaline.mintablenew

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.alphaline.mintablenew.cell.TableCellView
import xyz.alphaline.mintablenew.cell.YxisEndView
import xyz.alphaline.mintablenew.cell.YxisView
import xyz.alphaline.mintablenew.model.ScheduleEntity
import xyz.alphaline.mintablenew.tableinterface.OnScheduleClickListener
import xyz.alphaline.mintablenew.tableinterface.OnScheduleLongClickListener
import xyz.alphaline.mintablenew.tableinterface.OnTimeCellClickListener
import xyz.alphaline.mintablenew.utils.dpToPx
import xyz.alphaline.mintablenew.utils.getHour
import xyz.alphaline.mintablenew.R
import kotlin.math.roundToInt

open class BaseTimeTable : LinearLayout {

    protected var topMenuHeight: Int = 20
    protected var leftMenuWidth: Int = 30
    protected var cellHeight: Int = 50

    protected var isRatio: Boolean = false
    protected var cellRatio: Float = 0f

    protected var tableContext: Context = context

    protected var topMenuHeightPx: Float = 0.0f
    protected var leftMenuWidthPx: Float = 0.0f
    protected var cellHeightPx: Float = 0.0f
    protected var averageWidth: Int = 0
    protected var widthPaddingPx: Float = 0.0f

    protected var tableStartTime: Int = 9
    protected var tableEndTime: Int = 16

    protected var dayList: Array<String> = arrayOf()

    protected var radiusStyle: Int = 0
    protected var isTwentyFourHourClock = true
    protected var cellColor = 0
    protected var menuColor = 0
    protected var lineColor = 0
    protected var menuTextColor = 0
    protected var menuTextSize = 0f

    protected var isFullScreen = false
    protected var widthPadding = 0

    protected var scheduleClickListener: OnScheduleClickListener? = null
    protected var timeCellClickListener: OnTimeCellClickListener? = null
    protected var scheduleLongClickListener: OnScheduleLongClickListener? = null

    protected var border: Boolean = false
    protected var xEndLine: Boolean = false
    protected var yEndLine: Boolean = false

    protected var schedules: ArrayList<ScheduleEntity> = ArrayList()

    protected lateinit var mainTable: LinearLayout
    protected lateinit var topMenu: LinearLayout
    protected lateinit var leftMenu: LinearLayout
    protected lateinit var timeCell: LinearLayout
    protected lateinit var timetable: LinearLayout
    protected lateinit var borderBox: LinearLayout
    protected lateinit var zeroPoint: LinearLayout


    constructor(context: Context) : super(context){
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }



    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun initView(context: Context, attrs: AttributeSet?) {
        tableContext = context
        val inflater = LayoutInflater.from(tableContext)
        val v = inflater.inflate(R.layout.mintable, this, false)
        addView(v)

        mainTable = findViewById(R.id.mainTable)
        topMenu = findViewById(R.id.topMenu)
        leftMenu = findViewById(R.id.leftMenu)
        timeCell = findViewById(R.id.timeCell)
        timetable = findViewById(R.id.timetable)
        borderBox = findViewById(R.id.borderBox)
        zeroPoint = findViewById(R.id.zeroPoint)


        if (attrs == null) {
            return
        }

        val array = tableContext.obtainStyledAttributes(attrs, R.styleable.MinTimeTableView)

        radiusStyle = array.getInt(R.styleable.MinTimeTableView_radiusOption, 0)
        isTwentyFourHourClock = array.getBoolean(R.styleable.MinTimeTableView_isTwentyFourHourClock, true)
        cellColor = array.getColor(R.styleable.MinTimeTableView_cellColor, 0)
        menuColor = array.getColor(R.styleable.MinTimeTableView_menuColor, 0)
        lineColor = array.getColor(R.styleable.MinTimeTableView_lineColor,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                resources.getColor(R.color.default_line, null)
            else
                resources.getColor(R.color.default_line))

        border = array.getBoolean(R.styleable.MinTimeTableView_border, false)
        xEndLine = array.getBoolean(R.styleable.MinTimeTableView_xEndLine, false)
        yEndLine = array.getBoolean(R.styleable.MinTimeTableView_yEndLine, false)

        if(lineColor != 0) {
            mainTable.setBackgroundColor(lineColor)
            topMenu.setBackgroundColor(lineColor)
            leftMenu.setBackgroundColor(lineColor)
        }

        isFullScreen = array.getBoolean(R.styleable.MinTimeTableView_isFullWidth, false)
        widthPadding = array.getInteger(R.styleable.MinTimeTableView_widthPadding, 0)

        menuTextColor = array.getColor(R.styleable.MinTimeTableView_menuTextColor, 0)
        menuTextSize = array.getFloat(R.styleable.MinTimeTableView_menuTextSize, 0f)

        array.recycle()
    }

    protected fun calculateTime (schedules: ArrayList<ScheduleEntity>) {
        tableStartTime =
            getHour(schedules[0].startTime)
        tableEndTime = 16
        schedules.map {entity ->
            if(getHour(entity.startTime) < tableStartTime)
                tableStartTime =
                    getHour(entity.startTime)
            if(getHour(entity.endTime) >= tableEndTime)
                tableEndTime = getHour(entity.endTime) + 1
        }
        if ((tableEndTime - tableStartTime) < 7) {
            tableEndTime = tableStartTime + 7
        }
    }

    protected fun recycleTimeCell () {
        for(i in 0 until (tableEndTime - tableStartTime)) {
            dayList.forEach { day -> val j: Int = dayList.indexOf(day)
                mainTable.addView(
                    TableCellView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        averageWidth,
                        (j * averageWidth),
                        (i * cellHeightPx.roundToInt()),
                        cellColor,
                        timeCellClickListener,
                        j,
                        (tableStartTime + i)
                    )
                )
            }
            val hour = if (isTwentyFourHourClock) (tableStartTime + i)
            else {
                if ((tableStartTime + i)!=12) (tableStartTime + i) % 12
                else (tableStartTime + i)
            }
            if (yEndLine) {
                timeCell.addView(
                    YxisView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor,
                        menuTextColor,
                        menuTextSize
                    )
                )
            }
            else {
                if (i == (tableEndTime - tableStartTime) - 1) timeCell.addView(
                    YxisEndView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor,
                        menuTextColor,
                        dpToPx(tableContext, menuTextSize)
                    )
                )
                else timeCell.addView(
                    YxisView(
                        tableContext,
                        cellHeightPx.roundToInt(),
                        leftMenuWidthPx.roundToInt(),
                        hour.toString(),
                        menuColor,
                        menuTextColor,
                        dpToPx(tableContext, menuTextSize)
                    )
                )
            }

        }
    }

}