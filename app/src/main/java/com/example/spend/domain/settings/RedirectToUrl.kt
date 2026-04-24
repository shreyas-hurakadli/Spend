package com.example.spend.domain.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RedirectToUrl @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(url: String): Boolean =
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.data = url.toUri()
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
}