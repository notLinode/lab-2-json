import java.sql.*;

public class PersonSqlParser {
    private final Connection connection;

    public PersonSqlParser(Connection connection) throws SQLException {
        this.connection = connection;
    }

    public void parseArray(Person[] people) throws SQLException {
        for (Person person : people) {
            parseObject(person);
        }
    }

    public void parseObject(Person person) throws SQLException {
        // Могут быть повторы в таблицах локаций, работ и кредиток, но мне лень сейчас писать проверку.
        PreparedStatement ps;
        ResultSet rs;
        int locationId, jobId, creditCardId, personId;
        locationId = jobId = creditCardId = personId = -1;

// ========================================== INSERT LOCATION ==========================================
        ps = connection.prepareStatement(
                """
                    INSERT INTO Locations(Street, City, State, Country, Zip, Latitude, Longitude)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING
                    RETURNING LocationID
                    """
        );

        ps.setString(1, person.location().street());
        ps.setString(2, person.location().city());
        ps.setString(3, person.location().state());
        ps.setString(4, person.location().country());
        ps.setString(5, person.location().zip());
        ps.setDouble(6, person.location().coordinates().latitude());
        ps.setDouble(7, person.location().coordinates().longitude());

        rs = ps.executeQuery();
        if (rs.next()) {
            locationId = rs.getInt("LocationID");
        }
// ------------------------------------------------------------------------------------------------

// ========================================== INSERT JOB ==========================================
        ps = connection.prepareStatement(
                """
                    INSERT INTO Jobs(Title, Descriptor, Area, Type, Company)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING
                    RETURNING JobID;
                    """
        );

        ps.setString(1, person.job().title());
        ps.setString(2, person.job().descriptor());
        ps.setString(3, person.job().area());
        ps.setString(4, person.job().type());
        ps.setString(5, person.job().company());

        rs = ps.executeQuery();
        if (rs.next()) {
            jobId = rs.getInt("JobID");
        }
// ------------------------------------------------------------------------------------------------

// ====================================== INSERT CREDIT CARD ======================================
        ps = connection.prepareStatement(
                """
                    INSERT INTO CreditCards(Number, CVV, Issuer)
                    VALUES (?, ?, ?)
                    ON CONFLICT DO NOTHING
                    RETURNING CreditCardID;
                    """
        );

        ps.setString(1, person.creditCard().number());
        ps.setString(2, person.creditCard().cvv());
        ps.setString(3, person.creditCard().issuer());

        rs = ps.executeQuery();
        if (rs.next()) {
            creditCardId = rs.getInt("CreditCardID");
        }
// -------------------------------------------------------------------------------------------------

// ========================================= INSERT PERSON =========================================
        ps = connection.prepareStatement(
                """
                    INSERT INTO
                        People(Status, FirstName, MiddleName, LastName, MonthlySalary, Birthday, Username,
                        Password, PhoneNumber, LocationID, Website, Domain, JobID, CreditCardID, UUID, ObjectID)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT DO NOTHING
                    RETURNING PersonID;
                    """
        );

        ps.setBoolean(1, person.status() == Person.Status.ACTIVE);
        ps.setString(2, person.name().first());
        ps.setString(3, person.name().middle());
        ps.setString(4, person.name().last());
        ps.setBigDecimal(5, person.monthlySalary());
        ps.setTimestamp(6, Timestamp.valueOf(person.birthday()));
        ps.setString(7, person.username());
        ps.setString(8, person.password());
        ps.setString(9, person.phoneNumber());
        ps.setInt(10, locationId);
        ps.setString(11, person.website());
        ps.setString(12, person.domain());
        ps.setInt(13, jobId);
        ps.setInt(14, creditCardId);
        ps.setString(15, person.uuid());
        ps.setString(16, person.objectId());

        rs = ps.executeQuery();
        if (rs.next()) {
            personId = rs.getInt("PersonID");
        }
// -------------------------------------------------------------------------------------------------

// ========================================= INSERT EMAILS =========================================
        ps = connection.prepareStatement(
                "INSERT INTO Emails(PersonID, Username, Domain) VALUES (?, ?, ?);"
        );

        for (String email : person.emails()) {
            String username = email.split("@")[0];
            String domain = email.split("@")[1];

            ps.setInt(1, personId);
            ps.setString(2, username);
            ps.setString(3, domain);

            ps.executeUpdate();
        }
// -------------------------------------------------------------------------------------------------
    }

    public void createTables() throws SQLException {
        PreparedStatement ps;

        ps = connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS Locations (
                        LocationID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        Street VARCHAR(30),
                        City VARCHAR(30),
                        State VARCHAR(30),
                        Country VARCHAR(52),
                        Zip VARCHAR(20),
                        Latitude FLOAT,
                        Longitude FLOAT,
                        UNIQUE (Latitude, Longitude)
                    );
                    """
        );
        ps.executeUpdate();

        ps = connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS Jobs (
                    JobID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    Title VARCHAR(50),
                    Descriptor VARCHAR(50),
                    Area VARCHAR(50),
                    Type VARCHAR(30),
                    Company VARCHAR(50),
                    UNIQUE (Title, Descriptor, Area, Type, Company)
                );
                """
        );
        ps.executeUpdate();

        ps = connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS CreditCards (
                        CreditCardID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        Number VARCHAR(30),
                        CVV VARCHAR(4),
                        Issuer VARCHAR(30),
                        UNIQUE (Number, CVV, Issuer)
                    );
                    """
        );
        ps.executeUpdate();

        ps = connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS People (
                        PersonID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        Status BOOLEAN,
                        FirstName VARCHAR(50),
                        MiddleName VARCHAR(50),
                        LastName VARCHAR(50),
                        MonthlySalary NUMERIC,
                        Birthday TIMESTAMP,
                        Username VARCHAR(30),
                        Password VARCHAR(30),
                        PhoneNumber VARCHAR(30),
                        LocationID INT,
                        FOREIGN KEY (LocationID) REFERENCES Locations(LocationID),
                        Website VARCHAR(50),
                        Domain VARCHAR(50),
                        JobID INT,
                        FOREIGN KEY (JobID) REFERENCES Jobs(JobID),
                        CreditCardID INT,
                        FOREIGN KEY (CreditCardID) REFERENCES CreditCards(CreditCardID),
                        UUID VARCHAR(50) UNIQUE,
                        ObjectID VARCHAR(50) UNIQUE                   
                    );
                    """
        );
        ps.executeUpdate();

        ps = connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS Emails (
                        EmailID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        PersonID INT,
                        FOREIGN KEY (PersonID) REFERENCES People(PersonID),
                        Username VARCHAR(30),
                        Domain VARCHAR(30),
                        UNIQUE (PersonID, Username, Domain)
                    );
                    """
        );
        ps.executeUpdate();
    }

    public void dropTables() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "DROP TABLE IF EXISTS Emails, Locations, Jobs, CreditCards, People;"
        );
        ps.executeUpdate();
    }

}