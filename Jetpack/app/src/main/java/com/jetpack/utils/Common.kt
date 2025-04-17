package com.jetpack.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.R

@Preview(showSystemUi = true)
    @Composable
    fun ProgressBar() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEditField(
    output: MutableState<String>,
    placeholder: String,
    action: ImeAction,
    inputType: KeyboardType,
    onClick: (Boolean) -> Unit,
) {
    OutlinedTextField(modifier = Modifier
        .fillMaxWidth()
        .onFocusChanged { focusState ->
            if (focusState.hasFocus) {
                onClick(true)


            } else {
                onClick(false)
            }
        }, value = output.value,
        onValueChange = {

            output.value = it

        }, shape = RoundedCornerShape(5.dp),
        textStyle = TextStyle(color = colorResource(R.color.text_color)),
        keyboardOptions = KeyboardOptions(keyboardType = inputType, imeAction = action),
        maxLines = 1,
        placeholder = {
            Text(
                text = placeholder,
                color = colorResource(R.color.text_color)
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.Transparent,
            disabledTextColor = Color.Transparent,
            focusedBorderColor = colorResource(R.color.text_color),
            unfocusedBorderColor = colorResource(R.color.text_color),
            cursorColor = colorResource(R.color.text_color)
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPasswordEditField(
    output: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    placeholder: String,
    action: ImeAction,
    inputType: KeyboardType,

    onClick: (Boolean) -> Unit,
) {
    OutlinedTextField(modifier = Modifier
        .fillMaxWidth()

        .onFocusChanged { focusState ->

            if (focusState.hasFocus) {
                onClick(true)


            } else {
                onClick(false)
            }

        }, value = output.value,
        onValueChange = {

            output.value = it

        }, shape = RoundedCornerShape(5.dp),
        textStyle = TextStyle(color = colorResource(R.color.text_color)),
        keyboardOptions = KeyboardOptions(keyboardType = inputType, imeAction = action),
        maxLines = 1,
        placeholder = {
            Text(
                text = placeholder,
                color = colorResource(R.color.text_color)
            )
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.Transparent,
            disabledTextColor = Color.Transparent,
            focusedBorderColor = colorResource(R.color.text_color),
            unfocusedBorderColor = colorResource(R.color.text_color),
            cursorColor = colorResource(R.color.text_color)
            ),
        trailingIcon ={

                Icon(modifier = Modifier.clickable {
                    passwordVisible.value = !passwordVisible.value
                },
                    painter = painterResource(id = if (passwordVisible.value)
                        R.drawable.hide_password
                    else
                        R.drawable.password_show), tint = colorResource(id = R.color.text_color),
                    contentDescription = null
                )
        }
        )
}

@Composable
fun CustomButton(text: String,onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.button_color), shape = CircleShape)
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier

                .padding(horizontal = 30.dp), text = text, style = TextStyle(
                color = colorResource(R.color.text_color), fontWeight = FontWeight.W700,
                fontSize = 16.sp)
            )
    }
}


