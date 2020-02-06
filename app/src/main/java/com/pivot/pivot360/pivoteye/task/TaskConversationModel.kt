package com.pivot.pivot360.pivoteye.task

data class TaskConversationModel(
    var cardId: String = "",
    var title: String = "",
    var value: String = "",
    var timerValueSummary: String = "",
    var description: String = "",
    var status: String = "",
    var taskType: String = "",
    var updatedAt: String = "",
    var modelType: Int = -1,
    var cardType: String = "",
    var taskStatus: String = "",
    var checkBoxIndex: String = "",
    var checkBoxValue: String = "",
    var switchValue: Boolean = false,
    var showCheckMark: Boolean = false,
    var summaryUserEnteredValue: String = "",
    var showCompleteButton: Boolean = false

) {

    companion object {
        const val TYPE_HEADER = 27
        const val TYPE_TOGGLE = 25
        const val TYPE_RESULT = 29
        const val TYPE_VIEW_ONE = 30
        const val TYPE_VIEW_COMPLETE = 31
        const val TYPE_VIEW_TIMER = 32
        const val TYPE_VIEW_SWITCH = 33
        const val TYPE_HEADER_FOUND_AS = 34


    }
}
