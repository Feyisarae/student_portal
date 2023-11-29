package com.example.studentportal.view.checks

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import com.example.studentportal.R
import com.example.studentportal.roomDataBase.admin.AdminRepository
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class SignupCheck {
    companion object {
        fun isPhoneNumberValid(
            context: Context,
            inputLayout: TextInputLayout,
            editText: EditText
        ): Boolean {
            val phoneNumber = editText.text.toString().trim()

            return if (phoneNumber.isEmpty()) {
                inputLayout.error = context.getString(R.string.enter_phone_no)
                false
            } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                inputLayout.error = context.getString(R.string.invalid_phone_number)
                false
            } else {
                inputLayout.error = null
                true
            }
        }

        fun isPasswordEqual(
            context: Context,
            inputLayout: TextInputLayout,
            editText: EditText,
            confirmEditText: EditText?
        ): Boolean {
            val password = editText.text.toString().trim()
            val confirmPassword = confirmEditText!!.text.toString().trim()

            return if (password.isEmpty()) {
                inputLayout.error = context.getString(R.string.enter_password)
                false
            } else if (password != confirmPassword) {
                inputLayout.error = context.getString(R.string.passwords_do_not_match)
                false
            } else {
                inputLayout.error = null
                true
            }
        }

        suspend fun isUsernameTaken(
            context: Context,
            inputLayout: TextInputLayout,
            editText: EditText,
            adminRepository: AdminRepository
        ): Boolean {
            val username = editText.text.toString().lowercase().trim()

            return if (username.isEmpty()) {
                inputLayout.error = context.getString(R.string.enter_username)
                false
            } else {
                // Perform the database operation using a coroutine in the IO dispatcher
                val isTaken = withContext(Dispatchers.IO) {
                    adminRepository.isUsernameTaken(username)
                }

                if (isTaken) {
                    inputLayout.error = context.getString(R.string.username_taken)
                    false
                } else {
                    inputLayout.error = null
                    true
                }
            }
        }

        fun areAllFieldsFilled(vararg editTexts: EditText): Boolean {
            for (editText in editTexts) {
                if (editText.text.toString().trim().isEmpty()) {
                    return false
                }
            }
            return true
        }

        fun isRequired(
            context: Context,
            layout: TextInputLayout,
            edittext: EditText,
            fieldNameResource: Int? = null,
        ): Boolean {
            val value = edittext.text.toString().trim()
            val fieldName: String = if (fieldNameResource == null) {
                if (edittext.hint != null) {
                    if (edittext.hint.toString().length <= 12) {
                        edittext.hint.toString()
                    } else {
                        "This field"
                    }
                } else {
                    "This field"
                }
            } else {
                context.getString(fieldNameResource)
            }
            layout.isErrorEnabled = true
            if (value.isEmpty()) {
                layout.error = String.format(
                    context.getString(R.string.is_required_error),
                    fieldName
                )
                return false
            }

            layout.isErrorEnabled = false
            return true
        }

        fun isName(
            context: Context,
            layout: TextInputLayout,
            editText: EditText,
            title: String,
        ): Boolean {
            val name = editText.text.toString().trim()
            layout.isErrorEnabled = true

            if (TextUtils.isEmpty(name) && title != context.getString(R.string.middle_name)) {
                layout.error =
                    String.format(context.getString(R.string.name_empty_error), title)
                return false
            }
            if (name.length < 2) {
                layout.error =
                    String.format(context.getString(R.string.enter_minimum_of_x_chars))
                return false
            }

            val regex3 = "[a-zA-Z]+(?:(?:\\. |[' ])[a-zA-Z]+)*" // allows for apostrophe
            if (!Pattern.compile(regex3)
                    .matcher(name).matches()
            ) {
                layout.error = String.format(
                    context.getString(R.string.invalid_format_error),
                    title
                )
                return false
            }
            layout.isErrorEnabled = false
            return true
        }

        fun isErrorInFields(formErrorMap: MutableMap<String, Boolean>): Boolean {
            for (fieldId in formErrorMap.keys) {
                if (formErrorMap[fieldId] == true) {
                    return true
                }
            }
            return false
        }
    }
}