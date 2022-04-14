package com.app.ia.driver.utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern


class ValidationUtils{
    companion object {
       /* fun isValidEmail(target: CharSequence): Boolean {
            var prefix = false
            Log.d("Email", "${target.indexOf("@", 0, true)}")
            if(target.indexOf("@", 0, true) > 2  || target.indexOf("@", 0, true) <= 65){
                prefix = true
            }

            if(target.indexOf("%", 0, true) > -1){
                prefix = false
            }
            var postfix = false
            Log.d("Email", "${target.indexOf("@", 0, true)}")
            if((target.length - target.indexOf("@", 0, true)) > 4  || (target.length - target.indexOf("@", 0, true)) < 256){
                postfix = true
            }
            return prefix && postfix && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }*/
        fun isValidNumber(target: String) : Boolean{
            return if (target.isNullOrEmpty()) false else target.all { Character.isDigit(it) }

        }

        fun isValidEmailId(email: String?): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{3,4})$"
            ).matcher(email).matches()
        }

        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun isValidPhone(phone: CharSequence): Boolean {
            return phone.length == 10 && Patterns.PHONE.matcher(phone).matches()
        }

        fun isHaveLettersOnly(target: String): Boolean {
            return if (target.isNotEmpty()) {
                val letter = Pattern.compile("[^a-zA-Z\\s]")
                //val letter = Pattern.compile("^[a-zA-z]\\s")
                val digit = Pattern.compile("[0-9]")
                val special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
                //Pattern eight = Pattern.compile (".{8}");
                val hasLetter = letter.matcher(target.trim())
                val hasDigit = digit.matcher(target)
                val hasSpecial = special.matcher(target)
                hasLetter.find()
            } else false
        }

        fun isValidPassword(target: CharSequence): Boolean {
            return if (target.isNotEmpty()) {
                val letter = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE)
                val digit = Pattern.compile("[0-9]")
                val special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
                //Pattern eight = Pattern.compile (".{8}");
                val hasLetter = letter.matcher(target)
                val hasDigit = digit.matcher(target)
                val hasSpecial = special.matcher(target)
                hasDigit.find() && hasLetter.find()
            } else false
        }

        /*fun isValidPassword(password: CharSequence): Boolean {

            val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
            val pattern = Pattern.compile(passwordPattern)
            val matcher = pattern.matcher(password)
            return matcher.matches()
        }*/
    }
}