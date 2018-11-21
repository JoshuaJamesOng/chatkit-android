package com.pusher.chatkit.rooms

import com.google.gson.annotations.SerializedName
import com.pusher.chatkit.CustomData

data class Room(
        val id: String,
        val createdById: String,
        var name: String,
        @SerializedName("private")
        var isPrivate: Boolean,
        var customData: CustomData?,
        val createdAt: String,
        var updatedAt: String,
        var deletedAt: String
) {
    @SerializedName("member_user_ids") private var _memberUserIds: MutableSet<String>? = null
    val memberUserIds: Set<String>
        get() = memberUserIds()

    private fun memberUserIds(): MutableSet<String> = _memberUserIds
        ?: mutableSetOf<String>().also { _memberUserIds = it }

    fun removeUser(userId: String) {
        memberUserIds() -= userId
    }

    fun addUser(userId: String) {
        memberUserIds() += userId
    }

    override fun equals(other: Any?) = (other is Room) && other.id == this.id

    fun deepEquals(room: Room) =
            room.name == this.name && room.customData == this.customData && room.isPrivate == this.isPrivate
}
