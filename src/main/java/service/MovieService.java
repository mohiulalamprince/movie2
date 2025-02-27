package service;

import consumer.MovieConsumer;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Movie;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.mutiny.redis.client.Response;
import java.util.Arrays;
import java.util.NoSuchElementException;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import org.jboss.logging.Logger;

@Singleton
public class MovieService {
    private final Logger logger = Logger.getLogger(MovieService.class);

    private static final String MOVIE_HASH_PREFIX = "mov:";

    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Multi<Movie> allMovies() {
        return reactiveRedisClient.keys("*")
                .onItem().transformToMulti(response -> Multi.createFrom().iterable(response).map(Response::toString))
                .onItem().transformToUniAndMerge(key ->
                        reactiveRedisClient.hgetall(key)
                                .map(resp ->
                                        constructMovie(Integer.parseInt(key.substring(MOVIE_HASH_PREFIX.length())), resp)));
    }

    public Uni<Movie> getMovie(int year) {
        return reactiveRedisClient.hgetall(MOVIE_HASH_PREFIX + year)
                .map(resp -> resp.size() > 0
                        ? constructMovie(year, resp)
                        : null
                );
    }

    public Uni<Movie> createMovie(Movie movie) {
        return storeMovie(movie);
    }

    public Uni<Movie> storeMovie(Movie movie) {
        logger.infof("movies => %s %d", movie.title, movie.year);
        return reactiveRedisClient.hmset(Arrays.asList(MOVIE_HASH_PREFIX + movie.year, "Title", movie.title))
                .onItem().transform(resp -> {
                    if (resp.toString().equals("OK")) {
                        logger.infof("ok %d", movie.year);
                        return movie;
                    } else {
                        logger.infof("not ok %d", movie.year);

                        throw new NoSuchElementException();
                    }
                });
    }

    Movie constructMovie(int year, Response response) {
        Movie movie = new Movie();
        movie.year = year;
        movie.title = response.get("title").toString();
        return movie;
    }
}