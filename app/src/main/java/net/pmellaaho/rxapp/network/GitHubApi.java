package net.pmellaaho.rxapp.network;

import net.pmellaaho.rxapp.model.Contributor;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface GitHubApi {

    /**
     * See https://developer.github.com/v3/repos/#list-contributors
     */
    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> contributors(@Path("owner") String owner,
                                               @Path("repo") String repo);

}
