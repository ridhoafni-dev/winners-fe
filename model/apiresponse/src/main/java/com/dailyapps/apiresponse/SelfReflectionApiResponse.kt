package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class SelfReflectionApiResponse(
    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("description")
    val title: String? = null,

    @field:SerializedName("active")
    val active: Boolean? = null,

    @field:SerializedName("userId")
    val userId: Long? = null,

    @field:SerializedName("user")
    val user: UserApiResponse? = null,

    @field:SerializedName("createAt")
    val createAt: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("selfEvaluationLecturer")
    val selfReflectionLecturer: SelfReflectionLecturerApiResponse? = null,

    @field:SerializedName("selfReflectionComment")
    val selfReflectionCommentResponse: SelfReflectionCommentApiResponse? = null
)

data class SelfReflectionLecturerApiResponse(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: Long? = null,

    @field:SerializedName("id")
    val id: Long? = null
)

data class SelfReflectionCommentApiResponse(
    @field:SerializedName("rating")
    val rating: Int? = null,

    @field:SerializedName("comment")
    val comment: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("userId")
    val userId: Int? = null
)
