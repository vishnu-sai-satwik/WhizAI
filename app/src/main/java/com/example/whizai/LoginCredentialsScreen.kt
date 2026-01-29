package com.example.whizai

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginCredentialsScreen(navController: NavController) {
    // VM CONNECT: Get an instance of our LoginViewModel.
    val viewModel: LoginViewModel = viewModel()
    // VM CONNECT: Observe the loginState from the ViewModel.
    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    // State for the Material 3 bottom sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    // State holders for the UI inputs
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }

    // Validation logic
    val isPhoneValid = phoneNumber.length == 10
    val isOtpValid = otp.length == 6

    // Resend OTP timer state
    var resendTimer by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // VM CONNECT: A LaunchedEffect to react to changes in loginState.
    fun startTimer() {
        resendTimer = 30
        isTimerRunning = true
    }
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.CodeSent -> {
                verificationId = state.verificationId
                showBottomSheet = true
                startTimer()
            }
            is LoginState.Success -> {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.MainScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }
            is LoginState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {
                // Handle Idle and Loading states if needed.
            }
        }
    }

    // Effect to run the countdown timer
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (resendTimer > 0) {
                delay(1000L)
                resendTimer--
            }
            isTimerRunning = false
        }
    }




    Scaffold (containerColor = Color(0xFFF5F7FA)) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 24.dp)
            )
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(120.dp))
                Text(
                    text = "Welcome Back!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enter your phone number to continue",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {  if (it.length <= 10) phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("10-Digit Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Text("+91", fontWeight = FontWeight.Bold) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (activity != null) {
                            viewModel.sendOtp(phoneNumber, activity)
                        } else {
                            Toast.makeText(context, "An internal error occurred.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = RoundedCornerShape(10.dp),
                    enabled = isPhoneValid && loginState !is LoginState.Loading
                ) {
                    if(loginState is LoginState.Loading && !showBottomSheet) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(text = "Continue", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account?")
                    Spacer(modifier = Modifier.width(2.dp))
                    // IMPROVEMENT: Use the Screen object for type-safe navigation
                    TextButton(onClick = { navController.navigate(Screen.SignupScreen.route) }) { Text("Sign Up", fontWeight = FontWeight.Bold) }
                }

                // NOTE: Make sure the 'intro_screen_pic_one' drawable exists in your res/drawable folder.
                Image(
                    painter = painterResource(R.drawable.intro_screen_pic_one),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 20.dp)
                )
            }

            // --- Bottom Sheet for OTP ---
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    // We can reuse the same OtpInputSheet composable
                    OtpInputSheet(
                        phoneNumber = phoneNumber,
                        otp = otp,
                        onOtpChange = { if (it.length <= 6) otp = it },
                        isOtpValid = isOtpValid,
                        timer = resendTimer,
                        isTimerRunning = isTimerRunning,
                        isLoading = loginState is LoginState.Loading,
                        onResendClick = {
                            if (activity != null) {
                                viewModel.sendOtp(phoneNumber, activity)
                            }
                        },
                        onVerifyClick = {
                            verificationId?.let { verId ->
                                viewModel.verifyOtp(verId, otp)
                            }
                        }
                    )
                }
            }
        }
    }
}

// CORRECTION: Added the missing OtpInputSheet composable function.
@Composable
fun OtpInputSheet(
    phoneNumber: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    isOtpValid: Boolean,
    timer: Int,
    isTimerRunning: Boolean,
    isLoading: Boolean,
    onResendClick: () -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Verification Code", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Sent to +91 $phoneNumber", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = onOtpChange,
            label = { Text("6-Digit OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onResendClick,
                enabled = !isTimerRunning && !isLoading
            ) {
                Text(if (isTimerRunning) "Resend in $timer s" else "Resend OTP")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onVerifyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isOtpValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Verify & Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

