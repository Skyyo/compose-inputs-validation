package com.skyyo.userinputvalidation.inputValidations.manual

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyyo.userinputvalidation.R
import com.skyyo.userinputvalidation.getStateFlow
import com.skyyo.userinputvalidation.inputValidations.FocusedTextFieldKey
import com.skyyo.userinputvalidation.inputValidations.InputValidator
import com.skyyo.userinputvalidation.inputValidations.InputWrapper
import com.skyyo.userinputvalidation.inputValidations.ScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private class InputErrors(
    val nameErrorId: Int?,
    val cardErrorId: Int?
)

@HiltViewModel
class FormValidationManualViewModel @Inject constructor(private val handle: SavedStateHandle) :
    ViewModel() {

    val name = handle.getStateFlow(viewModelScope, "name", InputWrapper())
    val creditCardNumber = handle.getStateFlow(viewModelScope, "ccNumber", InputWrapper())
    private var focusedTextField = handle.get("focusedTextField") ?: FocusedTextFieldKey.NAME
        set(value) {
            field = value
            handle.set("focusedTextField", value)
        }

    private val _events = Channel<ScreenEvent>()
    val events = _events.receiveAsFlow()

    fun onNameEntered(input: String) {
        name.tryEmit(name.value.copy(value = input, errorId = null))
    }

    fun onCardNumberEntered(input: String) {
        creditCardNumber.tryEmit(creditCardNumber.value.copy(value = input, errorId = null))
    }

    fun onTextFieldFocusChanged(key: FocusedTextFieldKey, isFocused: Boolean) {
        focusedTextField = if (isFocused) key else FocusedTextFieldKey.NONE
    }

    fun onNameImeActionClick() {
        viewModelScope.launch(Dispatchers.Default) {
            _events.send(ScreenEvent.MoveFocus())
        }
    }

    fun onContinueClick() {
        viewModelScope.launch(Dispatchers.Default) {
            when (val inputErrors = getInputErrorsOrNull()) {
                null -> {
                    clearFocusAndHideKeyboard()
                    _events.send(ScreenEvent.ShowToast(R.string.success))
                }
                else -> displayInputErrors(inputErrors)
            }
        }
    }

    private fun getInputErrorsOrNull(): InputErrors? {
        val nameErrorId = InputValidator.getNameErrorIdOrNull(name.value.value)
        val cardErrorId = InputValidator.getCardNumberErrorIdOrNull(creditCardNumber.value.value)
        return if (nameErrorId == null && cardErrorId == null) {
            null
        } else {
            InputErrors(nameErrorId, cardErrorId)
        }
    }

    private suspend fun displayInputErrors(inputErrors: InputErrors) {
        name.emit(name.value.copy(errorId = inputErrors.nameErrorId))
        creditCardNumber.emit(creditCardNumber.value.copy(errorId = inputErrors.cardErrorId))
    }

    private suspend fun clearFocusAndHideKeyboard() {
        _events.send(ScreenEvent.ClearFocus(focusedTextField))
        _events.send(ScreenEvent.UpdateKeyboard(false))
        focusedTextField = FocusedTextFieldKey.NONE
    }
}

