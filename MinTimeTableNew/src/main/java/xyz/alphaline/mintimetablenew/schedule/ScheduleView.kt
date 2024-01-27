package xyz.alphaline.mintimetablenew.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.alphaline.mintimetablenew.databinding.ItemScheduleBinding
import xyz.alphaline.mintimetablenew.model.ScheduleEntity
import xyz.alphaline.mintimetablenew.tableinterface.OnScheduleClickListener
import xyz.alphaline.mintimetablenew.tableinterface.OnScheduleLongClickListener
import xyz.alphaline.mintimetablenew.utils.dpToPx
import xyz.alphaline.mintimetablenew.utils.getTotalMinute

@SuppressLint("ViewConstructor")
class ScheduleView(context: Context,
                   entity: ScheduleEntity,
                   height: Int,
                   width: Int,
                   scheduleClickListener: OnScheduleClickListener?,
                   scheduleLongClickListener: OnScheduleLongClickListener?,
                   tableStartTime: Int,
                   radiusStyle: Int
) : ConstraintLayout(context) {
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
        // inflater.inflate(R.layout.item_schedule, this, true)
        val binding = ItemScheduleBinding.inflate(inflater, this, true)

        val duration = getTotalMinute(entity.endTime) - getTotalMinute(
            entity.startTime
        )

        val layoutSetting = LayoutParams(width, ((height * duration).toDouble() / 60).toInt())
        layoutSetting.topMargin = (((height * getTotalMinute(
            entity.startTime
        )).toDouble() / 60) - (height * tableStartTime)).toInt()
        layoutSetting.leftMargin = width * entity.scheduleDay

        layoutSetting.topToTop = LayoutParams.PARENT_ID
        layoutSetting.leftToLeft = LayoutParams.PARENT_ID
        layoutSetting.rightToRight = LayoutParams.PARENT_ID

        binding.tableItem.layoutParams = layoutSetting

        binding.tableItem.setOnClickListener {
            scheduleClickListener?.scheduleClicked(entity)
            entity.mOnClickListener?.onClick(binding.tableItem)
        }

        binding.tableItem.setOnLongClickListener {
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
                binding.name.layoutParams = layoutText
                binding.name.gravity = Gravity.RIGHT
                binding.room.gravity = Gravity.RIGHT

                border.cornerRadii = floatArrayOf(cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius, 0f, 0f)
            }
            RIGHT -> {
                layoutText.rightMargin = (width.toDouble() * 0.15).toInt()
                binding.name.layoutParams = layoutText

                border.cornerRadii = floatArrayOf(0f, 0f, cornerRadius, cornerRadius, 0f, 0f, cornerRadius, cornerRadius)
            }
            ALL -> {
                border.cornerRadius = roundRadius
            }
        }

        binding.tableItem.background = border

        binding.name.text = entity.scheduleName
        binding.room.text = entity.roomInfo.joinToString(", ") + " (" + entity.groups.joinToString(" / ") + ")"
        binding.professor.text = entity.professor.joinToString(", ")

        binding.name.setTextColor(Color.parseColor(entity.textColor))
        binding.room.setTextColor(Color.parseColor(entity.textColor))
        binding.professor.setTextColor(Color.parseColor(entity.textColor))
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
