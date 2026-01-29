package com.example.whizai

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
fun SignupScreen(navController: NavController) {
    val viewModel: SignupViewModel = viewModel()
    val signUpState by viewModel.signupState.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isFormValid = fullName.isNotBlank() && phoneNumber.length == 10 && isEmailValid
    val isOtpValid = otp.length == 6

    var resendTimer by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }

    fun startTimer() {
        resendTimer = 30
        isTimerRunning = true
    }

    LaunchedEffect(signUpState) {
        when (val state = signUpState) {
            is SignupState.CodeSent -> {
                verificationId = state.verificationId
                showBottomSheet = true
                startTimer()
            }
            is SignupState.Success -> {
                showBottomSheet = false // Hide sheet on success
                Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.MainScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }
            is SignupState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {
                // Handle Idle and Loading states if needed.
            }
        }
    }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (resendTimer > 0) {
                delay(1000L)
                resendTimer--
            }
            isTimerRunning = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 24.dp)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(120.dp))
                Text("Join WhizAI Today!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Create your account to get started.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = email.isNotBlank() && !isEmailValid
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                    label = { Text("10-Digit Phone Number") },
                    leadingIcon = { Text("+91") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = isFormValid && signUpState !is SignupState.Loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (signUpState is SignupState.Loading && !showBottomSheet) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text("Already have an account?")
                    TextButton(onClick = { navController.navigate("login_screen") }) {
                        Text("Log In", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.signup_screen_one),
                contentDescription = "Sign Up Illustration",
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.9f)
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    OtpInputSheet(
                        phoneNumber = phoneNumber,
                        otp = otp,
                        // CORRECTION: Use a named parameter to avoid ambiguity.
                        onOtpChange = { newOtpValue -> if (newOtpValue.length <= 6) otp = newOtpValue },
                        isOtpValid = isOtpValid,
                        timer = resendTimer,
                        isTimerRunning = isTimerRunning,
                        isLoading = signUpState is SignupState.Loading,
                        onResendClick = {
                            if (activity != null) {
                                viewModel.sendOtp(phoneNumber, activity)
                            }
                        },
                        onVerifyClick = {
                            verificationId?.let { verId ->
                                viewModel.verifyOtp(verId, otp)
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


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
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
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
                Text("Verify & Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

