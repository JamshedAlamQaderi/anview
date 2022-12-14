package com.jamshedalamqaderi.anview.bot

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.jamshedalamqaderi.anview.dsl.anViewQuery
import com.jamshedalamqaderi.anview.enums.ParamType
import com.jamshedalamqaderi.anview.ext.AccessibilityNodeInfoExt.findNodes
import com.jamshedalamqaderi.anview.ext.AnViewActions
import com.jamshedalamqaderi.anview.ext.AnViewActions.click
import com.jamshedalamqaderi.anview.ext.AnViewActions.inputText

class SendMessageBot(
    private val context: Context,
    private val number: String,
    private val message: String
) {
    private var nextState = SendMessageBotState.PREPARE_OPEN_APP

    private val startChatButtonQuery = anViewQuery {
        params {
            param(ParamType.packageName, "com.google.android.apps.messaging")
            param(ParamType.className, "android.widget.Button")
            param(ParamType.text, "Start chat")
        }
    }

    private val typeNumberFieldQuery = anViewQuery {
        params {
            param(ParamType.packageName, "com.google.android.apps.messaging")
            param(ParamType.className, "android.widget.ScrollView")
        }
        query {
            params {
                param(ParamType.packageName, "com.google.android.apps.messaging")
                param(ParamType.className, "android.widget.MultiAutoCompleteTextView")
            }
        }
    }

    private val typeMessageField = anViewQuery {
        params {
            param(ParamType.packageName, "com.google.android.apps.messaging")
            param(ParamType.className, "android.view.ViewGroup")
        }
        query {
            params {
                param(ParamType.packageName, "com.google.android.apps.messaging")
                param(ParamType.className, "android.widget.EditText")
            }
        }
    }

    private val sendSmsButton = anViewQuery {
        params {
            param(ParamType.packageName, "com.google.android.apps.messaging")
            param(ParamType.className, "android.view.ViewGroup")
        }
        query {
            params {
                param(ParamType.packageName, "com.google.android.apps.messaging")
                param(ParamType.className, "android.widget.LinearLayout")
                param(ParamType.contentDescription, "Send SMS")
            }
        }
    }

    fun onUpdate(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) return
        Log.i(this::class.simpleName, "Current state: $nextState")
        when (nextState) {
            SendMessageBotState.PREPARE_OPEN_APP -> prepareOpenApp()
            SendMessageBotState.VERIFY_OPEN_APP -> verifyOpenApp(nodeInfo)
            SendMessageBotState.PREPARE_CLICK_NEW -> prepareClickNew(nodeInfo)
            SendMessageBotState.VERIFY_CLICK_NEW -> verifyClickNew(nodeInfo)
            SendMessageBotState.PREPARE_TYPE_NUMBER -> prepareTypeNumber(nodeInfo)
            SendMessageBotState.VERIFY_TYPE_NUMBER -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                verifyTypeNumber(nodeInfo)
            }
            SendMessageBotState.PREPARE_TYPE_MESSAGE -> prepareTypeMessage(nodeInfo)
            SendMessageBotState.VERIFY_TYPE_MESSAGE -> verifyTypeMessage(nodeInfo)
            SendMessageBotState.PREPARE_CLICK_SEND -> prepareClickSend(nodeInfo)
            SendMessageBotState.VERIFY_CLICK_SEND -> verifyClickSend(nodeInfo)
            else -> {}
        }
    }

    private fun prepareOpenApp() {
        // launch message app
        val intent = context
            .packageManager
            .getLaunchIntentForPackage("com.google.android.apps.messaging")
        if (intent != null) {
            context.startActivity(intent)
            nextState = SendMessageBotState.VERIFY_OPEN_APP
        }
    }

    private fun verifyOpenApp(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo.findNodes(startChatButtonQuery)
        if (matchingNodes.isNotEmpty()) {
            nextState = SendMessageBotState.PREPARE_CLICK_NEW
        }
    }

    private fun prepareClickNew(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo.findNodes(startChatButtonQuery)
        if (matchingNodes.isNotEmpty()) {
            val startChatButton = matchingNodes.first()
            if (startChatButton.click()) {
                nextState = SendMessageBotState.VERIFY_CLICK_NEW
            }
        }
    }

    private fun verifyClickNew(nodeInfo: AccessibilityNodeInfo) {
        if (nodeInfo.findNodes(typeNumberFieldQuery).isNotEmpty()) {
            nextState = SendMessageBotState.PREPARE_TYPE_NUMBER
        }
    }

    private fun prepareTypeNumber(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo.findNodes(typeNumberFieldQuery)
        if (matchingNodes.isNotEmpty()) {
            val numberField = matchingNodes.first()
            if (numberField.inputText(number)) {
                nextState = SendMessageBotState.VERIFY_TYPE_NUMBER
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun verifyTypeNumber(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo.findNodes(typeNumberFieldQuery)
        if (matchingNodes.isNotEmpty()) {
            val numberField = matchingNodes.first()
            if (numberField.text?.toString() == number) {
                val displayMetrics = context.resources.displayMetrics
                AnViewActions.tap(
                    displayMetrics.widthPixels * .99F,
                    displayMetrics.heightPixels * .99F
                )
                nextState = SendMessageBotState.PREPARE_TYPE_MESSAGE
            }
        }
    }

    private fun prepareTypeMessage(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo
            .findNodes(typeMessageField)
            .filter { it.text?.toString() == "Text" }
        if (matchingNodes.isNotEmpty()) {
            val messageField = matchingNodes.first()
            if (messageField.inputText(message)) {
                nextState = SendMessageBotState.VERIFY_TYPE_MESSAGE
            }
        }
    }

    private fun verifyTypeMessage(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo
            .findNodes(typeMessageField)
            .filter { it.text?.toString() == message }
        if (matchingNodes.isNotEmpty()) {
            nextState = SendMessageBotState.PREPARE_CLICK_SEND
        }
    }

    private fun prepareClickSend(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo.findNodes(sendSmsButton)
        if (matchingNodes.isNotEmpty()) {
            val sendSmsBtn = matchingNodes.first()
            if (sendSmsBtn.click()) {
                nextState = SendMessageBotState.VERIFY_CLICK_SEND
            }
        }
    }

    private fun verifyClickSend(nodeInfo: AccessibilityNodeInfo) {
        val matchingNodes = nodeInfo
            .findNodes(typeMessageField)
            .filter { it.text?.toString() == "Text" }
        if (matchingNodes.isNotEmpty()) {
            nextState = SendMessageBotState.COMPLETED
            AnViewActions.pressHome()
        }
    }
}
