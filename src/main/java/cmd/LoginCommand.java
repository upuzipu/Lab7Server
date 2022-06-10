package cmd;

import client.Vehicles;
import collection.UserToken;
import connection.SQLManagerDB;
import ioManager.ConsoleManager;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginCommand {
    static Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());
    public static UserToken login(Vehicles env) {
        ConsoleManager.getInstance().write("Введите логин:");
        String login = ConsoleManager.getInstance().readline();
        ConsoleManager.getInstance().write("Введите пароль:");
        String password = String.valueOf(ConsoleManager.getInstance().readline());
        UserToken user = new UserToken(login, password);
        try {
            if (env.getManagerDB().checkUser(user)){
                env.getOut().writeln("Вы успешно авторизовались");
            }
            else{
                env.getOut().writeln("Вы ввели неправильный логин или пароль");
            }
        } catch (SQLException e) {
            LOGGER.warning("Ошибка доступа к бд"+e);
        }
        return user;
    }
    public static void register(SQLManagerDB managerDB){
        try {
            managerDB.registerUser(new UserToken(ConsoleManager.getInstance().readline(), String.valueOf(ConsoleManager.getInstance().readline())));
        } catch (SQLException e) {
            LOGGER.warning("Ошибка доступа к бд"+e);
        }
    }
}
