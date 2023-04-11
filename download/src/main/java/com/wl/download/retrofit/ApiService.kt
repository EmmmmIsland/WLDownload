package com.wl.download.retrofit

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {
 	@Streaming
 	@GET
 	suspend fun downloadFile(@Url fileUrl: String?): ResponseBody


	@Streaming
	@GET
	suspend fun downloadFile(@Url fileUrl: String?, @Header("Range") bytesRange: String): ResponseBody
 }