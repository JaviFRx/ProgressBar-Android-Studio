package com.develou.progressbar_en_android

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener referencias de progress bars
        val linearIndicator: ProgressBar = findViewById(R.id.determinate_linear_indicator)
        val indeterminateSwitch: ProgressBar = findViewById(R.id.indeterminate_to_determinate)
        val bufferProgressBar: ProgressBar = findViewById(R.id.buffer_progressbar)
        val redProgressBar: ProgressBar = findViewById(R.id.red_progressbar)
        val blueProgressBar: ProgressBar = findViewById(R.id.blue_progressbar)

        // Indicar determinado
        scope.launch {
            while (true)
                progress(linearIndicator)
        }

        // De indeterminado a determinado
        scope.launch {
            while (true)
                indeterminateToDeterminate(indeterminateSwitch)
        }

        // Buffer
        scope.launch {
            while (true) {
                val async1 = scope.async {
                    progress(bufferProgressBar)
                }
                val async2 = scope.async {
                    secondaryProgress(bufferProgressBar)
                }
                async1.await()
                async2.await()
            }
        }

        DrawableCompat.setTint(
            blueProgressBar.indeterminateDrawable,
            ContextCompat.getColor(this, R.color.blue)
        )
        DrawableCompat.setTint(
            redProgressBar.indeterminateDrawable,
            ContextCompat.getColor(this, R.color.red)
        )
    }

    private suspend fun progress(progressBar: ProgressBar) {
        while (progressBar.progress < progressBar.max) {
            delay(300)
            progressBar.incrementProgressBy(PROGRESS_INCREMENT)
        }
        progressBar.progress = 0
        progressBar.secondaryProgress = 0
    }

    private suspend fun indeterminateToDeterminate(
        progressBar: ProgressBar
    ) {
        delay(2000)
        progressBar.isIndeterminate = false
        progress(progressBar)
        progressBar.isIndeterminate = true
    }

    private suspend fun secondaryProgress(progressBar: ProgressBar) {
        while (progressBar.secondaryProgress < progressBar.max) {
            delay(150)
            progressBar.incrementSecondaryProgressBy(PROGRESS_INCREMENT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Destruimos el alcance de la corrutina
    }

    companion object {
        const val PROGRESS_INCREMENT = 5
    }
}