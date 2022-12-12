import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class dataTest {

    public Connection connect(){
        Connection con = null;

        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/EmailsDB.db");
            // verify by text + verify INSERT;
            if (con != null){
                System.out.println("Connected");
            }
        } catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    public void subscribe(String email){

        //int id = getId();
        Connection con= connect();
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO Emails VALUES (?);";

            statement = con.prepareStatement(sql);

            //statement.setInt(1,id);
            statement.setString(1,email);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        dataTest T = new dataTest();
        T.subscribe("Jounta@email.com");
    }
}
