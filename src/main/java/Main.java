import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Person[] people;

        try {
            people = JsonPersonParser.parseFromFile("data/data.json");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

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
    }

}