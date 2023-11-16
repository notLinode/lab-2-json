import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
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
                .filter(p -> p.monthlySalary().compareTo(BigDecimal.valueOf(500L)) >= 0)
                .sorted(Comparator.comparing(p -> p.job().title()))
                .forEach(p -> System.out.printf("%s, ", p.job().title()));
        System.out.println();

        Arrays.stream(people)
                .filter(p -> {
                    boolean olderThan30 = p.birthday().isBefore(LocalDateTime.now().minusYears(30));
                    boolean isActive = p.status() == Person.Status.ACTIVE;
                    boolean salaryLessThan100 = p.monthlySalary().compareTo(BigDecimal.valueOf(100L)) < 0;
                    boolean usesExampleDotComEmail = Arrays.stream(p.emails())
                            .anyMatch(email -> email.matches("\\w*@example\\.com"));

                    return olderThan30 && isActive && salaryLessThan100 && usesExampleDotComEmail;
                })
                .forEach(p -> System.out.printf(
                        "%s %s: %s, ", p.name().first(), p.name().last(), Arrays.toString(p.emails())
                ));
        System.out.println();

        System.out.println(
                Arrays.stream(people)
                        .filter(p -> p.status() == Person.Status.DISABLED)
                        .collect(Collectors.groupingBy(
                                p -> p.location().state(),
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue,
                                LinkedHashMap::new
                        ))
                        .toString() // Не работает без toString()
        );

        System.out.println(
            Arrays.stream(people)
                    .filter(p -> {
                        boolean isActive = p.status() == Person.Status.ACTIVE;
                        boolean youngerThan21 = p.birthday().isAfter(LocalDateTime.now().minusYears(21));

                        return isActive && youngerThan21;
                    })
                    .max(Comparator.comparing(Person::monthlySalary))
                    .get()
        );

        System.out.println(
                Arrays.stream(people)
                        .map(p -> p.job().company())
                        .collect(Collectors.toSet())
        );
    }

}