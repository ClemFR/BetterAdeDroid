package xyz.alphaline.mintimetablenew.model

import android.view.View
import java.io.Serializable

class ScheduleEntity(
    var originId: Int,
    var scheduleName: String,
    var roomInfo: List<String>,
    var scheduleDay: Int,
    var startTime: String,
    var endTime: String,
    var backgroundColor: String = "#dddddd",
    var textColor: String = "#ffffff",
    var professor: List<String>,
    var groups: List<String> ) : Serializable {

    private var isProfessorItalic = false
    private var isRoomItalic = false

    var mOnClickListener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    fun setProfessorItalic(professorItalic: Boolean): ScheduleEntity {
        isProfessorItalic = professorItalic
        return this
    }

    fun setRoomItalic(roomItalic: Boolean): ScheduleEntity {
        isRoomItalic = roomItalic
        return this
    }

    fun getProfessorItalic(): Boolean {
        return isProfessorItalic
    }

    fun getRoomItalic(): Boolean {
        return isRoomItalic
    }

}
