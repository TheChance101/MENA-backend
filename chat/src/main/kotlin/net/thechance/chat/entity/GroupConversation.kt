package net.thechance.chat.entity

import jakarta.persistence.*

@Entity
@Table(name = "group_chats", schema = "chat")
open class GroupChat(

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
    val groupName: String?,
    @Column(name = "group_image_url", nullable = true)
    val groupImageUrl: String?
)