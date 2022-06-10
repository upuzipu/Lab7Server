package connection;

import collection.Car;
import collection.UserToken;
import utils.DateAdapter;
import ioManager.RequestElement;
import utils.Encryptor;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentSkipListSet;

public class SQLManagerDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String addWithIDTemplate = "INSERT INTO world (id, name, coordinateX, coordinateY," +
            " creationDate, enginePower, vehicleType, fuelType," +
            "username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String addWithoutIDTemplate = "INSERT INTO world (name, coordinateX, coordinateY," +
            " creationDate, enginePower, vehicleType, fuelType," +
            " username) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final Connection connection;

    public SQLManagerDB(String adminLogin, String password) throws SQLException {
        this.connection = DriverManager.getConnection(URL, adminLogin, password);
    }

    public ConcurrentSkipListSet<Car> getCollection() throws SQLException {
        Statement statement = connection.createStatement();
        ConcurrentSkipListSet<Car> world = new ConcurrentSkipListSet<>();
        ResultSet result = statement.executeQuery("SELECT * FROM world;");
        while (result.next()) {
            String[] values = new String[15];

            for (int i = 0; i < 15; i++) {
                values[i] = result.getString(i + 1);
            }
            world.add(RequestElement.readElement(values));
        }
        statement.close();
        return world;
    }

    public void addWithID(Car car, UserToken user) throws SQLException {
        java.sql.Date sqlDate = new java.sql.Date(car.getCreationDate().getTime());
        PreparedStatement statement = connection.prepareStatement(addWithIDTemplate);
        statement.setInt(1, car.getId());
        statement.setString(2, car.getName());
        statement.setFloat(3, car.getCoordinates().getX());
        statement.setFloat(4, car.getCoordinates().getY());
        statement.setString(5, String.valueOf(sqlDate));
        statement.setInt(6, car.getEnginePower());
        if (car.getVehicleType() == null)
            statement.setString(7, null);
        else
            statement.setString(7, car.getVehicleType().toString());
        if (car.getFuelType() == null)
            statement.setString(8, null);
        else
            statement.setString(8, car.getFuelType().toString());
        statement.setString(9, user.getLogin());
        statement.executeUpdate();
        statement.close();
    }

    public void addWithoutID(Car car, UserToken user) throws SQLException {
        java.sql.Date sqlDate = new java.sql.Date(car.getCreationDate().getTime());
        PreparedStatement statement = connection.prepareStatement(addWithoutIDTemplate);
        statement.setString(1, car.getName());
        statement.setFloat(2, car.getCoordinates().getX());
        statement.setFloat(3, car.getCoordinates().getY());
        statement.setString(4, String.valueOf(sqlDate));
        statement.setInt(5, car.getEnginePower());
        if (car.getVehicleType() == null)
            statement.setString(6, null);
        else
            statement.setString(6, car.getVehicleType().toString());
        if (car.getFuelType() == null)
            statement.setString(7, null);
        else
            statement.setString(7, car.getFuelType().toString());
        statement.setString(8, user.getLogin());
        statement.executeUpdate();
        statement.close();
    }

    public int remove(Car car, UserToken user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM world WHERE id= ? AND username= ?;");
        statement.setInt(1, car.getId());
        statement.setString(2, user.getLogin());
        int res = statement.executeUpdate();
        statement.close();
        return res;
    }

    public int clear(UserToken user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM world WHERE username= ?;");
        statement.setString(1, user.getLogin());
        int res = statement.executeUpdate();
        statement.close();
        return res;
    }

    public void registerUser(UserToken user) throws SQLException {
        String login = user.getLogin();
        String password = Encryptor.encryptSHA512(user.getPassword());
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (logins, passwords) VALUES (?, ?);");
        statement.setString(1, login);
        statement.setString(2, password);
        statement.executeUpdate();
        statement.close();
    }

    public boolean checkUser(UserToken user) throws SQLException {
        Statement statement = connection.createStatement();
        String login = user.getLogin();
        String password = Encryptor.encryptSHA512(user.getPassword());
        ResultSet result = statement.executeQuery("SELECT * FROM users;");
        boolean f = false;
        while (result.next()) {
            if (login.equals(result.getString(1))) {
                if (password.equals(result.getString(2))){
                    f = true;
                } else {
                    return false;
                }
            }
        }
        statement.close();
        return f;
    }

    public int getLastID() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id FROM world");
        ResultSet result = statement.executeQuery();
        int id = 0;
        while (result.next()) {
            id = result.getInt(1);
        }
        statement.close();
        return id;
    }

    /*
    public void restartSeq(int ids) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("ALTER SEQUENCE id RESTART WITH "+ids+";");
        statement.executeUpdate();
        statement.close();
    }*/
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
