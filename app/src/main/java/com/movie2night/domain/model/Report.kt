package com.movie2night.domain.model

data class Report(
    val id: String,
    val reporterId: String,
    val reportedUserId: String,
    val reason: ReportReason,
    val description: String?,
    val createdAt: String
)

enum class ReportReason {
    SPAM,
    INAPPROPRIATE_CONTENT,
    HARASSMENT,
    FAKE_PROFILE,
    OTHER
}
