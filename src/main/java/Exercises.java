import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Exercises {

    private Exercises() {}

    public static void ex1(Person[] people) {
        System.out.println("1. Вывести в консоль краткое ФИО всех активных пользователей;");

        Arrays.stream(people)
                .filter(p -> p.status() == Person.Status.ACTIVE)
                .forEach(p -> System.out.printf("%s %s, ", p.name().first(), p.name().last()));

        System.out.println();
    }

    public static void ex2(Person[] people) {
        System.out.println("2. Вывести количество пользователей, которые используют только почту gmail.com;");

        System.out.println(
                Arrays.stream(people)
                        .filter(
                                p -> Arrays.stream(p.emails())
                                        .allMatch(email -> email.matches("\\w*@gmail\\.com"))
                        )
                        .count()
        );
    }

    public static void ex3(Person[] people) {
        System.out.println("3. Вывести статистику какими картами и сколько человек пользуется;");

        System.out.println(
                Arrays.stream(people)
                        .collect(Collectors.groupingBy(
                                p -> p.creditCard().issuer(),
                                Collectors.counting())
                        )
        );
    }

    public static void ex4(Person[] people) {
        System.out.println("4. Вывести отсортированный алфавитном порядке список названий профессий," +
                "за которые платят больше 500.00;");

        Arrays.stream(people)
                .filter(p -> p.monthlySalary().compareTo(BigDecimal.valueOf(500L)) >= 0)
                .sorted(Comparator.comparing(p -> p.job().title()))
                .forEach(p -> System.out.printf("%s, ", p.job().title()));

        System.out.println();
    }

    public static void ex5(Person[] people) {
        System.out.println("5. Вывести ФИО и почту тех, кто старше 30 лет, активном статусе," +
                "имеет заработок ниже 100.00, использует почту example.com;");

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
    }

    public static void ex6(Person[] people) {
        System.out.println("6. Собрать статистику по штатам с количеством неактивных пользователей," +
                "отсортировать по убыванию, вывести первые 5;");

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
    }

    public static void ex7(Person[] people) {
        System.out.println("7. Найти самого высокооплачиваемого активного пользователя в возрасте до 21 года;");

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
    }

    public static void ex8(Person[] people) {
        System.out.println("8. Вывести все уникальные названия компаний.");

        System.out.println(
                Arrays.stream(people)
                        .map(p -> p.job().company())
                        .collect(Collectors.toSet())
        );
    }

}