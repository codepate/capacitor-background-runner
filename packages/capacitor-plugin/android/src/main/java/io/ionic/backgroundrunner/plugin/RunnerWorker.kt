package io.ionic.backgroundrunner.plugin;

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class RunnerWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        try {
            val label = this.inputData.getString("label") ?: ""
            val src = this.inputData.getString("src") ?: ""
            val event = this.inputData.getString("event") ?: ""

            if (label.isEmpty() || src.isEmpty() || event.isEmpty()) {
                throw Exception("label is empty")
            }

            val runnerConfigObj = JSONObject()
            runnerConfigObj.put("label", label)
            runnerConfigObj.put("src", src)
            runnerConfigObj.put("event", event)
            runnerConfigObj.put("autoStart", false)
            runnerConfigObj.put("repeats", false)
            runnerConfigObj.put("interval", 0)

            val config = RunnerConfig(runnerConfigObj)

            runBlocking {
                val impl = BackgroundRunner.getInstance(this@RunnerWorker.applicationContext)
                impl.execute(this@RunnerWorker.applicationContext, config, JSONObject())
            }

            return Result.success()
        } catch (ex: Exception) {
            val data = Data.Builder()
                .putString("error", ex.toString())
                .build()

            return Result.failure(data)
        }
    }
}