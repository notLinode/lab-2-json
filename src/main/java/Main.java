import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Person[] people;

        try {
            people = JsonPersonParser.parseFromFile("data/data.json");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        Arrays.stream(people)
                .filter(p -> p.status() == Person.Status.ACTIVE)
                .forEach(p -> System.out.printf("%s %s, ", p.name().first(), p.name().last()));
        System.out.println();

        System.out.println(
                Arrays.stream(people)
                .filter(
                        p -> Arrays.stream(p.emails())
                                .allMatch(email -> email.matches("\\w*@gmail\\.com"))
                )
                .count()
        );

        System.out.println(
                Arrays.stream(people)
                        .collect(Collectors.groupingBy(
                                p -> p.creditCard().issuer(),
                                Collectors.counting())
                        )
        );

        Arrays.stream(people)
                .filter(p -> p.monthlySalary().compareTo(BigDecimal.valueOf(500)) >= 0)
                .sorted(Comparator.comparing(p -> p.job().title()))
                .forEach(p -> System.out.printf("%s, ", p.job().title()));
        System.out.println();
    }

}