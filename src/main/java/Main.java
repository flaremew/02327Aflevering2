import dal.IUserDAO;
import dal.UserDAOImpls185141;
import dal.dto.IUserDTO;
import dal.dto.UserDTO;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        IUserDAO connect = new UserDAOImpls185141();
        UserDTO user = new UserDTO();
        user.setUserId(114);
        user.setUserName("Hans Hansen");
        user.setIni("HH");
        ArrayList<String> roles = new ArrayList();
        roles.add("Operator");
        user.setRoles(roles);

        UserDTO user2 = new UserDTO();
        user2.setUserId(114);
        user2.setUserName("Hans Hansen EX");
        user2.setIni("HHX");
        ArrayList<String> roles2 = new ArrayList();
        roles2.add("Operator");
        roles2.add("Lærer");
        user2.setRoles(roles2);

        try {
            //connect.deleteUser(13);
            connect.createUser(user);
            System.out.println(connect.getUser(user.getUserId()));
            System.out.println(connect.getUserList());
            connect.updateUser(user2);
            System.out.println(connect.getUserList());
            connect.deleteUser(user.getUserId());
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }
}
