package consumer;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.Record;
import model.Movie;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import service.MovieService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MovieConsumer {

    private final Logger logger = Logger.getLogger(MovieConsumer.class);

    @Inject
    MovieService movieService;

//    @Incoming("movies-in")
//    public void receive(Record<Integer, String> record) {
//        logger.infof("Got a movie: %d - %s", record.key(), record.value());
//
//        Movie movie = new Movie();
//        movie.title = record.value();
//        movie.year = record.key();
//        movieService.storeMovie(movie);
//    }

    @Incoming("movies-in")
    public CompletionStage<Void> consume(Message<ConsumerRecord<Integer, Movie>> message) {
        logger.infof("Consume 1# partition %d offset %s", message.getPayload().partition(), message.getPayload().offset());
        logger.infof("Received 1# Got a movie %d Movie name: %s ", message.getPayload().key(), message.getPayload().value());
        return message.ack();
    }
}