package com.example.ui

import androidx.compose.animation.*
import androidx.compose.ui.layout.ContentScale
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SchoolAppMainContainer(viewModel: SchoolViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkThemeOverride.collectAsStateWithLifecycle()

    MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val screen = currentScreen) {
                is Screen.Login -> LoginScreen(viewModel)
                is Screen.StudentDashboard -> StudentDashboard(viewModel)
                is Screen.TeacherDashboard -> TeacherDashboard(viewModel)
                is Screen.AdminDashboard -> AdminDashboard(viewModel)
                is Screen.SolveTest -> SolveTestScreen(viewModel, screen.testId)
            }
        }
    }
}

// Minimal mathematical Crest Logo representing original circular design beautifully
@Composable
fun SchoolLogoCrest(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(105.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size.center
            val radius = size.minDimension / 2.0f

            // Outer Green Circle
            drawCircle(
                color = Color(0xFF1E9028),
                radius = radius - 4.dp.toPx(),
                style = Stroke(width = 4.dp.toPx())
            )
            // Secondary Thin Circle
            drawCircle(
                color = Color(0xFF1E9028),
                radius = radius - 12.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
            // Accent background glow
            drawCircle(
                color = Color(0xFFF24A1A).copy(alpha = 0.08f),
                radius = radius - 15.dp.toPx()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Hirawati School Crest logo icon",
                tint = Color(0xFFF24A1A),
                modifier = Modifier.size(42.dp)
            )
            Text(
                text = "ESTD: 2017",
                fontSize = 9.sp,
                color = Color(0xFF1E9028),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LoginScreen(viewModel: SchoolViewModel) {
    var selectedRole by remember { mutableStateOf("STUDENT") } // STUDENT, TEACHER, ADMIN
    var idInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val errorMsg by viewModel.loginError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("login_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SchoolLogoCrest()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "HIRAWATI SENIOR SECONDARY SCHOOL",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Text(
                    text = "Affiliated to CBSE, New Delhi (Up to 10th)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Role Selector Tab Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(4.dp)
                ) {
                    val roles = listOf("STUDENT" to "Student", "TEACHER" to "Teacher", "ADMIN" to "Admin")
                    roles.forEach { (roleCode, roleName) ->
                        val isSelected = selectedRole == roleCode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    selectedRole = roleCode
                                    idInput = ""
                                    passwordInput = ""
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = roleName,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ID input
                OutlinedTextField(
                    value = idInput,
                    onValueChange = { idInput = it },
                    label = { 
                        Text(
                            when(selectedRole) {
                                "STUDENT" -> "Student ID (e.g., S101)"
                                "TEACHER" -> "Teacher ID (e.g., T201)"
                                else -> "Admin ID (Type: admin)"
                            }
                        ) 
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "ID icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("id_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password lock") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (selectedRole == "ADMIN") {
                    Text(
                        text = "Admin check password: anjali301",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, start = 8.dp)
                    )
                }

                // Error message
                AnimatedVisibility(visible = errorMsg != null) {
                    errorMsg?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Login click button
                Button(
                    onClick = { viewModel.login(idInput, passwordInput, selectedRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Access Portal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Login, contentDescription = "Arrow")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "School Code: 65920 • Aff. No: 330923\nHirawati Senior Secondary School, Vill-Manda, P.S-Ismailpur, Bihar",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// STUDENT DASHBOARD SCREEN (Rich Layout containing multiple pages)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudentDashboard(viewModel: SchoolViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkThemeOverride.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("HOME") } // HOME, HOMEWORK, STUDY, TEST, NOTICES, GALLERY, AI_HUB

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.logout() },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout icon")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Hirawati School",
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${currentUser?.name ?: "Student Portal"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { viewModel.toggleDarkTheme() }) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Theme selection mode icon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                tonalElevation = 8.dp
            ) {
                val menuItems = listOf(
                    Triple("HOME", "Home", Icons.Default.Home),
                    Triple("HOMEWORK", "Homework", Icons.Default.Assignment),
                    Triple("STUDY", "Notes", Icons.Default.Book),
                    Triple("TEST", "Tests", Icons.Default.Quiz),
                    Triple("AI_HUB", "AI Helper", Icons.Default.SmartToy),
                    Triple("GALLERY", "Gallery", Icons.Default.PhotoLibrary)
                )

                menuItems.forEach { (tabCode, tabName, tabIcon) ->
                    val isSelected = activeTab == tabCode
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tabCode },
                        icon = { Icon(tabIcon, contentDescription = null) },
                        label = { Text(tabName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "HOME" -> StudentHomeTab(viewModel, onNavigateToTab = { activeTab = it })
                "HOMEWORK" -> StudentHomeworkTab(viewModel)
                "STUDY" -> StudentStudyTab(viewModel)
                "TEST" -> StudentTestTab(viewModel)
                "AI_HUB" -> StudentAIHubTab(viewModel)
                "GALLERY" -> StudentGalleryTab(viewModel)
            }
        }
    }
}

@Composable
fun StudentHomeTab(viewModel: SchoolViewModel, onNavigateToTab: (String) -> Unit) {
    val student by viewModel.currentUser.collectAsStateWithLifecycle()
    val attendancePercent by viewModel.myAttendancePercentage.collectAsStateWithLifecycle()
    val noticeList by viewModel.allNotices.collectAsStateWithLifecycle()
    val eventsList by viewModel.allEvents.collectAsStateWithLifecycle()
    val testResults by viewModel.allResults.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Namaste,",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = student?.name ?: "Student",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Class: ${student?.classLevel ?: "10th A"} • Roll No: ${student?.rollNo ?: 12}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        
                        // Dynamically award Badge based on attendance and test performance
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            if (attendancePercent >= 80.0) {
                                BadgeChip(label = "Regularity Star", color = Color(0xFF1E9028), icon = Icons.Default.Stars)
                            }
                            if (testResults.any { it.percentage >= 90.0 }) {
                                Spacer(modifier = Modifier.width(6.dp))
                                BadgeChip(label = "Brainiac", color = Color(0xFFFF9F1C), icon = Icons.Default.Bolt)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Avatar profile face",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Attendance % Progress Indicator Circle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Total Attendance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (attendancePercent >= 75.0) Color(0xFF1E9028).copy(alpha = 0.15f)
                                    else Color(0xFFF24A1A).copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (attendancePercent >= 75.0) "Good Standing" else "Short Attendance",
                                color = if (attendancePercent >= 75.0) Color(0xFF1E9028) else Color(0xFFF24A1A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(110.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = (attendancePercent / 100.0).toFloat(),
                            modifier = Modifier.size(110.dp),
                            color = if (attendancePercent >= 75.0) Color(0xFF1E9028) else Color(0xFFF24A1A),
                            strokeWidth = 10.dp,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${attendancePercent.toInt()}%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Strict dynamic attendance alert warning
                    if (attendancePercent < 75.0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF24A1A).copy(alpha = 0.10f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Strict warning",
                                tint = Color(0xFFF24A1A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOW ATTENDANCE WARNING! Minimum 75% attendance is required for board exams.",
                                color = Color(0xFFF24A1A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Today Classes & Quick Actions Row Grid
        item {
            Column {
                Text(
                    text = "Today Classes & Quick Panels",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ClassQuickCard(
                        time = "09:00 AM",
                        subject = "Maths",
                        teacher = "Mrs. Anjali Sen",
                        room = "Room 12",
                        modifier = Modifier.weight(1f)
                    )
                    ClassQuickCard(
                        time = "10:30 AM",
                        subject = "Physics",
                        teacher = "Mr. Rajesh Shah",
                        room = "Lab 2",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // NOTICES BOX
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "School Important Notices",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (noticeList.isEmpty()) {
                    EmptyBoxPlaceholders(msg = "No notices updated.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        noticeList.take(3).forEach { notice ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notice.title,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = notice.date,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notice.content,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Posted by: ${notice.postedBy}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // UPCOMING EVENTS & HOLIDAYS
        item {
            Column {
                Text(
                    text = "Upcoming School Events",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (eventsList.isEmpty()) {
                    EmptyBoxPlaceholders(msg = "No holidays or events scheduled.")
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(eventsList) { event ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when(event.type) {
                                        "Holiday" -> Color(0xFFFFF1F1)
                                        "Exam" -> Color(0xFFF1F6FF)
                                        else -> Color(0xFFF1FFF2)
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when(event.type) {
                                                        "Holiday" -> Color(0xFFF24A1A)
                                                        "Exam" -> Color(0xFF3F8CFF)
                                                        else -> Color(0xFF1E9028)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = event.type,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Text(
                                            text = event.date,
                                            fontSize = 9.sp,
                                            color = Color.Black.copy(alpha = 0.5f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = event.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = event.description,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.Black.copy(alpha = 0.7f),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Badge Chip representation
@Composable
fun BadgeChip(label: String, color: Color, icon: ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ClassQuickCard(time: String, subject: String, teacher: String, room: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(time, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subject, fontWeight = FontWeight.Black, fontSize = 15.sp)
            Text(teacher, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text(room, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// HOMEWORK TAB
@Composable
fun StudentHomeworkTab(viewModel: SchoolViewModel) {
    val homeworkList by viewModel.allHomework.collectAsStateWithLifecycle()
    val submissions by viewModel.mySubmissions.collectAsStateWithLifecycle()

    var selectedHomeworkForSubmission by remember { mutableStateOf<Homework?>(null) }
    var solutionTextInput by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    if (selectedHomeworkForSubmission != null) {
        val hw = selectedHomeworkForSubmission!!
        val alreadySubmitted = submissions.firstOrNull { it.homeworkId == hw.id }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selectedHomeworkForSubmission = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Homework Submission", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(hw.title, fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Subject: ${hw.subject} • Class: ${hw.classLevel}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(hw.description, fontSize = 14.sp, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Given by: ${hw.teacherName}", fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    Text("Strict Deadline: ${hw.deadline}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (alreadySubmitted != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E9028))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Already Submitted!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your Submission Answer:", fontSize = 12.sp, color = Color.Gray)
                        Text(alreadySubmitted.submissionText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Teacher Feedback & Score:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(alreadySubmitted.feedback, fontSize = 13.sp, color = Color.Black)
                        Text("Grade Awarded: ${alreadySubmitted.grade}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                Text("Write Your Solution/Answer details:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = solutionTextInput,
                    onValueChange = { solutionTextInput = it },
                    placeholder = { Text("Describe your solution, equations, or copy-paste links here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (solutionTextInput.isNotEmpty()) {
                            viewModel.recordHomeworkSubmission(hw.id, solutionTextInput)
                            solutionTextInput = ""
                            selectedHomeworkForSubmission = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Answers Live", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Your Assigned Homework", fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))

            if (homeworkList.isEmpty()) {
                EmptyBoxPlaceholders(msg = "Hooray! No pending homework assigned by your teachers.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(homeworkList) { hw ->
                        val matchedSubmission = submissions.firstOrNull { it.homeworkId == hw.id }
                        val isDone = matchedSubmission != null

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedHomeworkForSubmission = hw },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(hw.subject, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isDone) Color(0xFF1E9028).copy(alpha = 0.15f)
                                                else Color(0xFFF24A1A).copy(alpha = 0.15f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isDone) "Submitted" else "Pending Due",
                                            color = if (isDone) Color(0xFF1E9028) else Color(0xFFF24A1A),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(hw.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(hw.description, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Given by: ${hw.teacherName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text("Deadline: ${hw.deadline}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// NOTES & MATERIALS TAB
@Composable
fun StudentStudyTab(viewModel: SchoolViewModel) {
    val materials by viewModel.allStudyMaterials.collectAsStateWithLifecycle()

    var activeMaterialForDetail by remember { mutableStateOf<StudyMaterial?>(null) }
    val aiSummaryLoading by viewModel.aiSummarizeLoading.collectAsStateWithLifecycle()
    val aiSummaryResult by viewModel.aiSummarizeResponse.collectAsStateWithLifecycle()

    if (activeMaterialForDetail != null) {
        val mat = activeMaterialForDetail!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    activeMaterialForDetail = null
                    // Reset summary when leaving
                    viewModel.callAINotesSummarizer("")
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Study Notes & Materials", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(mat.subject, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text(mat.category, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(mat.title, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Chapter: ${mat.chapter}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(mat.content, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Notes Summarizer integrated directly!
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✨ Gemini Study Summarizer",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = { viewModel.callAINotesSummarizer(mat.content) },
                            enabled = !aiSummaryLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (aiSummaryLoading) "Summarizing..." else "Generative Summary", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (aiSummaryLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else if (aiSummaryResult != null) {
                        Text(
                            text = aiSummaryResult!!,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else {
                        Text(
                            text = "Tap candidate above to ask Gemini to summarize long notes and extract mathematical formulas or list takeaways instantly.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("School Chapter-wise Material", fontWeight = FontWeight.Black, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))

            if (materials.isEmpty()) {
                EmptyBoxPlaceholders(msg = "Digital classroom is clean. No study materials published yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(materials) { mat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeMaterialForDetail = mat },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when(mat.category) {
                                                "Video Lecture" -> Color(0xFFFFF1F1)
                                                "Important Questions" -> Color(0xFFF1FFF2)
                                                else -> Color(0xFFF1F6FF)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when(mat.category) {
                                            "Video Lecture" -> Icons.Default.PlayArrow
                                            "Important Questions" -> Icons.Default.HelpOutline
                                            else -> Icons.Default.Description
                                        },
                                        contentDescription = null,
                                        tint = when(mat.category) {
                                            "Video Lecture" -> Color(0xFFF24A1A)
                                            "Important Questions" -> Color(0xFF1E9028)
                                            else -> Color(0xFF3F8CFF)
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1.0f)) {
                                    Text(
                                        text = mat.subject,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = mat.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Chapter: ${mat.chapter}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = "Arrow")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ONLINE TEST TAB
@Composable
fun StudentTestTab(viewModel: SchoolViewModel) {
    val tests by viewModel.allTests.collectAsStateWithLifecycle()
    val results by viewModel.allResults.collectAsStateWithLifecycle()
    val leaderboardResults by viewModel.allResultsOrdered.collectAsStateWithLifecycle()

    var activeTabSubState by remember { mutableStateOf("QUIZZES") } // QUIZZES, RESULTS, LEADERBOARD

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .padding(4.dp)
        ) {
            val sections = listOf("QUIZZES" to "MCQ Quizzes", "RESULTS" to "Report Card", "LEADERBOARD" to "Leaderboard")
            sections.forEach { (secCode, secName) ->
                val isSelected = activeTabSubState == secCode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { activeTabSubState = secCode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = secName,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTabSubState) {
            "QUIZZES" -> {
                if (tests.isEmpty()) {
                    EmptyBoxPlaceholders(msg = "Test board is clean! No MCQ exams published.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(tests) { test ->
                            val alreadySolvedResult = results.firstOrNull { it.testId == test.id }

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = test.subject,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Timer: ${test.durationMinutes} Mins",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(test.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    
                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (alreadySolvedResult != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Completed (Score: ${alreadySolvedResult.score}/${alreadySolvedResult.totalMarks})",
                                                color = Color(0xFF1E9028),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${alreadySolvedResult.percentage.toInt()}% Marks",
                                                color = Color(0xFF1E9028),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.navigateTo(Screen.SolveTest(test.id)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Start MCQ Test with Timer", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "RESULTS" -> {
                if (results.isEmpty()) {
                    EmptyBoxPlaceholders(msg = "Test outcomes are awaiting submission. Start an MCQ first.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(results) { res ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(res.testTitle, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Text(res.date, fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Marks Scored", fontSize = 11.sp, color = Color.Gray)
                                            Text("${res.score} / ${res.totalMarks}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Percentage Score", fontSize = 11.sp, color = Color.Gray)
                                            Text("${res.percentage.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Performance Range", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                text = when {
                                                    res.percentage >= 90 -> "Outstanding!"
                                                    res.percentage >= 75 -> "First Class"
                                                    res.percentage >= 50 -> "Passed"
                                                    else -> "Awaiting Improvement"
                                                },
                                                fontWeight = FontWeight.Bold,
                                                color = if (res.percentage >= 75) Color(0xFF1E9028) else Color(0xFFFF9F1C),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "LEADERBOARD" -> {
                Column {
                    Text(
                        text = "🏆 Trigonometry Class Rankings",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (leaderboardResults.isEmpty()) {
                        EmptyBoxPlaceholders(msg = "Leaderboard calculations is processing.")
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                leaderboardResults.distinctBy { it.studentId }.forEachIndexed { index, lead ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Rank indicators
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (index) {
                                                            0 -> Color(0xFFFFD700) // Gold
                                                            1 -> Color(0xFFC0C0C0) // Silver
                                                            2 -> Color(0xFFCD7F32) // Bronze
                                                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (index < 3) Color.Black else MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = lead.studentName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            text = "${lead.score}/${lead.totalMarks} (${lead.percentage.toInt()}%)",
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (index < leaderboardResults.size - 1) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// SOLVE MCQ INTERACTIVE EXAM WITH TIMELINE COUNTER
@Composable
fun SolveTestScreen(viewModel: SchoolViewModel, testId: Int) {
    val tests by viewModel.allTests.collectAsStateWithLifecycle()
    val test = tests.firstOrNull { it.id == testId } ?: return

    val questions = remember(test) { parseQuestionsJson(test.questionsJson) }
    
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val selectedAnswers = remember { mutableStateListOf<Int>().apply { addAll(List(questions.size) { -1 }) } }

    var timeRemainingSeconds by remember { mutableStateOf(test.durationMinutes * 60) }

    val scope = rememberCoroutineScope()

    // Countdown timer coroutine loop
    LaunchedEffect(key1 = testId) {
        while (timeRemainingSeconds > 0) {
            delay(1000)
            timeRemainingSeconds--
        }
        // Force submit when time ends!
        var finalScore = 0
        questions.forEachIndexed { qIdx, q ->
            if (selectedAnswers[qIdx] == q.correctOptionIndex) {
                finalScore++
            }
        }
        viewModel.recordOnlineTestResult(test.id, test.title, finalScore, questions.size)
    }

    val minutesLeft = timeRemainingSeconds / 60
    val secondsLeft = timeRemainingSeconds % 60

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Top Countdown Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = test.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (questions.isEmpty()) {
                Text("Error reading questions")
            } else {
                val q = questions[currentQuestionIndex]

                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = q.questionText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selectable Answers Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    q.options.forEachIndexed { optionIdx, option ->
                        val isSelected = selectedAnswers[currentQuestionIndex] == optionIdx
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAnswers[currentQuestionIndex] = optionIdx },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else Color.Gray.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when(optionIdx) { 
                                            0 -> "A" 
                                            1 -> "B" 
                                            2 -> "C" 
                                            else -> "D" 
                                        },
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(option, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Nav
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Previous")
                    }

                    if (currentQuestionIndex < questions.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = {
                                var finalScore = 0
                                questions.forEachIndexed { qIdx, question ->
                                    if (selectedAnswers[qIdx] == question.correctOptionIndex) {
                                        finalScore++
                                    }
                                }
                                viewModel.recordOnlineTestResult(test.id, test.title, finalScore, questions.size)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E9028))
                        ) {
                            Text("Finish Exam")
                        }
                    }
                }
            }
        }
    }
}

// AI SERVICE HUB INTEGRATION CORES
@Composable
fun StudentAIHubTab(viewModel: SchoolViewModel) {
    var subTabSelection by remember { mutableStateOf("HOMEWORK") } // HOMEWORK, DOUBT, SUMMARIZER, CHAT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .padding(4.dp)
        ) {
            val sections = listOf(
                "HOMEWORK" to "AI Helper",
                "DOUBT" to "Doubt Solver",
                "CHAT" to "Hira-Chatbot"
            )
            sections.forEach { (secCode, secName) ->
                val isSelected = subTabSelection == secCode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { subTabSelection = secCode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = secName,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (subTabSelection) {
            "HOMEWORK" -> {
                var queryText by remember { mutableStateOf("") }
                val loading by viewModel.aiHomeworkLoading.collectAsStateWithLifecycle()
                val response by viewModel.aiHomeworkResponse.collectAsStateWithLifecycle()

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Explain Complex Topics Instantly", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = { Text("Describe what you want explained e.g. 'Explain Newton 3rd Law'") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.callAIHomeworkHelper(queryText) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = queryText.isNotEmpty() && !loading,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (loading) "Contacting Gemini..." else "Ask AI Homework Helper")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AI Explanation Response:", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (loading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            } else if (response != null) {
                                Text(response!!, fontSize = 14.sp, lineHeight = 20.sp)
                            } else {
                                Text("Ask a query above to see dynamic answers powered by Gemini.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            "DOUBT" -> {
                var subjectInput by remember { mutableStateOf("Mathematics") }
                var doubtQueryText by remember { mutableStateOf("") }
                
                val loading by viewModel.aiDoubtLoading.collectAsStateWithLifecycle()
                val response by viewModel.aiDoubtResponse.collectAsStateWithLifecycle()

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Select Subject for Doubt Solve", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    val listSubjects = listOf("Mathematics", "Physics", "Chemistry", "English")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listSubjects) { sub ->
                            val isSelected = subjectInput == sub
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                                    .clickable { subjectInput = sub }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(sub, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = doubtQueryText,
                        onValueChange = { doubtQueryText = it },
                        placeholder = { Text("Write your step math equation/doubt statement...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.callAIDoubtSolver(subjectInput, doubtQueryText) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = doubtQueryText.isNotEmpty() && !loading,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (loading) "Solving doubt on Gemini..." else "Request Step-by-Step Solve")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Doubt Resolution Solve:", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (loading) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            } else if (response != null) {
                                Text(response!!, fontSize = 14.sp, lineHeight = 20.sp)
                            } else {
                                Text("Select a subject and type your doubt details to request equations from AI.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            "CHAT" -> {
                val chatLog by viewModel.aiChatHistory.collectAsStateWithLifecycle()
                var chatInput by remember { mutableStateOf("") }
                val botLoading by viewModel.aiChatLoading.collectAsStateWithLifecycle()

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(8.dp)
                    ) {
                        if (chatLog.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                    Text("Say Hello to Hira School AI Bot!", fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(chatLog) { chatMsg ->
                                    val isBot = chatMsg.second
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        contentAlignment = if (isBot) Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = if (isBot) 0.dp else 12.dp,
                                                        bottomEnd = if (isBot) 12.dp else 0.dp
                                                    )
                                                )
                                                .background(
                                                    if (isBot) MaterialTheme.colorScheme.secondaryContainer
                                                    else MaterialTheme.colorScheme.primary
                                                )
                                                .padding(12.dp)
                                                .widthIn(max = 260.dp)
                                        ) {
                                            Text(
                                                text = chatMsg.first,
                                                color = if (isBot) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary,
                                                fontSize = 13.sp,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Ask syllabus details, formulas...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotEmpty()) {
                                    viewModel.callAIChatbot(chatInput)
                                    chatInput = ""
                                }
                            },
                            enabled = chatInput.isNotEmpty() && !botLoading,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
                        }
                    }
                }
            }
        }
    }
}

// GALLERY TAB MODULE
@Composable
fun StudentGalleryTab(viewModel: SchoolViewModel) {
    val items by viewModel.allGalleryItems.collectAsStateWithLifecycle()

    var activeAlbumFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hirawati school Gallery", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal filter tags
        val filtersList = listOf("All", "Annual Function", "Sports Day", "Farewell", "Trips")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtersList) { tag ->
                val isSelected = activeAlbumFilter == tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                        .clickable { activeAlbumFilter = tag }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(tag, color = if (isSelected) Color.White else Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredItems = if (activeAlbumFilter == "All") items 
                             else items.filter { it.albumName.equals(activeAlbumFilter, ignoreCase = true) }

        if (filteredItems.isEmpty()) {
            EmptyBoxPlaceholders(msg = "Album contains no photographs currently.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { photo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(Color.LightGray)
                            ) {
                                AsyncImage(
                                    model = photo.imageUrl,
                                    contentDescription = photo.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(photo.albumName, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = photo.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TEACHER DASHBOARD SCREEN
@Composable
fun TeacherDashboard(viewModel: SchoolViewModel) {
    val teacher by viewModel.currentUser.collectAsStateWithLifecycle()
    val notices by viewModel.allNotices.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()

    var activeSubActionTab by remember { mutableStateOf("ATTENDANCE") } // ATTENDANCE, HOMEWORK, STUDY, TEST, NOTICES

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Teacher Panel Workspace",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${teacher?.name ?: "Educator"} • Dept: ${teacher?.subject ?: "Academic"}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                val menuItems = listOf(
                    Triple("ATTENDANCE", "Register", Icons.Default.HowToReg),
                    Triple("HOMEWORK", "Add HW", Icons.Default.PostAdd),
                    Triple("STUDY", "Upload Notes", Icons.Default.CloudUpload),
                    Triple("NOTICES", "Notices", Icons.Default.Campaign),
                    Triple("TEST", "Create Test", Icons.Default.AddBox)
                )

                menuItems.forEach { (tabCode, tabName, tabIcon) ->
                    val isSelected = activeSubActionTab == tabCode
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeSubActionTab = tabCode },
                        icon = { Icon(tabIcon, contentDescription = null) },
                        label = { Text(tabName, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (activeSubActionTab) {
                "ATTENDANCE" -> {
                    var selectedDateInput by remember { mutableStateOf("2026-05-26") }
                    val currentAttendanceStates = remember { mutableStateMapOf<String, String>() }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Mark Students Attendance Register", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = selectedDateInput,
                            onValueChange = { selectedDateInput = it },
                            label = { Text("Selected Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (students.isEmpty()) {
                            EmptyBoxPlaceholders(msg = "Student list is loading.")
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(students) { stu ->
                                    val currentStatusValue = currentAttendanceStates[stu.id] ?: "Present"
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(stu.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("ID: ${stu.id} • Class: ${stu.classLevel}", fontSize = 11.sp, color = Color.Gray)
                                            }

                                            Row {
                                                Button(
                                                    onClick = { 
                                                        currentAttendanceStates[stu.id] = "Present"
                                                        viewModel.markStudentAttendance(stu.id, selectedDateInput, "Present")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (currentStatusValue == "Present") Color(0xFF1E9028) else Color.Gray.copy(alpha = 0.15f)
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text("Present", fontSize = 11.sp, color = if (currentStatusValue == "Present") Color.White else Color.Black)
                                                }

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Button(
                                                    onClick = { 
                                                        currentAttendanceStates[stu.id] = "Absent"
                                                        viewModel.markStudentAttendance(stu.id, selectedDateInput, "Absent")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (currentStatusValue == "Absent") Color(0xFFF24A1A) else Color.Gray.copy(alpha = 0.15f)
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text("Absent", fontSize = 11.sp, color = if (currentStatusValue == "Absent") Color.White else Color.Black)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "HOMEWORK" -> {
                    var hwTitle by remember { mutableStateOf("") }
                    var hwDesc by remember { mutableStateOf("") }
                    var hwSubject by remember { mutableStateOf(teacher?.subject ?: "Mathematics") }
                    var hwDeadline by remember { mutableStateOf("2026-06-03") }
                    var hwClass by remember { mutableStateOf("10th A") }

                    val scope = rememberCoroutineScope()
                    var successBanner by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Publish New Homework Exercise", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(value = hwTitle, onValueChange = { hwTitle = it }, label = { Text("Exercise Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = hwDesc, onValueChange = { hwDesc = it }, label = { Text("Task Instructions & Questions") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = hwSubject, onValueChange = { hwSubject = it }, label = { Text("Subject Area") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = hwClass, onValueChange = { hwClass = it }, label = { Text("Class Level Target") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = hwDeadline, onValueChange = { hwDeadline = it }, label = { Text("Due Date Deadline (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        if (successBanner) {
                            Text("Homework published to Class successfully!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                        }

                        Button(
                            onClick = {
                                if (hwTitle.isNotEmpty() && hwDesc.isNotEmpty()) {
                                    viewModel.submitHomework(hwTitle, hwDesc, hwSubject, hwDeadline, hwClass)
                                    hwTitle = ""
                                    hwDesc = ""
                                    successBanner = true
                                    scope.launch {
                                        delay(3000)
                                        successBanner = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Publish Homework", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "STUDY" -> {
                    var noteTitle by remember { mutableStateOf("") }
                    var noteChapter by remember { mutableStateOf("") }
                    var noteSubject by remember { mutableStateOf(teacher?.subject ?: "Mathematics") }
                    var noteCategory by remember { mutableStateOf("PDF Notes") } // PDF Notes, Important Questions, Video Lecture
                    var noteContent by remember { mutableStateOf("") }

                    var successMsg by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Upload Chapter study Notes", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, label = { Text("Article / Notes Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = noteChapter, onValueChange = { noteChapter = it }, label = { Text("Chapter Title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = noteSubject, onValueChange = { noteSubject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Dropdowns Simulator
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val catOptions = listOf("PDF Notes", "Important Questions", "Video Lecture")
                            catOptions.forEach { opt ->
                                val isSelected = noteCategory == opt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                                        .clickable { noteCategory = opt }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(opt, color = if (isSelected) Color.White else Color.Black, fontSize = 10.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            label = { Text("Detailed Notes Content Text / Video URL") },
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (successMsg) {
                            Text("Revision chapter notes added to DB successfully!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                                    viewModel.submitStudyMaterial(noteTitle, noteSubject, noteCategory, noteChapter, noteContent, "revision paper notes")
                                    noteTitle = ""
                                    noteChapter = ""
                                    noteContent = ""
                                    successMsg = true
                                    scope.launch {
                                        delay(3000)
                                        successMsg = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Upload Study Material", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "NOTICES" -> {
                    var noticeTitleInput by remember { mutableStateOf("") }
                    var noticeContentInput by remember { mutableStateOf("") }
                    var noticeSuccess by remember { mutableStateOf(false) }

                    val scope = rememberCoroutineScope()

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Announce Class Notice Board Alert", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(value = noticeTitleInput, onValueChange = { noticeTitleInput = it }, label = { Text("Announcement Heading") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = noticeContentInput, onValueChange = { noticeContentInput = it }, label = { Text("Details & Warning body message") }, modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(10.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        if (noticeSuccess) {
                            Text("Notice published live successfully!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                        }

                        Button(
                            onClick = {
                                if (noticeTitleInput.isNotEmpty() && noticeContentInput.isNotEmpty()) {
                                    viewModel.publishNotice(noticeTitleInput, noticeContentInput, "2026-05-26")
                                    noticeTitleInput = ""
                                    noticeContentInput = ""
                                    noticeSuccess = true
                                    scope.launch {
                                        delay(3000)
                                        noticeSuccess = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Publish Notice", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "TEST" -> {
                    var tTitle by remember { mutableStateOf("") }
                    var tSubject by remember { mutableStateOf(teacher?.subject ?: "Mathematics") }
                    var tClass by remember { mutableStateOf("10th A") }

                    // Custom question constructor
                    var qText by remember { mutableStateOf("") }
                    var qOptA by remember { mutableStateOf("") }
                    var qOptB by remember { mutableStateOf("") }
                    var qOptC by remember { mutableStateOf("") }
                    var qOptD by remember { mutableStateOf("") }
                    var qCorrectIndex by remember { mutableStateOf(0) }

                    val draftQuestions = remember { mutableStateListOf<Question>() }
                    var testSuccessMsg by remember { mutableStateOf(false) }

                    val scope = rememberCoroutineScope()

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Construct Interactive MCQ Test", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = tTitle, onValueChange = { tTitle = it }, label = { Text("Test Board Heading (e.g. Algebra Quiz)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Add Questions to Draft (Current size: ${draftQuestions.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("Question Statement") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = qOptA, onValueChange = { qOptA = it }, label = { Text("A") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = qOptB, onValueChange = { qOptB = it }, label = { Text("B") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = qOptC, onValueChange = { qOptC = it }, label = { Text("C") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = qOptD, onValueChange = { qOptD = it }, label = { Text("D") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Correct Answer Index:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("0" to "A", "1" to "B", "2" to "C", "3" to "D").forEach { (idxStr, label) ->
                                val isSelected = qCorrectIndex == idxStr.toInt()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f))
                                        .clickable { qCorrectIndex = idxStr.toInt() }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (qText.isNotEmpty() && qOptA.isNotEmpty() && qOptB.isNotEmpty()) {
                                    draftQuestions.add(Question(
                                        questionText = qText,
                                        options = listOf(qOptA, qOptB, qOptC, qOptD),
                                        correctOptionIndex = qCorrectIndex
                                    ))
                                    // Reset single question inputs
                                    qText = ""
                                    qOptA = ""
                                    qOptB = ""
                                    qOptC = ""
                                    qOptD = ""
                                    qCorrectIndex = 0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Question to Draft")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (testSuccessMsg) {
                            Text("MCQ Exam created in DB successfully!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                        }

                        Button(
                            onClick = {
                                if (tTitle.isNotEmpty() && draftQuestions.isNotEmpty()) {
                                    viewModel.createOnlineTest(tTitle, tSubject, tClass, 10, draftQuestions.toList())
                                    tTitle = ""
                                    draftQuestions.clear()
                                    testSuccessMsg = true
                                    scope.launch {
                                        delay(3000)
                                        testSuccessMsg = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = draftQuestions.isNotEmpty() && tTitle.isNotEmpty()
                        ) {
                            Text("Publish Final Exam (${draftQuestions.size} Qs)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ADMIN DASHBOARD SCREEN (Secure Access restricted with password: anjali301)
@Composable
fun AdminDashboard(viewModel: SchoolViewModel) {
    var activeAdminTab by remember { mutableStateOf("STUDENTS") } // STUDENTS, TEACHERS, NEW_ACCOUNT, SYSTEM_NOTICES, GALLERY_ADD

    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val teachers by viewModel.allTeachers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF3B0F03)) // Luxury Crimson/Amber
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Central Administration",
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "School Hirawati Senior Sec.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                val menuItems = listOf(
                    Triple("STUDENTS", "Students", Icons.Default.SupervisedUserCircle),
                    Triple("TEACHERS", "Teachers", Icons.Default.CoPresent),
                    Triple("NEW_ACCOUNT", "Create Account", Icons.Default.PersonAdd),
                    Triple("GALLERY_ADD", "Post Photo", Icons.Default.AddToPhotos)
                )

                menuItems.forEach { (tabCode, tabName, tabIcon) ->
                    val isSelected = activeAdminTab == tabCode
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeAdminTab = tabCode },
                        icon = { Icon(tabIcon, contentDescription = null) },
                        label = { Text(tabName, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (activeAdminTab) {
                "STUDENTS" -> {
                    Column {
                        Text("Active Students Registry", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (students.isEmpty()) {
                            EmptyBoxPlaceholders("No students in registry.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(students) { stu ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(stu.name, fontWeight = FontWeight.Bold)
                                                Text("ID: ${stu.id} • Class: ${stu.classLevel} • Roll: ${stu.rollNo}", fontSize = 11.sp, color = Color.Gray)
                                                Text("Password: ${stu.password}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            IconButton(onClick = { viewModel.deleteUser(stu) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove User", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "TEACHERS" -> {
                    Column {
                        Text("Active Educators Register", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        if (teachers.isEmpty()) {
                            EmptyBoxPlaceholders("No teachers registered.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(teachers) { te ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(te.name, fontWeight = FontWeight.Bold)
                                                Text("ID: ${te.id} • Subject: ${te.subject}", fontSize = 11.sp, color = Color.Gray)
                                                Text("Password: ${te.password}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            IconButton(onClick = { viewModel.deleteUser(te) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove User", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "NEW_ACCOUNT" -> {
                    var selectedRoleOption by remember { mutableStateOf("STUDENT") } // STUDENT, TEACHER
                    var newId by remember { mutableStateOf("") }
                    var newName by remember { mutableStateOf("") }
                    var newPassword by remember { mutableStateOf("") }
                    // Student specific
                    var newClassLevel by remember { mutableStateOf("10th A") }
                    var newRollNo by remember { mutableStateOf("1") }
                    // Teacher specific
                    var newTeacherSubject by remember { mutableStateOf("") }

                    var successCreated by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Create Student or Teacher Portal", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Switch role
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("STUDENT" to "Create Student", "TEACHER" to "Create Teacher").forEach { (code, label) ->
                                val isSelected = selectedRoleOption == code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f))
                                        .clickable { selectedRoleOption = code }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(value = newId, onValueChange = { newId = it }, label = { Text("Logon ID (e.g., S105, T205)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Logon Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        
                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedRoleOption == "STUDENT") {
                            OutlinedTextField(value = newClassLevel, onValueChange = { newClassLevel = it }, label = { Text("Assign Class Room (e.g. 10th A)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = newRollNo, onValueChange = { newRollNo = it }, label = { Text("Roll Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(10.dp))
                        } else {
                            OutlinedTextField(value = newTeacherSubject, onValueChange = { newTeacherSubject = it }, label = { Text("Course Expertise Subject") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (successCreated) {
                            Text("Database updated with login account!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        }

                        Button(
                            onClick = {
                                if (newId.isNotEmpty() && newName.isNotEmpty() && newPassword.isNotEmpty()) {
                                    val compiledUser = User(
                                        id = newId,
                                        password = newPassword,
                                        role = selectedRoleOption,
                                        name = newName,
                                        classLevel = if (selectedRoleOption == "STUDENT") newClassLevel else "",
                                        rollNo = if (selectedRoleOption == "STUDENT") newRollNo.toIntOrNull() ?: 1 else 0,
                                        subject = if (selectedRoleOption == "TEACHER") newTeacherSubject else ""
                                    )
                                    viewModel.addNewUser(compiledUser)
                                    newId = ""
                                    newName = ""
                                    newPassword = ""
                                    newTeacherSubject = ""
                                    successCreated = true
                                    scope.launch {
                                        delay(3000)
                                        successCreated = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Write Registry to DB", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "GALLERY_ADD" -> {
                    var albumOption by remember { mutableStateOf("Annual Function") } // Colors, Sports Day, farewell
                    var imgUrlInput by remember { mutableStateOf("https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=500") }
                    var photoTitle by remember { mutableStateOf("") }
                    var photoSuccess by remember { mutableStateOf(false) }

                    val scope = rememberCoroutineScope()

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Add New Photograph to Gallery", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Album selector
                        val albums = listOf("Annual Function", "Sports Day", "Farewell", "Trips", "Competitions")
                        Text("Select Target Album Folder:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(albums) { alb ->
                                val isSelected = albumOption == alb
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.15f))
                                        .clickable { albumOption = alb }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(alb, color = if (isSelected) Color.White else Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = photoTitle,
                            onValueChange = { photoTitle = it },
                            label = { Text("Photo Title / Caption") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = imgUrlInput,
                            onValueChange = { imgUrlInput = it },
                            label = { Text("Image URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (photoSuccess) {
                            Text("Event photo published to gallery successfully!", color = Color(0xFF1E9028), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                        }

                        Button(
                            onClick = {
                                if (photoTitle.isNotEmpty()) {
                                    viewModel.addNewGalleryItem(albumOption, imgUrlInput, photoTitle)
                                    photoTitle = ""
                                    photoSuccess = true
                                    scope.launch {
                                        delay(3000)
                                        photoSuccess = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Publish Photo Live", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Global Empty Layout
@Composable
fun EmptyBoxPlaceholders(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
