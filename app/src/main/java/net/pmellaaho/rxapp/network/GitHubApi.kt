package net.pmellaaho.rxapp.network

import net.pmellaaho.rxapp.model.Contributor
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

private const val OWNER = "square"

interface GitHubApi {

    /**
     * See https://developer.github.com/v3/repos/#list-contributors
     */
    @Headers("Content-Type: application/json")
    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("repo") repo: String,
        @Path("owner") owner: String = OWNER
    ): List<Contributor>
}