package com.movie2night.domain.repository

import com.movie2night.domain.model.Rating
import com.movie2night.domain.model.Report
import com.movie2night.domain.model.User

interface UserRepository {
    suspend fun getUserById(userId: String): User?

    suspend fun updateProfile(
        name: String,
        bio: String?,
        photoUrl: String?
    ): Result<User>

    suspend fun rateUser(
        matchId: String,
        ratedUserId: String,
        score: Int,
        comment: String?
    ): Result<Rating>

    suspend fun reportUser(
        reportedUserId: String,
        reason: String,
        description: String?
    ): Result<Report>

    suspend fun blockUser(userId: String): Result<Unit>
}