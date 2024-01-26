package xyz.alphaline.mintablenew.tableinterface

import xyz.alphaline.mintablenew.model.ScheduleEntity

interface OnScheduleLongClickListener {
    fun scheduleLongClicked(entity: ScheduleEntity)
}