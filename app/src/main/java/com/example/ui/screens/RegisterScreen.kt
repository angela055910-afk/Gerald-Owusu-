package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JungleAvatar
import com.example.ui.theme.EncryptionGold
import com.example.ui.theme.JungleDarkBackground
import com.example.ui.theme.JungleDarkSurface
import com.example.ui.theme.JunglePrimary
import kotlinx.coroutines.delay

private enum class RegisterStep {
    WELCOME,
    PHONE_INPUT,
    OTP_VERIFICATION,
    RESTORE_BACKUP,
    PROFILE_SETUP,
    INITIALIZING_KEYS
}

data class CountryCode(val name: String, val dialCode: String, val flag: String)

private val CountryList = listOf(
    CountryCode("Ghana", "+233", "🇬🇭"),
    CountryCode("Nigeria", "+234", "🇳🇬"),
    CountryCode("South Africa", "+27", "🇿🇦"),
    CountryCode("Kenya", "+254", "🇰🇪"),
    CountryCode("United States", "+1", "🇺🇸"),
    CountryCode("United Kingdom", "+44", "🇬🇧"),
    CountryCode("Canada", "+1", "🇨🇦"),
    CountryCode("Germany", "+49", "🇩🇪"),
    CountryCode("France", "+33", "🇫🇷"),
    CountryCode("India", "+91", "🇮🇳"),
    CountryCode("Australia", "+61", "🇦🇺"),
    CountryCode("Brazil", "+55", "🇧🇷")
)

