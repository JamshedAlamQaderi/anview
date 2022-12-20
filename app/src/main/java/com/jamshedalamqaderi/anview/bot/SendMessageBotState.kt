package com.jamshedalamqaderi.anview.bot

enum class SendMessageBotState {
    PREPARE_OPEN_APP, VERIFY_OPEN_APP,
    PREPARE_CLICK_NEW, VERIFY_CLICK_NEW,
    PREPARE_TYPE_NUMBER, VERIFY_TYPE_NUMBER,
    PREPARE_TYPE_MESSAGE, VERIFY_TYPE_MESSAGE,
    PREPARE_CLICK_SEND, VERIFY_CLICK_SEND,
    COMPLETED
}
