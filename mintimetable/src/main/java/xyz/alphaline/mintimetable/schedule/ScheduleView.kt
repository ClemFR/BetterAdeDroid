package xyz.alphaline.mintablenew.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import xyz.alphaline.mintablenew.BaseTimeTable
import xyz.alphaline.mintablenew.R
import xyz.alphaline.mintablenew.model.ScheduleEntity
import xyz.alphaline.mintablenew.tableinterface.OnScheduleClickListener
import xyz.alphaline.mintablenew.tableinterface.OnScheduleLongClickListener
import xyz.alphaline.mintablenew.utils.dpToPx
import xyz.alphaline.mintablenew.utils.getTotalMinute

@SuppressLint("ViewConstructor")
class ScheduleView(context: Context,
                   entity: ScheduleEntity,
                   height: Int,
                   width: Int,
                   scheduleClickListener: OnScheduleClickListener?,
                   scheduleLongClickListener: OnScheduleLongClickListener?,
                   tableStartTime: Int,
                   radiusStyle: Int
) : LinearLayout(context) {
    init {
        setting(
            context,
            entity,
            height,
            width,
            scheduleClickListener,
            scheduleLongClickListener,
            tableStartTime,
            radiusStyle
        )
    }

    protected lateinit var tableItem: LinearLayout
    protected lateinit var name: TextView
    protected lateinit var room: TextView

    @SuppressLint("RtlHardcoded")
    private fun setting(context: Context,
                        entity: ScheduleEntity,
                        height: Int,
                        width: Int,
                        scheduleClickListener: OnScheduleClickListener?,
                        scheduleLongClickListener: OnScheduleLongClickListener?,
                        tableStartTime: Int,
                        radiusStyle: Int
    ) {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_schedule, this, true)

        tableItem = findViewById(R.id.tableItem)
        name = findViewById(R.id.name)
        room = findViewById(R.id.room)

        val duration = getTotalMinute(entity.endTime) - getTotalMinute(
            entity.startTime
        )

        val layoutSetting = LayoutParams(width, ((height * duration).toDouble() / 60).toInt())
        layoutSetting.topMargin = (((height * getTotalMinute(
            entity.startTime
        )).toDouble() / 60) - (height * tableStartTime)).toInt()
        layoutSetting.leftMargin = width * entity.scheduleDay

        tableItem.layoutParams = layoutSetting

        tableItem.setOnClickListener {
            scheduleClickListener?.scheduleClicked(entity)
            entity.mOnClickListener?.onClick(tableItem)
        }

        tableItem.setOnLongClickListener {
            scheduleLongClickListener?.scheduleLongClicked(entity)
            return@setOnLongClickListener true
        }


        val layoutText = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val cornerRadius =
            dpToPx(context, RADIUS.toFloat())
        val roundRadius =
            dpToPx(context, ROUND.toFloat())

        val border = GradientDrawable()
        border.setColor(Color.parseColor(entity.backgroundColor))
        border.shape = GradientDrawable.RECTANGLE

        when (radiusStyle) {
            NONE -> {}
            LEFT -> {
                layoutText.leftMargin = (width.toDouble() * 0.15).toInt()
                tableItem.gravity = Gravity.RIGHT
                name.layoutParams = layoutText
                name.gravity = Gravity.RIGHT
                room.gravity = Gravity.RIGHT

                border.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius, 0f, 0f)
            }
            RIGHT -> {
                layoutText.rightMargin = (width.toDouble() * 0.15).toInt()
                name.layoutParams = layoutText

                border.cornerRadii = floatArrayOf(0f, 0f, cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius)
            }
            ALL -> {
                border.cornerRadius = roundRadius
            }
        }

        tableItem.background = border

        name.text = entity.scheduleName
        room.text = entity.roomInfo

        name.setTextColor(Color.parseColor(entity.textColor))
        room.setTextColor(Color.parseColor(entity.textColor))
    }

    companion object {
        const val NONE = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val ALL = 3
        private const val RADIUS = 30
        private const val ROUND = 15
    }
}
