package com.example.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T> (
    val code: Int,
    val message: String?,
    val data: T?,
    val meta: Meta?
) {
  @Serializable
  data class Meta(
      val total: Long,
      val page: Int,
      @SerialName("page_of_number")
      val pageOfNumber: Int
  )
}