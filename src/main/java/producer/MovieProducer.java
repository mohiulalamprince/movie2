package producer;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.kafka.Record;
import model.Movie;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

@ApplicationScoped
public class MovieProducer {

    @Inject @Channel("movies-out")
    Emitter<Record<Integer, Movie>> emitter;

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public void sendMovieToKafka(Movie movie) {
        int year = randInt(2000, 9999);
        movie.year = year;
        emitter.send(Record.of(movie.year, movie));
    }
}