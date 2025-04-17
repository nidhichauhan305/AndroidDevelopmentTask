package com.jetpack.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jetpack.R
import com.jetpack.navigation.Screen
import com.jetpack.room.UserDatabase
import com.jetpack.room.UserLoginInfoClass
import com.jetpack.room.repository.UserRepository
import com.jetpack.room.viewModel.UserViewModel
import com.jetpack.utils.CustomButton
import com.jetpack.utils.SelectEditField
import com.jetpack.utils.SelectPasswordEditField
import com.jetpack.utils.SharedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val type = remember {
        mutableIntStateOf(1)  //1-login,2-signup
    }
    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val db = remember { UserDatabase.getInstance(context) }

    val repository = UserRepository(db.appDao())
    val factory = UserAuthViewModelFactory(repository)
    val userAuthViewModel: UserViewModel = viewModel(factory = factory)
    Scaffold(modifier = Modifier
        .statusBarsPadding()
        .navigationBarsPadding(),
        containerColor = colorResource(R.color.black)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {

            Card(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(all = 20.dp), shape = RoundedCornerShape(5.dp),colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Zenithra", color = Color.LightGray, fontSize = 15.sp)
                    Text(text = "Welcome Back", color = Color.LightGray, fontSize = 20.sp, modifier = Modifier.padding(vertical = 2.dp))
                    Text(text =if(type.value==2){
                        "Please enter your details to sign up"}

                    else{
                            "Please enter your details to sign in"}

                    , color = colorResource(R.color.text_color), fontSize = 13.sp)

                    Box(modifier = Modifier.height(15.dp))
                    SelectEditField(email,"Your email address", ImeAction.Next, KeyboardType.Email, onClick = {

                    })

                    Box(modifier = Modifier.height(10.dp))
                    SelectPasswordEditField(password,passwordVisible,"Password", ImeAction.Done, KeyboardType.Password,
                      onClick = {
                    })

                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp), text =  if(type.value==2){""}else{"Forgot Password?"}, color = colorResource(R.color.light_blue_text), fontSize = 14.sp, textAlign = TextAlign.End, style = TextStyle(textDecoration = TextDecoration.Underline)
                        )
                    CustomButton(text = if(type.value==2){
                        "Sign Up"
                      }
                    else{
                        "Sign In"
                        }
                            , onClick = {
                            if (validate(context,email.value,password.value)){
                                if (type.value==2){
                                    val newUser = UserLoginInfoClass(email=email.value, password = password.value)
                                    userAuthViewModel.signUp(newUser){
                                            result ->
                                        CoroutineScope(Dispatchers.Main).launch {

                                            if (result == LoginResult.Success) {
                                                Toast.makeText(
                                                    context,
                                                    "Signup successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                SharedPrefsManager.setIsLogin(context,true)

                                                navController.navigate(Screen.Manga.route) {
                                                    popUpTo(Screen.Manga.route) { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    }
                                }
                                else{
                                    userAuthViewModel.login(email.value, password.value) { result ->
                                        CoroutineScope(Dispatchers.Main).launch {

                                            when (result) {
                                                 LoginResult.Success -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Login successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                     SharedPrefsManager.setIsLogin(context,true)
                                                     navController.navigate(Screen.Manga.route) {
                                                         popUpTo(Screen.Manga.route) { inclusive = true }
                                                         launchSingleTop = true
                                                     }
                                                }

                                                 LoginResult.IncorrectPassword -> {
                                                    Toast.makeText(
                                                        context,
                                                        "Password incorrect",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                 LoginResult.UserNotFound -> {
                                                    Toast.makeText(
                                                        context,
                                                        "User not found",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }


                                            }
                                        }
                                    }
                                }
                            }


                    })

                    Text(modifier = Modifier.padding(top = 10.dp).clickable {
                        if(type.value==2){
                            type.value = 1
                        }
                        else{
                            type.value = 2
                        }
                        email.value = ""
                        password.value = ""
                    }, text =
                    if(type.value==2){
                        "Already have an account? Sign In"
                   }
                        else{
                        "Don't have an account? Sign Up"
                       }
                        , color = colorResource(R.color.text_color), fontSize = 13.sp)


                }

            }
        }
    }

}

fun validate(context: Context, email: String, password: String): Boolean {
    val isEmailValid = email.isNotEmpty() && email.contains("@")
    val isPasswordValid = password.length >= 6

    return if (isEmailValid && isPasswordValid) {
        true
    } else {
        if (email.isEmpty()){
            Toast.makeText(context, "Please enter email address", Toast.LENGTH_SHORT).show()
        }
        else if (!isEmailValid) {
            Toast.makeText(context, "Please enter valid email address", Toast.LENGTH_SHORT).show()
        } else if (!isPasswordValid) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
        }
        false
    }
}