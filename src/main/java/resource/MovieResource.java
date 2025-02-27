package resource;

import consumer.MovieConsumer;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Movie;
import org.jboss.logging.Logger;
import producer.MovieProducer;
import service.MovieService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestPath;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
    private final Logger logger = Logger.getLogger(MovieResource.class);

    @Inject
    MovieProducer producer;

    @Inject
    MovieService client;

    @GET
    public Multi<Movie> allMovies() {
        return client.allMovies();
    }

    @GET
    @Path("{id}")
    public Uni<Movie> getMovie(@RestPath int year) {
        return client.getMovie(year).onItem().ifNull()
                .failWith(new WebApplicationException("Failed to find Movie", Response.Status.NOT_FOUND));
    }

    @POST
    public Response send(Movie movie) {
        logger.infof("movies %s", movie.title);

        producer.sendMovieToKafka(movie);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }


}