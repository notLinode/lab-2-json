import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        Person[] people;

        try {
            people = JsonPersonParser.parseFromFile("data/data.json");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        doExercisesStreamApi(people);

        System.out.println("====================================================================================\n");

        String url = "jdbc:postgresql://localhost:5432/people";
        String user = "linode";
        String pass = "?";

        try (Connection connection = DriverManager.getConnection(url, user, pass)) {
            PersonSqlParser parser = new PersonSqlParser(connection);

            parser.dropTables();
            parser.createTables();
            parser.parseArray(people);

            ExercisesSql exercisesSql = new ExercisesSql(connection);
            doExercisesSql(exercisesSql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private static void doExercisesStreamApi(Person[] people) {
        Exercises.ex1(people);
        System.out.println();
        Exercises.ex2(people);
        System.out.println();
        Exercises.ex3(people);
        System.out.println();
        Exercises.ex4(people);
        System.out.println();
        Exercises.ex5(people);
        System.out.println();
        Exercises.ex6(people);
        System.out.println();
        Exercises.ex7(people);
        System.out.println();
        Exercises.ex8(people);
        System.out.println();
    }

    private static void doExercisesSql(ExercisesSql exercisesSql) throws SQLException {
        exercisesSql.ex1();
        System.out.println();
        exercisesSql.ex2();
        System.out.println();
        exercisesSql.ex3();
        System.out.println();
        exercisesSql.ex4();
        System.out.println();
        exercisesSql.ex5();
        System.out.println();
        exercisesSql.ex6();
        System.out.println();
        exercisesSql.ex7();
        System.out.println();
        exercisesSql.ex8();
        System.out.println();
    }

}