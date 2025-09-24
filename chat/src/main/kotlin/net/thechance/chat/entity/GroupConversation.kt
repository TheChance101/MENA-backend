package net.thechance.chat.entity

import jakarta.persistence.*

@Entity
@Table(name = "group_conversations", schema = "chat")
open class GroupConversation(

    @Id
    @OneToOne
    @JoinColumn(
        name = "conversation_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_group_conversation_conversation")
    )
    val conversation: Conversation,

    @Column(name = "group_name", nullable = true)
    val groupName: String?,
    @Column(name = "group_image_url", nullable = true)
    val groupImageUrl: String?
)