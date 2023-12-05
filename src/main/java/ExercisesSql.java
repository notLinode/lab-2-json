import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class ExercisesSql {
    private final Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public ExercisesSql(Connection connection) {
        this.connection = connection;
    }

    public void ex1() throws SQLException {
        System.out.println("1. Вывести в консоль краткое ФИО всех активных пользователей;");

        ps = connection.prepareStatement(
                "SELECT FirstName, LastName FROM People;"
        );

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s %s, ", rs.getString(1), rs.getString(2));
        }

        System.out.println();
    }

    public void ex2() throws SQLException {
        System.out.println("2. Вывести количество пользователей, которые используют только почту gmail.com;");

        ps = connection.prepareStatement(
                """
                    SELECT COUNT(*) FROM (
                        SELECT PersonID FROM Emails
                        GROUP BY PersonID
                        HAVING COUNT(*) = SUM(CASE WHEN Domain = ? THEN 1 ELSE 0 END)
                    );
                    """
        );
        ps.setString(1, "gmail.com");

        rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println(rs.getInt(1));
        }
    }

    public void ex3() throws SQLException {
        System.out.println("3. Вывести статистику какими картами и сколько человек пользуется;");

        ps = connection.prepareStatement(
                "SELECT Issuer, COUNT(Issuer) FROM CreditCards GROUP BY Issuer;"
        );

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s: %d | ", rs.getString(1), rs.getInt(2));
        }

        System.out.println();
    }

    public void ex4() throws SQLException {
        System.out.println("4. Вывести отсортированный алфавитном порядке список названий профессий," +
                "за которые платят больше 500.00;");

        ps = connection.prepareStatement(
                """
                    SELECT Jobs.Title
                    FROM People
                    INNER JOIN Jobs ON People.JobID = Jobs.JobID
                    WHERE People.MonthlySalary > ?
                    ORDER BY Jobs.Title;
                    """
        );
        ps.setBigDecimal(1, BigDecimal.valueOf(500));

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s, ", rs.getString(1));
        }

        System.out.println();
    }

    public void ex5() throws SQLException {
        System.out.println("5. Вывести ФИО и почту тех, кто старше 30 лет, активном статусе," +
                "имеет заработок ниже 100.00, использует почту example.com;");

        ps = connection.prepareStatement(
                """
                    SELECT
                        FirstName,
                        LastName,
                        Emails.Username || ? || Emails.Domain Email
                    FROM People
                    INNER JOIN Emails ON People.PersonID = Emails.PersonID
                    WHERE
                        Birthday < ?
                        AND Status = TRUE
                        AND MonthlySalary < ?
                        AND Emails.Domain = ?;
                    """
        );

        ps.setString(1, "@");
        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusYears(30)));
        ps.setBigDecimal(3, BigDecimal.valueOf(100));
        ps.setString(4, "example.com");

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s %s: %s | ", rs.getString(1), rs.getString(2), rs.getString(3));
        }

        System.out.println();
    }

    public void ex6() throws SQLException {
        System.out.println("6. Собрать статистику по штатам с количеством неактивных пользователей," +
                "отсортировать по убыванию, вывести первые 5;");

        ps = connection.prepareStatement(
                """
                    SELECT
                        State,
                        COUNT(Locations.LocationID) Cnt
                    FROM Locations
                    INNER JOIN People ON Locations.LocationID = People.LocationID
                    WHERE Status = FALSE
                    GROUP BY State
                    ORDER BY Cnt DESC
                    LIMIT ?;
                    """
        );

        ps.setInt(1, 5);

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s: %d | ", rs.getString(1), rs.getInt(2));
        }

        System.out.println();
    }

    public void ex7() throws SQLException {
        System.out.println("7. Найти самого высокооплачиваемого активного пользователя в возрасте до 21 года;");

        ps = connection.prepareStatement(
                """
                    SELECT
                        PersonID,
                        FirstName,
                        LastName,
                        MonthlySalary
                    FROM People
                    WHERE Birthday > ?
                    ORDER BY MonthlySalary DESC
                    LIMIT 1;
                    """
        );

        ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusYears(21)));

        rs = ps.executeQuery();

        if (rs.next()) {
            System.out.printf("%d: %s %s - %.2f\n", rs.getInt(1), rs.getString(2),
                    rs.getString(3), rs.getBigDecimal(4));
        }
    }

    public void ex8() throws SQLException {
        System.out.println("8. Вывести все уникальные названия компаний.");

        ps = connection.prepareStatement(
                """
                    SELECT DISTINCT ON(Company)
                        COMPANY
                    FROM Jobs
                    """
        );

        rs = ps.executeQuery();

        while (rs.next()) {
            System.out.printf("%s | ", rs.getString(1));
        }

        System.out.println();
    }

}