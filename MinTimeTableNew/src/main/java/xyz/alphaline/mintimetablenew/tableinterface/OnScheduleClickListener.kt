package xyz.alphaline.mintimetablenew.tableinterface

import xyz.alphaline.mintimetablenew.model.ScheduleEntity

interface OnScheduleClickListener {
    fun scheduleClicked(entity: ScheduleEntity)
}