package net.pmellaaho.rxapp.model

import net.pmellaaho.rxapp.network.GitHubApi
import javax.inject.Inject

class ContributorsRepository @Inject constructor(private val api: GitHubApi) {

    suspend fun getContributors(owner: String, repo: String): List<Contributor> {
        return api.getContributors(owner, repo)
    }

}