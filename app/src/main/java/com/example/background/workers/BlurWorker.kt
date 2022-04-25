package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx,params) {

    override fun doWork(): Result {
        val appContext = applicationContext
        // ADD THIS LINE
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("image is start",appContext)
        // ADD THIS TO SLOW DOWN THE WORKER
        sleep()
        // ^^^^
        return try {
         //   val picture = BitmapFactory.decodeResource(appContext.resources, R.drawable.android_cupcake)

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appContext, output)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            makeStatusNotification("Output is $outputUri", appContext)

            Result.success(outputData)

        }catch (throwable: Throwable){
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }


}