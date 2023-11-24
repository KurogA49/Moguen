package io.ssafy.mogeun.data

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

interface DataLayerRepository{
    suspend fun launchMogeun()
    suspend fun noticeExerciseName(message: String)
    suspend fun noticeTimer(message: String)
}

class AndroidDataLayerRepository(
    private val context: Context
): DataLayerRepository {
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    override suspend fun launchMogeun() {
        try {
            val nodes = Tasks.await(
                capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            ).nodes

            nodes.map { node ->
                messageClient.sendMessage(
                    node.id,
                    MOGEUN_SERVICE_START_PATH,
                    byteArrayOf()
                )
            }

        } catch(exception: Exception) {
            Log.d(TAG, "Send start failed: $exception")
        }
    }

    override suspend fun noticeExerciseName(message: String) {
        try {
            val nodes = Tasks.await(
                capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            ).nodes

            nodes.map { node ->
                messageClient.sendMessage(
                    node.id,
                    MOGEUN_EXERCISE_NAME_MESSAGE_PATH,
                    message.toByteArray()
                )
            }

        } catch(exception: Exception) {
            Log.d(TAG, "Send message failed: $exception")
        }
    }

    override suspend fun noticeTimer(message: String) {
        try {
            val nodes = Tasks.await(
                capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
            ).nodes

            nodes.map { node ->
                messageClient.sendMessage(
                    node.id,
                    MOGEUN_ROUTINE_TIMER_MESSAGE_PATH,
                    message.toByteArray()
                )
            }

        } catch(exception: Exception) {
            Log.d(TAG, "Send message failed: $exception")
        }
    }

    companion object {
        private const val TAG = "datalayer"

        private const val WEAR_CAPABILITY = "mogeun_transcription"
        private const val MOGEUN_SERVICE_START_PATH = "/mogeun_start"
        private const val MOGEUN_EXERCISE_NAME_MESSAGE_PATH = "/mogeun_routine_name"
        private const val MOGEUN_ROUTINE_TIMER_MESSAGE_PATH = "/mogeun_routine_timer"
        private const val MOGEUN_ROUTINE_START_SET_PATH = "/mogeun_start_set"
        private const val MOGEUN_ROUTINE_END_SET_PATH = "/mogeun_end_set"
    }
}