@Composable
fun RegisterScreen(
    onCancel: () -> Unit,
    onComplete: (name: String, phone: String, about: String, avatarSeed: Int) -> Unit
) {
    BackHandler {
        onCancel()
    }

    var step by remember { mutableStateOf(RegisterStep.WELCOME) }
    var selectedCountry by remember { mutableStateOf(CountryList[0]) }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("241234567") }
    var otpCode by remember { mutableStateOf("784291") }
    var userName by remember { mutableStateOf("OWUSU") }
    var userHandle by remember { mutableStateOf("owusu_jungle") }
    var hidePhoneNumber by remember { mutableStateOf(true) }
    var userAbout by remember { mutableStateOf("Available on Jungle • Curve25519 E2EE") }
    var avatarSeed by remember { mutableIntStateOf(0) }
    var otpCountdown by remember { mutableIntStateOf(45) }

    LaunchedEffect(step) {
        if (step == RegisterStep.OTP_VERIFICATION) {
            otpCountdown = 45
            while (otpCountdown > 0) {
                delay(1000)
                otpCountdown--
            }
        } else if (step == RegisterStep.INITIALIZING_KEYS) {
            delay(2000)
            val fullPhone = "${selectedCountry.dialCode} ${phoneNumber.ifBlank { "241234567" }}"
            onComplete(userName.ifBlank { "OWUSU" }, fullPhone, userAbout, avatarSeed)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JungleDarkBackground)
            .testTag("register_screen")
    ) {
        // Top Close/Back button
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8696A0))
        }

        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label = "registerStepAnimation",
            modifier = Modifier.fillMaxSize()
        ) { currentStep ->
            when (currentStep) {
                RegisterStep.WELCOME -> {
                    WelcomeStep(
                        onAgreeAndContinue = { step = RegisterStep.PHONE_INPUT }
                    )
                }
                RegisterStep.PHONE_INPUT -> {
                    PhoneInputStep(
                        selectedCountry = selectedCountry,
                        phoneNumber = phoneNumber,
                        countryMenuExpanded = countryMenuExpanded,
                        onCountryMenuToggle = { countryMenuExpanded = it },
                        onCountrySelect = {
                            selectedCountry = it
                            countryMenuExpanded = false
                        },
                        onPhoneChange = { phoneNumber = it },
                        onNext = { step = RegisterStep.OTP_VERIFICATION }
                    )
                }
                RegisterStep.OTP_VERIFICATION -> {
                    OtpVerificationStep(
                        fullPhone = "${selectedCountry.dialCode} ${phoneNumber.ifBlank { "241234567" }}",
                        otpCode = otpCode,
                        otpCountdown = otpCountdown,
                        onOtpChange = { otpCode = it },
                        onCallMe = {
                            otpCode = "784291"
                        },
                        onVerify = { step = RegisterStep.RESTORE_BACKUP }
                    )
                }
                RegisterStep.RESTORE_BACKUP -> {
                    RestoreBackupStep(
                        onRestore = { step = RegisterStep.PROFILE_SETUP },
                        onSkip = { step = RegisterStep.PROFILE_SETUP }
                    )
                }
                RegisterStep.PROFILE_SETUP -> {
                    ProfileSetupStep(
                        name = userName,
                        usernameHandle = userHandle,
                        hidePhone = hidePhoneNumber,
                        about = userAbout,
                        avatarSeed = avatarSeed,
                        onNameChange = { userName = it },
                        onUsernameChange = { userHandle = it },
                        onHidePhoneChange = { hidePhoneNumber = it },
                        onAboutChange = { userAbout = it },
                        onAvatarSeedChange = { avatarSeed = it },
                        onFinish = { step = RegisterStep.INITIALIZING_KEYS }
                    )
                }
                RegisterStep.INITIALIZING_KEYS -> {
                    InitializingKeysStep()
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onAgreeAndContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "Welcome to Jungle",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Brand Hero Visual
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0B2923))
                    .border(2.5.dp, JunglePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = JunglePrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "E2EE SIGNAL PROTOCOL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EncryptionGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Simple. Secure. Reliable messaging and voice/video calling with end-to-end privacy and EchoStream AI.",
                color = Color(0xFF8696A0),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text(
                text = "Read our Privacy Policy. Tap \"Agree and continue\" to accept the Terms of Service.",
                color = Color(0xFF8696A0),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onAgreeAndContinue,
                colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("agree_and_continue_button")
            ) {
                Text(
                    text = "Agree and continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun PhoneInputStep(
    selectedCountry: CountryCode,
    phoneNumber: String,
    countryMenuExpanded: Boolean,
    onCountryMenuToggle: (Boolean) -> Unit,
    onCountrySelect: (CountryCode) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "Enter your phone number",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jungle will verify your phone number via a zero-knowledge SMS OTP.",
                color = Color(0xFF8696A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Country Selector Row
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color(0xFF111B21),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF202C33)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountryMenuToggle(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${selectedCountry.flag}  ${selectedCountry.name}",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Text(
                            text = selectedCountry.dialCode,
                            color = JunglePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                DropdownMenu(
                    expanded = countryMenuExpanded,
                    onDismissRequest = { onCountryMenuToggle(false) },
                    modifier = Modifier.background(Color(0xFF202C33))
                ) {
                    CountryList.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.flag}  ${country.name} (${country.dialCode})", color = Color.White) },
                            onClick = { onCountrySelect(country) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                placeholder = { Text("Phone number", color = Color(0xFF8696A0)) },
                leadingIcon = {
                    Text(
                        text = selectedCountry.dialCode,
                        color = JunglePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_phone_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Carrier SMS charges may apply.",
                color = Color(0xFF667781),
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("phone_next_button")
        ) {
            Text("Next", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun OtpVerificationStep(
    fullPhone: String,
    otpCode: String,
    otpCountdown: Int,
    onOtpChange: (String) -> Unit,
    onCallMe: () -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = "Verifying your number",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Waiting to automatically detect an SMS sent to\n$fullPhone",
                color = Color(0xFF8696A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 6-digit Code input
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) onOtpChange(it) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .testTag("otp_code_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (otpCountdown > 0) "Resend SMS in 0:${if (otpCountdown < 10) "0$otpCountdown" else "$otpCountdown"}" else "Resend SMS",
                    color = if (otpCountdown > 0) Color(0xFF8696A0) else JunglePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(enabled = otpCountdown == 0) { /* resend */ }
                )
                Text(
                    text = "•",
                    color = Color(0xFF8696A0)
                )
                Row(
                    modifier = Modifier
                        .clickable { onCallMe() }
                        .testTag("call_me_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = JunglePrimary, modifier = Modifier.size(15.dp))
                    Text(
                        text = "Call Me",
                        color = JunglePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = onVerify,
            colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("verify_otp_button")
        ) {
            Text("Verify & Continue", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun RestoreBackupStep(
    onRestore: () -> Unit,
    onSkip: () -> Unit
) {
    var isRestoring by remember { mutableStateOf(false) }
    var restoreProgress by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }

    LaunchedEffect(isRestoring) {
        if (isRestoring) {
            while (restoreProgress < 1f) {
                delay(120)
                restoreProgress += 0.2f
            }
            delay(400)
            onRestore()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300A884)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Cloud Backup",
                    tint = JunglePrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Restore backup",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Restore your messages and media from Google Drive storage. If you don't restore now, you won't be able to restore later.",
                color = Color(0xFF8696A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color(0xFF111B21),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF202C33)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Found backup", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("AES-256 Encrypted", color = EncryptionGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Yesterday, 11:45 PM • 24.8 MB", color = Color(0xFF8696A0), fontSize = 12.sp)
                    Text("Account: Google Drive (Backup Vault)", color = JunglePrimary, fontSize = 12.sp)

                    if (isRestoring) {
                        Spacer(modifier = Modifier.height(14.dp))
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { restoreProgress.coerceIn(0f, 1f) },
                            color = JunglePrimary,
                            trackColor = Color(0xFF202C33),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Restoring messages and media… ${(restoreProgress * 100).toInt()}%",
                            color = JunglePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { isRestoring = true },
                enabled = !isRestoring,
                colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("restore_backup_button")
            ) {
                Text("Restore", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            androidx.compose.material3.OutlinedButton(
                onClick = onSkip,
                enabled = !isRestoring,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("skip_restore_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8696A0)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF202C33))
            ) {
                Text("Skip", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun ProfileSetupStep(
    name: String,
    usernameHandle: String,
    hidePhone: Boolean,
    about: String,
    avatarSeed: Int,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onHidePhoneChange: (Boolean) -> Unit,
    onAboutChange: (String) -> Unit,
    onAvatarSeedChange: (Int) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 30.dp)
        ) {
            Text(
                text = "Profile info",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set your name and optional username to hide your phone number.",
                color = Color(0xFF8696A0),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar with camera icon badge
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { onAvatarSeedChange((avatarSeed + 1) % 6) }
            ) {
                JungleAvatar(
                    name = if (name.isNotBlank()) name else "User",
                    size = 90.dp,
                    colorSeed = avatarSeed
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(JunglePrimary)
                        .border(2.dp, JungleDarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change avatar color",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name (e.g. OWUSU)", color = Color(0xFF8696A0)) },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = JunglePrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Username (WhatsApp 2026 feature)
            OutlinedTextField(
                value = usernameHandle,
                onValueChange = onUsernameChange,
                label = { Text("Username Key (Hides phone number)", color = Color(0xFF8696A0)) },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = JunglePrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_username_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = about,
                onValueChange = onAboutChange,
                label = { Text("About / Status", color = Color(0xFF8696A0)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reg_about_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = JunglePrimary,
                    unfocusedBorderColor = Color(0xFF2A3942),
                    focusedContainerColor = Color(0xFF111B21),
                    unfocusedContainerColor = Color(0xFF111B21)
                ),
                singleLine = true
            )
        }

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = JunglePrimary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("complete_reg_button")
        ) {
            Text("Next", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun InitializingKeysStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = JunglePrimary,
            modifier = Modifier.size(54.dp),
            strokeWidth = 3.5.dp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Initializing…",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Generating Curve25519 Double Ratchet cryptographic keys and synchronizing zero-knowledge parameters.",
            color = Color(0xFF8696A0),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
