package io.ssafy.mogeun.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    @SerialName(value = "user_email")
    val email: String,
    @SerialName(value = "user_pw")
    val pw: String
)

@Serializable
data class SignInResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: String?
)

@Serializable
data class DupEmailBody(
    @SerialName(value = "is_joined")
    val isJoined: Boolean,
    @SerialName(value = "join_state")
    val joinState: String
)

@Serializable
data class DupEmailResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: DupEmailBody
)
@Serializable
data class  SignUpRequest(
    @SerialName(value = "user_email")
    val userEmail: String,
    @SerialName(value = "user_pw")
    val userPw: String,
    @SerialName(value = "user_name")
    val userName: String,
    val gender: String,
    val height: Double?,
    val weight: Double?,
    val smm: Double?,
    val ffm: Double?
)

@Serializable
data class SignUpResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: SignUpResponseBody
)
@Serializable
data class SignUpResponseBody(
    @SerialName(value = "user_email")
    val userEmail: String,
    @SerialName(value = "user_pw")
    val userPw: String,
    @SerialName(value = "user_name")
    val userName: String,
    val gender: String,
    val height: Double,
    val weight: Double,
    val smm: Double,
    val ffm: Double
)