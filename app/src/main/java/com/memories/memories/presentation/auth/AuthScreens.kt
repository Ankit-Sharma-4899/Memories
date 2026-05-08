package com.memories.memories.presentation.auth

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.memories.memories.R
import com.memories.memories.domain.model.Gender
import com.memories.memories.presentation.components.MemoryScaffold
import java.util.Calendar

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        BrandHeader(title = "Welcome back", subtitle = "Sign in with your username and password")
        AppTextField(
            value = uiState.username,
            onValueChanged = onUsernameChanged,
            label = "Username or email",
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        )
        PasswordField(
            value = uiState.password,
            label = "Password",
            isVisible = uiState.isPasswordVisible,
            onValueChanged = onPasswordChanged,
            onToggleVisibility = onTogglePassword
        )
        TextButton(modifier = Modifier.align(Alignment.End), onClick = onForgotPassword) {
            Text("Forgot password?")
        }
        PrimaryButton(text = "Login", isLoading = uiState.isLoading, onClick = onLogin)
        SwitchAuthRow(
            text = "New to Memories?",
            action = "Create account",
            onClick = onCreateAccount
        )
    }
}

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onMobileChanged: (String) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onDateOfBirthChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        BrandHeader(title = "Create profile", subtitle = "Tell us who you are before the first memory")
        AppTextField(uiState.name, onNameChanged, "Full name") {
            Icon(Icons.Default.Badge, contentDescription = null)
        }
        AppTextField(uiState.username, onUsernameChanged, "Username") {
            Icon(Icons.Default.Person, contentDescription = null)
        }
        AppTextField(
            value = uiState.email,
            onValueChanged = onEmailChanged,
            label = "Email (optional)",
            keyboardType = KeyboardType.Email,
            icon = { Icon(Icons.Default.Email, contentDescription = null) }
        )
        AppTextField(
            value = uiState.mobileNumber,
            onValueChanged = onMobileChanged,
            label = "Mobile number",
            keyboardType = KeyboardType.Phone,
            icon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )
        GenderSelector(selected = uiState.gender, onSelected = onGenderChanged)
        DateOfBirthField(value = uiState.dateOfBirth, onDateSelected = onDateOfBirthChanged)
        PasswordField(
            value = uiState.password,
            label = "Password",
            isVisible = uiState.isPasswordVisible,
            onValueChanged = onPasswordChanged,
            onToggleVisibility = onTogglePassword
        )
        PasswordField(
            value = uiState.confirmPassword,
            label = "Confirm password",
            isVisible = uiState.isPasswordVisible,
            onValueChanged = onConfirmPasswordChanged,
            onToggleVisibility = onTogglePassword
        )
        PrimaryButton(text = "Next", isLoading = uiState.isLoading, onClick = onRegister)
        SwitchAuthRow(text = "Already onboarded?", action = "Login", onClick = onLogin)
    }
}

@Composable
fun OtpVerificationScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onOtpChanged: (String) -> Unit,
    onResendOtp: () -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        BrandHeader(
            title = "Verify account",
            subtitle = "Enter the 6 digit OTP sent to ${uiState.mobileNumber.ifBlank { "your mobile" }}."
        )
        AppTextField(
            value = uiState.otpCode,
            onValueChanged = onOtpChanged,
            label = "OTP",
            keyboardType = KeyboardType.Number,
            icon = { Icon(Icons.Default.Lock, contentDescription = null) }
        )
        TextButton(onClick = onResendOtp) {
            Text("Resend OTP")
        }
        PrimaryButton(
            text = "Verify OTP",
            isLoading = uiState.isLoading,
            onClick = onVerify
        )
    }
}

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onMobileChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        BrandHeader(
            title = "Reset password",
            subtitle = "Verify your mobile number first, then create a new password."
        )
        AppTextField(
            value = uiState.resetMobileNumber,
            onValueChanged = onMobileChanged,
            label = "Registered mobile number",
            keyboardType = KeyboardType.Phone,
            icon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )
        PrimaryButton(text = "Send OTP", isLoading = uiState.isLoading, onClick = onSubmit)
    }
}

@Composable
fun NewPasswordScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        BrandHeader(
            title = "Create new password",
            subtitle = "Choose a strong password for your Memories account."
        )
        PasswordField(
            value = uiState.newPassword,
            label = "New password",
            isVisible = uiState.isPasswordVisible,
            onValueChanged = onPasswordChanged,
            onToggleVisibility = onTogglePassword
        )
        PasswordField(
            value = uiState.confirmNewPassword,
            label = "Confirm new password",
            isVisible = uiState.isPasswordVisible,
            onValueChanged = onConfirmPasswordChanged,
            onToggleVisibility = onTogglePassword
        )
        PrimaryButton(text = "Change password", isLoading = uiState.isLoading, onClick = onSubmit)
    }
}

@Composable
private fun AuthScreenFrame(
    snackbarHostState: SnackbarHostState,
    content: @Composable ColumnScope.(PaddingValues) -> Unit
) {
    MemoryScaffold(snackbarHostState = snackbarHostState) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(22.dp)
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                content(padding)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BrandHeader(title: String, subtitle: String) {
    Image(
        painter = painterResource(id = R.drawable.memories_logo),
        contentDescription = "Memories logo",
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text(
        text = subtitle,
        modifier = Modifier.padding(top = 8.dp, bottom = 22.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateOfBirthField(value: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val picker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected("%02d/%02d/%04d".format(dayOfMonth, month + 1, year))
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        label = { Text("Date of birth") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
        trailingIcon = {
            TextButton(onClick = { picker.show() }) {
                Text("Select")
            }
        }
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        leadingIcon = icon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    value: String,
    label: String,
    isVisible: Boolean,
    onValueChanged: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (isVisible) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderSelector(selected: Gender, onSelected: (Gender) -> Unit) {
    Text(
        text = "Gender",
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Gender.values().forEach { gender ->
            FilterChip(
                selected = selected == gender,
                onClick = { onSelected(gender) },
                label = { Text(gender.label) }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun PrimaryButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = !isLoading,
        onClick = onClick
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text)
        }
    }
}

@Composable
private fun SwitchAuthRow(text: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        TextButton(onClick = onClick) {
            Text(action)
        }
    }
}
