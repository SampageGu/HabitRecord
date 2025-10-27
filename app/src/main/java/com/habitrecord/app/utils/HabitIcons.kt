package com.habitrecord.app.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 习惯图标管理
 */
object HabitIcons {

    data class IconOption(
        val id: String,
        val icon: ImageVector,
        val description: String
    )

    val availableIcons = listOf(
        IconOption("fitness", Icons.Default.FitnessCenter, "健身"),
        IconOption("run", Icons.Default.DirectionsRun, "跑步"),
        IconOption("book", Icons.Default.MenuBook, "阅读"),
        IconOption("study", Icons.Default.School, "学习"),
        IconOption("music", Icons.Default.MusicNote, "音乐"),
        IconOption("code", Icons.Default.Code, "编程"),
        IconOption("meditation", Icons.Default.SelfImprovement, "冥想"),
        IconOption("water", Icons.Default.WaterDrop, "喝水"),
        IconOption("sleep", Icons.Default.Bedtime, "睡眠"),
        IconOption("food", Icons.Default.Restaurant, "饮食"),
        IconOption("brush", Icons.Default.Brush, "绘画"),
        IconOption("camera", Icons.Default.CameraAlt, "摄影"),
        IconOption("bike", Icons.Default.DirectionsBike, "骑行"),
        IconOption("walk", Icons.Default.DirectionsWalk, "散步"),
        IconOption("heart", Icons.Default.Favorite, "健康"),
        IconOption("star", Icons.Default.Star, "目标"),
        IconOption("check", Icons.Default.CheckCircle, "习惯"),
        IconOption("flag", Icons.Default.Flag, "挑战"),
        IconOption("default", Icons.Default.Circle, "默认")
    )

    fun getIconById(id: String): ImageVector {
        return availableIcons.find { it.id == id }?.icon ?: Icons.Default.Circle
    }
}

