package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "group_chats", schema = "chat")
data class GroupChat(

    @Id
    @OneToOne
    @JoinColumn(
        name = "chat_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_group_chat_chat")
    )
    val chat: Chat,

    @Column(name = "group_name", nullable = true)
    val groupName: String,

    @Column(name = "group_image_url", nullable = true)
    val groupImageUrl: String?
)