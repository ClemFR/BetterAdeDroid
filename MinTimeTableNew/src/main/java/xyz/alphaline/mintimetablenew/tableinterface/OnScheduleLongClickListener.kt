package xyz.alphaline.mintimetablenew.tableinterface

import xyz.alphaline.mintimetablenew.model.ScheduleEntity

interface OnScheduleLongClickListener {
    fun scheduleLongClicked(entity: ScheduleEntity)
}