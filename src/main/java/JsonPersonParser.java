import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;

public class JsonPersonParser {

    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(
                LocalDateTime.class,
                new LocalDateTimeDeserializer("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        );
        gson = gsonBuilder.create();
    }

    private JsonPersonParser() {}

    public static Person[] parseFromFile(String filePath) throws IOException {
        Person[] people;

        try (Reader reader = new FileReader(filePath)) {
            people = gson.fromJson(reader, Person[].class);
        }

        return people;
    }

}