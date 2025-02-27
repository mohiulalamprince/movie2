package serializer;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import model.Movie;

public class MovieDeserializer extends ObjectMapperDeserializer<Movie> {
    public MovieDeserializer() {
        super(Movie.class);
    }
}
