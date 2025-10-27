package com.habitrecord.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitrecord.app.utils.HabitIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitScreen(
    habitName: String,
    selectedIcon: String,
    selectedColor: String,
    onNameChange: (String) -> Unit,
    onIconSelect: (String) -> Unit,
    onColorSelect: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑习惯") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = habitName.isNotBlank()
                    ) {
                        Text("保存", color = if (habitName.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 图标显示
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = Color(android.graphics.Color.parseColor(selectedColor)).copy(alpha = 0.3f),
                onClick = { showIconPicker = true }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = HabitIcons.getIconById(selectedIcon),
                        contentDescription = "习惯图标",
                        modifier = Modifier.size(50.dp),
                        tint = Color(android.graphics.Color.parseColor(selectedColor))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { showIconPicker = true }) {
                Text("选择图标")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 颜色选择
            Text(
                text = "选择颜色",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 颜色预览和选择按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(selectedColor)))
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                TextButton(onClick = { showColorPicker = true }) {
                    Text("更换颜色")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 名称输入
            OutlinedTextField(
                value = habitName,
                onValueChange = onNameChange,
                label = { Text("习惯名称") },
                placeholder = { Text("例如：健身、跑步、阅读...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (habitName.isBlank()) {
                Text(
                    text = "请输入习惯名称",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // 图标选择器对话框
        if (showIconPicker) {
            IconPickerDialog(
                selectedIcon = selectedIcon,
                onIconSelect = { icon ->
                    onIconSelect(icon)
                    showIconPicker = false
                },
                onDismiss = { showIconPicker = false }
            )
        }

        // 颜色选择器对话框
        if (showColorPicker) {
            ColorPickerDialog(
                selectedColor = selectedColor,
                onColorSelect = { color ->
                    onColorSelect(color)
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

