package com.movie2night.data.repository

import com.movie2night.data.remote.api.BlockRequest
import com.movie2night.data.remote.api.RatingRequest
import com.movie2night.data.remote.api.ReportRequest
import com.movie2night.data.remote.api.UpdateProfileRequest
import com.movie2night.data.remote.api.UserApi
import com.movie2night.domain.model.Rating
import com.movie2night.domain.model.Report
import com.movie2night.domain.model.ReportReason
import com.movie2night.domain.model.User
import com.movie2night.domain.model.UserIntention
import com.movie2night.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getUserById(userId: String): User? = runCatching {
        val dto = userApi.getUserById(userId)
        User(
            id = dto.id, name = dto.name, email = dto.email,
            birthDate = dto.birthDate, photoUrl = dto.photoUrl, bio = dto.bio,
            intention = UserIntention.valueOf(dto.intention),
            reputationScore = dto.reputationScore,
            isVerified = dto.isVerified, isBlocked = dto.isBlocked
        )
    }.getOrNull()

    override suspend fun updateProfile(
        name: String, bio: String?, photoUrl: String?, intention: String
    ): Result<User> = runCatching {
        val dto = userApi.updateProfile(UpdateProfileRequest(name, bio, photoUrl, intention))
        User(
            id = dto.id, name = dto.name, email = dto.email,
            birthDate = dto.birthDate, photoUrl = dto.photoUrl, bio = dto.bio,
            intention = UserIntention.valueOf(dto.intention),
            reputationScore = dto.reputationScore,
            isVerified = dto.isVerified, isBlocked = dto.isBlocked
        )
    }

    override suspend fun rateUser(
        matchId: String, ratedUserId: String, score: Int, comment: String?
    ): Result<Rating> = runCatching {
        userApi.rateUser(RatingRequest(matchId, ratedUserId, score, comment))
        Rating(
            id = "", matchId = matchId, raterId = "",
            ratedUserId = ratedUserId, score = score,
            comment = comment, createdAt = ""
        )
    }

    override suspend fun reportUser(
        reportedUserId: String, reason: String, description: String?
    ): Result<Report> = runCatching {
        userApi.reportUser(ReportRequest(reportedUserId, reason, description))
        Report(
            id = "", reporterId = "", reportedUserId = reportedUserId,
            reason = ReportReason.valueOf(reason),
            description = description, createdAt = ""
        )
    }

    override suspend fun blockUser(userId: String): Result<Unit> = runCatching {
        userApi.blockUser(BlockRequest(userId))
    }
}
