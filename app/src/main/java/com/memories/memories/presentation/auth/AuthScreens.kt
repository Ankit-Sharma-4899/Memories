@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.memories.memories.presentation.auth

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
        BrandHeader(title = "Welcome back", subtitle = "Continue capturing your best moments")
        AppTextField(uiState.username, onUsernameChanged, "Username or email") {
            Icon(Icons.Default.Person, contentDescription = null)
        }
        PasswordField(uiState.password, "Password", uiState.isPasswordVisible, onPasswordChanged, onTogglePassword)
        TextButton(modifier = Modifier.align(Alignment.End), onClick = onForgotPassword) {
            Text("Forgot password?")
        }
        PrimaryButton(text = "Login", isLoading = uiState.isLoading, onClick = onLogin)
        SwitchAuthRow("New to Memories?", "Create account", onCreateAccount)
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
        BrandHeader(title = "Create profile", subtitle = "Email verification protects every memory")
        AppTextField(uiState.name, onNameChanged, "Full name") {
            Icon(Icons.Default.Badge, contentDescription = null)
        }
        AppTextField(uiState.username, onUsernameChanged, "Username") {
            Icon(Icons.Default.Person, contentDescription = null)
        }
        AppTextField(uiState.email, onEmailChanged, "Email", KeyboardType.Email) {
            Icon(Icons.Default.Email, contentDescription = null)
        }
        AppTextField(uiState.mobileNumber, onMobileChanged, "Mobile number", KeyboardType.Phone) {
            Icon(Icons.Default.Phone, contentDescription = null)
        }
        GenderSelector(uiState.gender, onGenderChanged)
        DateOfBirthField(uiState.dateOfBirth, onDateOfBirthChanged)
        PasswordField(uiState.password, "Password", uiState.isPasswordVisible, onPasswordChanged, onTogglePassword)
        PasswordField(
            uiState.confirmPassword,
            "Confirm password",
            uiState.isPasswordVisible,
            onConfirmPasswordChanged,
            onTogglePassword
        )
        PrimaryButton(text = "Send verification email", isLoading = uiState.isLoading, onClick = onRegister)
        SwitchAuthRow("Already onboarded?", "Login", onLogin)
    }
}

@Composable
fun EmailVerificationScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onResendEmail: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Check your email",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "We sent a Firebase verification link to ${uiState.currentUser?.email ?: uiState.email}. Open it, then return here and continue.",
            modifier = Modifier.padding(top = 8.dp, bottom = 22.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
        )
        PrimaryButton("I verified my email", uiState.isLoading, onContinue)
        TextButton(onClick = onResendEmail, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Resend email")
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    snackbarHostState: SnackbarHostState,
    onEmailChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    AuthScreenFrame(snackbarHostState = snackbarHostState) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        BrandHeader("Reset password", "Firebase will email a secure reset link")
        AppTextField(uiState.resetEmail, onEmailChanged, "Registered email", KeyboardType.Email) {
            Icon(Icons.Default.Email, contentDescription = null)
        }
        PrimaryButton(text = "Send reset email", isLoading = uiState.isLoading, onClick = onSubmit)
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
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.82f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 520.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                content(padding)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BrandHeader(title: String, subtitle: String) {
    Image(
        painter = painterResource(id = R.drawable.memories_logo),
        contentDescription = "Memories logo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(18.dp))
    )
    Spacer(modifier = Modifier.height(18.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = subtitle,
        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
    )
}

@Composable
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
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

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
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
    Spacer(modifier = Modifier.height(12.dp))
}

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
                label = { Text(gender.label) },
                shape = CircleShape
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
        shape = RoundedCornerShape(14.dp),
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
        Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f))
        TextButton(onClick = onClick) {
            Text(action)
        }
    }
}
