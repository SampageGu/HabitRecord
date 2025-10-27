package com.habitrecord.app.utils

/**
 * 预设的习惯颜色
 */
object HabitColors {

    data class ColorOption(
        val color: String,
        val name: String
    )

    val availableColors = listOf(
        ColorOption("#6200EE", "紫色"),
        ColorOption("#FF5722", "橙红"),
        ColorOption("#E91E63", "粉红"),
        ColorOption("#9C27B0", "紫红"),
        ColorOption("#673AB7", "深紫"),
        ColorOption("#3F51B5", "靛蓝"),
        ColorOption("#2196F3", "蓝色"),
        ColorOption("#03A9F4", "浅蓝"),
        ColorOption("#00BCD4", "青色"),
        ColorOption("#009688", "青绿"),
        ColorOption("#4CAF50", "绿色"),
        ColorOption("#8BC34A", "浅绿"),
        ColorOption("#CDDC39", "黄绿"),
        ColorOption("#FFEB3B", "黄色"),
        ColorOption("#FFC107", "琥珀"),
        ColorOption("#FF9800", "橙色"),
        ColorOption("#795548", "棕色"),
        ColorOption("#607D8B", "蓝灰"),
    )

    const val DEFAULT_COLOR = "#6200EE" // 默认紫色
}

