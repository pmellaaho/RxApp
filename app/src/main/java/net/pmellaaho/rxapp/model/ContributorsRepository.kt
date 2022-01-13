package net.pmellaaho.rxapp.model

import net.pmellaaho.rxapp.network.GitHubApi
import javax.inject.Inject

class ContributorsRepository @Inject constructor(private val api: GitHubApi) {

    suspend fun getContributors(repo: String): List<Contributor> {
        return api.getContributors(repo)
    }

}