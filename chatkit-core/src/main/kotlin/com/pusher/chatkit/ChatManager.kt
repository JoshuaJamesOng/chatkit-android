package com.pusher.chatkit

import com.pusher.platform.network.Futures
import com.pusher.util.Result
import elements.Error

class ChatManager(
         instanceLocator: String,
         userId: String,
         dependencies: ChatkitDependencies
) {
    private val syncChatManager = SynchronousChatManager(
            instanceLocator,
            userId,
            dependencies
    )

    fun connect(listeners: ChatListeners, callback: (Result<CurrentUser, Error>) -> Unit) =
            connect(listeners.toCallback(), callback)

    @JvmOverloads
    fun connect(consumer: ChatManagerEventConsumer = {}, callback: (Result<CurrentUser, Error>) -> Unit) {
        makeCallback(
                f = { syncChatManager.connect(consumer) },
                c = callback
        )
    }

    /**
     * Tries to close all pending subscriptions and resources
     */
    fun close(callback: (Result<Unit, Error>) -> Unit) {
        makeCallback(
                f = { syncChatManager.close() },
                c = callback
        )
    }

    /**
     * If you would prefer calls to block and to manage your own concurrency with threading or
     * coroutines, this returns a chatmanager with synchronous interface.
     *
     * e.g.
     *
     *   chatManager.connect(listeners) { result ->
     *     // result has your currentUser object
     *   }
     *   // this line executes before connect is complete
     *
     * vs
     *
     *   val result = chatManager.blocking().connect(listeners)
     *   // result has your currentUser object
     *   // this line does not execute connect has completed
     */
    fun blocking() = syncChatManager
}

fun <V, E: Error> makeCallback(f: () -> Result<V, E>, c: (Result<V, E>) -> Unit) {
    Futures.schedule {
        c.invoke(f())
    }
}
