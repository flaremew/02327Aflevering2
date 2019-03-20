package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//TODO: test hele programmet
public class UserDAOImpls185141 implements IUserDAO {
    private Connection createConnection() throws SQLException {
        return  DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185141?"
                    + "user=s185141&password=SpvU4r25zUvbIEZ3CRn0U");
    }

    @Override
    public void createUser(IUserDTO user) throws DALException {
        List<String> list = user.getRoles();
        try(Connection conn = createConnection()) {
            PreparedStatement stmt = conn.prepareStatement("insert into Users2 values (?,?,?)");
            stmt.setInt(1,user.getUserId());
            stmt.setString(2,user.getUserName());
            stmt.setString(3,user.getIni());
            int Ind = stmt.executeUpdate();
            if (Ind == 0) {
                throw new DALException("ser ud til at noget gik galt");
            } else
                System.out.println("Du har oprettet en bruger :)");
            if (!list.isEmpty()){
                int rolesInd = 0;
                for (int i = 0; i < list.size(); i++){
                    PreparedStatement roleStmt = conn.prepareStatement("insert into Role2 values (?,?)");
                    roleStmt.setInt(1,user.getUserId());
                    roleStmt.setString(2,list.get(i));
                    rolesInd += roleStmt.executeUpdate();
                }
                if (rolesInd == 0){
                    System.out.println("Ser ud til at du ikke har nogen rolle");
                }
            } else {
                throw new DALException("Du har ikke indskrevet nogen roller i brugeren");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IUserDTO getUser(int userId) throws DALException {
        IUserDTO user = new UserDTO();
        try (Connection c = createConnection()){
            PreparedStatement prepstmt = c.prepareStatement("select * from Users2 where UserId = ?");
            prepstmt.setInt(1,userId);
            ResultSet get = prepstmt.executeQuery();
            if (get.first()){
                user.setUserId(userId);
                user.setUserName(get.getString(2));
                user.setIni(get.getString(3));
                PreparedStatement rolestmt = c.prepareStatement("select * from Role2 where Id = ?");
                rolestmt.setInt(1,userId);
                ResultSet roleget = rolestmt.executeQuery();
                while (roleget.next()){
                    user.addRole(roleget.getString(2));
                }
            } else {
                throw new DALException("Seems like the user doesn't exist");
            }
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
        return user;
    }



    @Override
    public List<IUserDTO> getUserList() throws DALException {
        List<IUserDTO> userList = new ArrayList<>();
        try (Connection c = createConnection()){
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("select UserId from Users2");
                while (rs.next()) {
                    userList.add(getUser(rs.getInt(1)));
                }
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
        return userList;
    }



    @Override
    public void updateUser(IUserDTO user) throws DALException {
        try(Connection c = createConnection()){
            PreparedStatement stmt = c.prepareStatement("update Users2 set UserName = ?, Ini = ? where UserId = ?");
            stmt.setString(1,user.getUserName());
            stmt.setString(2,user.getIni());
            stmt.setInt(3,user.getUserId());
            int get = stmt.executeUpdate();
            if (get == 0) {
                throw new DALException("Ser ud til at brugeren ikke eksistere");
            }
            if(user.getRoles().size() != 0){
                PreparedStatement delrolestmt = c.prepareStatement("delete from Role2 where Id = ?");
                delrolestmt.setInt(1,user.getUserId());
                int roledel = delrolestmt.executeUpdate();
                if (roledel == 0) {
                    throw new DALException("Ser ud til at der ikke er nogen roller assosieret med denne bruger");
                }
                for (int i = 0; i < user.getRoles().size(); i++){
                    PreparedStatement rolestmt = c.prepareStatement("insert into Role2 values (?,?)");
                    rolestmt.setInt(1,user.getUserId());
                    rolestmt.setString(2,user.getRoles().get(i));
                    int rs = rolestmt.executeUpdate();
                    if (rs == 0) {
                        throw new DALException("Ser ud til at denne bruger ikke havde nogen roller");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        try(Connection c = createConnection()){
            PreparedStatement rolestmt = c.prepareStatement("delete  from Role2 where Id = ?");
            rolestmt.setInt(1,userId);
            int roledel = rolestmt.executeUpdate();
            if (roledel == 0) {
                System.out.println("Ser ud til at der ikke er nogen roller assosieret med denne bruger");
            }
            PreparedStatement stmt = c.prepareStatement("delete from Users2 where UserId = ?");
            stmt.setInt(1,userId);
            int del = stmt.executeUpdate();
            if (del == 0) {
                throw new DALException("Ser ud til at brugeren ikke eksistere");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
