package com.example.lovetimer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.lovetimer.ui.theme.LoveTimerTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.logging.Level
import java.util.logging.Logger


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoveTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoveTimerApp()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoveTimerApp() {
    val context = LocalContext.current

    var updateTrigger by remember { mutableStateOf(0) }
    val currentTrigger by rememberUpdatedState(updateTrigger)
    val showCustomDateTimePicker = remember { mutableStateOf(false) }
    // 从SharedPreferences加载URI和开始时间
    val (initialUri, initialStartTime) = loadPreferences(context)
    Logger.getGlobal().log(Level.WARNING, initialUri?.toString() + "得到的URL ckkkkkkkkk")
    var startTime by rememberSaveable { mutableStateOf(initialStartTime) }
    var selectedImageUri by rememberSaveable { mutableStateOf(initialUri) }
    val triggerRecompositionKey = remember { mutableStateOf(0) }

    val openDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { selectedUri ->
                try {
                    selectedImageUri = uri.toString()
                    updateTrigger = updateTrigger + 1
                    triggerRecompositionKey.value++
//val uri = uriString?.let { Uri.parse(it) }
                    savePreferences(context, selectedImageUri, null)
                    Logger.getGlobal()
                        .log(Level.WARNING, uri?.toString() + "照片不是不是的空的啊 ckkkkkkkkk")
                    Logger.getGlobal().log(
                        Level.WARNING,
                        selectedImageUri?.toString() + "selectedImageUri照片不是不是的空的啊 ckkkkkkkkk"
                    )

                    // 请求持久化权限
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(selectedUri, takeFlags)

                    // 保存URI以便未来使用
//                    savePreferences(context, selectedUri.toString(), null)

                    Logger.getGlobal()
                        .log(Level.WARNING, uri?.toString() + "照片的空的啊 ckkkkkkkkk")

                } catch (e: SecurityException) {
                    Logger.getGlobal().log(Level.SEVERE, "请求持久化URI权限失败", e)
                }
            }

        }


    Column {

        Logger.getAnonymousLogger().log(Level.WARNING, startTime.toString() + "ckkkkkk")
        Logger.getAnonymousLogger().log(Level.WARNING, startTime + "ckkkkkk")
        // Display Love Time
        val (startYear, startMonth, startDay) = startTime.toString().split("-")
        LoveTimeDisplay(
            selectedImageUri,
            selectedImageUri,
            startYear.toInt(),
            startMonth.toInt(),
            startDay.toInt()
        )


        Row {
            Button(
                onClick = { openDocumentLauncher.launch(arrayOf("image/*")) },
                modifier = Modifier.padding(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 0.dp)
            ) {
                Text("Select Image")
            }

            Button(onClick = { showCustomDateTimePicker.value = true }) {
                Text("Select Start Date")
            }
        }


        CustomDateTimePickerDialog(
            showDialog = showCustomDateTimePicker,
            onDateTimeSelected = { dateTime, dataTimeString ->
                startTime = dataTimeString
                savePreferences(context, null, dataTimeString)
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoveTimeDisplay(
    selectedImageUri: String?,
    targetImageUri: String?,
    startYear: Int,
    startMonth: Int,
    startDay: Int,
    startHour: Int = 0,
    startMinute: Int = 0,
    startSecond: Int = 0
) {


    Logger.getGlobal().log(
        Level.WARNING,
        selectedImageUri.toString() + " selectedImageUri.toString() ckkkkkkkkk 啊啊啊啊"
    )
    var imageUriCk by remember { mutableStateOf(selectedImageUri) }
    LaunchedEffect(selectedImageUri) {
        imageUriCk = selectedImageUri
    }

    Logger.getGlobal()
        .log(Level.WARNING, imageUriCk.toString() + " imageUriCk.toString() ckkkkkkkkk 啊啊啊啊")
    selectedImageUri?.let { uriString ->


        Logger.getGlobal().log(
            Level.WARNING,
            uriString?.let { Uri.parse(it) }?.toString() + " 更新了界面 ckkkkkkkkk 啊啊啊啊"
        )
        Image(
            painter = rememberAsyncImagePainter(
                model = Uri.parse(selectedImageUri),
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Loading -> Logger.getGlobal()
                            .info("图片加载中...")

                        is AsyncImagePainter.State.Success -> Logger.getGlobal()
                            .info("图片加载成功！")

                        is AsyncImagePainter.State.Error -> Logger.getGlobal().info("图片加载失败！")
                        else -> {} // 其他状态可以根据需要处理
                    }
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 2.dp, top = 120.dp, end = 2.dp, bottom = 20.dp)
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop
        )
    } ?: Image(
        painter = painterResource(id = R.drawable.default_background),
        contentDescription = null,
        modifier = Modifier
            .padding(start = 2.dp, top = 120.dp, end = 2.dp, bottom = 20.dp)
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        contentScale = ContentScale.Crop
    )

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    val startTime = LocalDateTime.of(startYear, startMonth, startDay, 0, 0)
    val years = ChronoUnit.YEARS.between(startTime, currentTime)
    val months = ChronoUnit.MONTHS.between(startTime, currentTime) % 12
    val days = ChronoUnit.DAYS.between(startTime.plusYears(years).plusMonths(months), currentTime)
    val hours = ChronoUnit.HOURS.between(
        startTime.plusYears(years).plusMonths(months).plusDays(days),
        currentTime
    )
    val minutes = ChronoUnit.MINUTES.between(
        startTime.plusYears(years).plusMonths(months).plusDays(days).plusHours(hours), currentTime
    ) % 60
    val seconds = ChronoUnit.SECONDS.between(
        startTime.plusYears(years).plusMonths(months).plusDays(days).plusHours(hours)
            .plusMinutes(minutes), currentTime
    ) % 60

    LaunchedEffect(key1 = currentTime) {
        while (true) {
            delay(1000);
            // This ensures the loop runs every second
            currentTime = LocalDateTime.now()
        }
    }

//    LaunchedEffect(key1 = selectedImageUri) {
//        while (true) {
//            delay(1000);
//            imageUriCk = targetImageUri
//            // This ensures the loop runs every second
//        }
//    }

    Text(
        text = "Love Time: $years years, $months months, $days days, $hours hours, $minutes minutes, $seconds seconds",
        modifier = Modifier
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoveTimerTheme {
        LoveTimerApp()
    }
}


// 使用SharedPreferences保存URI和开始时间
@RequiresApi(Build.VERSION_CODES.O)
fun savePreferences(context: Context, uri: String?, startTime: String?) {
    if (uri != null) {
        val sharedPref = context.getSharedPreferences("LoveTimerPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selectedImageUri", uri.toString())
            Logger.getGlobal()
                .log(Level.WARNING, uri?.toString() + "selectedImageUri cunchu  ckkkkkkkkk")

            apply()
        }
    }

    if (startTime != null) {
        val sharedPref = context.getSharedPreferences("LoveTimerPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("startTime", startTime)
            apply()
        }
    }
}

// 从SharedPreferences加载URI和开始时间
@RequiresApi(Build.VERSION_CODES.O)
fun loadPreferences(context: Context): Pair<String?, String?> {
    val sharedPref = context.getSharedPreferences("LoveTimerPreferences", Context.MODE_PRIVATE)
    val uriString = sharedPref.getString("selectedImageUri", null)
    val uri = uriString?.let { Uri.parse(it) }

    val dateTime = LocalDateTime.of(
        LocalDate.now().year,
        LocalDate.now().monthValue,
        LocalDate.now().dayOfMonth,
        0,
        0,
    )
    val curDataTimeString =
        dateTime.year.toString() + "-" + dateTime.monthValue.toString() + "-" + dateTime.dayOfMonth.toString() + "-" + dateTime.hour.toString() + "-" + dateTime.minute.toString()

    val dataTimeString = sharedPref.getString("startTime", curDataTimeString)

    Logger.getGlobal().log(Level.WARNING, uri?.toString() + "获取 URI  ckkkkkkkkk")
    Logger.getGlobal().log(Level.WARNING, dataTimeString?.toString() + "获取 日期  ckkkkkkkkk")

    return Pair(uriString, dataTimeString)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDateTimePickerDialog(
    showDialog: MutableState<Boolean>,
    onDateTimeSelected: (LocalDateTime, String) -> Unit
) {
    if (showDialog.value) {
        val year = remember { mutableStateOf("") }
        val month = remember { mutableStateOf("") }
        val day = remember { mutableStateOf("") }
        val hour = remember { mutableStateOf("0") } // 默认为0
        val minute = remember { mutableStateOf("0") } // 默认为0

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Select Date and Time") },
            text = {
                Column {
                    TextField(
                        value = year.value,
                        onValueChange = { year.value = it },
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = month.value,
                        onValueChange = { month.value = it },
                        label = { Text("Month") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    TextField(
                        value = day.value,
                        onValueChange = { day.value = it },
                        label = { Text("Day") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        val dateTime = LocalDateTime.of(
                            year.value.toIntOrNull() ?: LocalDate.now().year,
                            month.value.toIntOrNull() ?: LocalDate.now().monthValue,
                            day.value.toIntOrNull() ?: LocalDate.now().dayOfMonth,
                            hour.value.toIntOrNull() ?: 0,
                            minute.value.toIntOrNull() ?: 0,
                        )
                        val dataTimeString =
                            dateTime.year.toString() + "-" + dateTime.monthValue.toString() + "-" + dateTime.dayOfMonth.toString() + "-" + dateTime.hour.toString() + "-" + dateTime.minute.toString()
                        onDateTimeSelected(dateTime, dataTimeString)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

