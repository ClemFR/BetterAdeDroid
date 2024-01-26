package xyz.alphaline.mintablenew.tableinterface

import xyz.alphaline.mintablenew.model.ScheduleEntity

interface OnScheduleClickListener {
    fun scheduleClicked(entity: ScheduleEntity)
}