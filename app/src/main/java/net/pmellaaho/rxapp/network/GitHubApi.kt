package net.pmellaaho.rxapp.network

import io.reactivex.Observable
import net.pmellaaho.rxapp.model.Contributor
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubApi {

    /**
     * See https://developer.github.com/v3/repos/#list-contributors
     */
    @Headers("Content-Type: application/json")
    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<List<Contributor>>

    @Headers("Content-Type: application/json")
    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<Contributor>
}