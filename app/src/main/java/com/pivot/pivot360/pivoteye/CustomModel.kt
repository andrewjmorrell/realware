package com.pivot.pivot360.pivoteye

data class CustomModel(
        var text: String = "",
        var modelType: Int = -1
) {

    companion object {
        const val TYPE_HEADER = 27
        const val TYPE_TOGGLE = 25
        const val TYPE_RESULT = 29
    }
}
