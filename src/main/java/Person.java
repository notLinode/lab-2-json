import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Person(
        int id,
        Status status,
        Name name,
        BigDecimal monthlySalary,
        LocalDateTime birthday,
        String username,
        String password,
        String[] emails,
        String phoneNumber,
        Location location,
        String website,
        String domain,
        Job job,
        CreditCard creditCard,
        String uuid,
        String objectId
) {

    public enum Status {

        ACTIVE ("ACTIVE"),
        DISABLED ("DISABLED");

        public final String status;

        Status(String status) {
            this.status = status;
        }
    }

    public record Name(
            String first,
            String middle,
            String last
    ) {}

    public record Location(
            String street,
            String city,
            String state,
            String country,
            String zip,
            Coordinates coordinates
    ) {

        public record Coordinates(
                Double latitude,
                Double longitude
        ) {}

    }

    public record Job(
            String title,
            String descriptor,
            String area,
            String type,
            String company
    ) {}

    public record CreditCard(
            String number,
            String cvv,
            String issuer
    ) {}

}