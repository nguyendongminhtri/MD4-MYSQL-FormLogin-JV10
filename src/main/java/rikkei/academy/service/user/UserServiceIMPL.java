package rikkei.academy.service.user;

import com.mysql.cj.Session;
import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserServiceIMPL implements IUserService {
    private Connection connection = ConnectMySQL.getConnection();
    private final String SELECT_ALL_USERNAME = "SELECT username FROM user";
    private final String SELECT_ALL_EMAIL = "SELECT email FROM user";
    private final String INSERT_INTO_USER = "INSERT INTO user(name, username, email, password) VALUES (?,?,?,?)";
    private final String INSERT_INTO_USER_ROLE = "INSERT INTO user_role(user_id, role_id) VALUES (?,?)";
    @Override
    public boolean existedByUsername(String username) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERNAME);
            List<String> listUsername = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                listUsername.add(resultSet.getString("username"));
            }
            for (int i = 0; i < listUsername.size(); i++) {
                if(username.equals(listUsername.get(i))){
                    return  true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean existedByEmail(String email) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_EMAIL);
            List<String> listEmail = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                listEmail.add(resultSet.getString("email"));
            }
            for (int i = 0; i < listEmail.size(); i++) {
                if(email.equals(listEmail.get(i))){
                    return  true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void save(User user) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getEmail());
            preparedStatement.setString(4,user.getPassword());
            preparedStatement.executeUpdate();
            int user_id = 0;
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                user_id = resultSet.getInt(1);
            }
            //SET user_id và role_id vào bảng trung gian user_role
            PreparedStatement preparedStatement1 = connection.prepareStatement(INSERT_INTO_USER_ROLE);
            Set<Role> roleSet = user.getRoleSet();
            List<Role> roleList = new ArrayList<>(roleSet);  //convert set --> list
            List<Integer> listRoleId = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                listRoleId.add(roleList.get(i).getId());
            }
            for (int i = 0; i < listRoleId.size(); i++) {
                preparedStatement1.setInt(1, user_id);
                preparedStatement1.setInt(2,listRoleId.get(i));
                preparedStatement1.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
