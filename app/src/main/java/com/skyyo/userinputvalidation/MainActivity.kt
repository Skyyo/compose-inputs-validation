package com.skyyo.userinputvalidation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.skyyo.userinputvalidation.inputValidations.auto.InputValidationAutoScreen
import com.skyyo.userinputvalidation.inputValidations.autoDebounce.InputValidationAutoDebounceScreen
import com.skyyo.userinputvalidation.inputValidations.manual.InputValidationManualScreen
import com.skyyo.userinputvalidation.ui.theme.UserinputvalidationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserinputvalidationTheme {
                Surface(color = MaterialTheme.colors.background) {
//                    InputValidationAutoScreen()
                    InputValidationAutoDebounceScreen()
//                    InputValidationManualScreen()
                }
            }
        }
    }
}
