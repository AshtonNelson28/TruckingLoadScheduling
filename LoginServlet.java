import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 System.out.println("LoginServlet: doPost started");
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking"; // Change to your database name

        // Database credentials
        String USER = "root"; 
        String PASS = ""; // Update with your database password

        // User login data
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        System.out.println("Login attempt with email: " + email);

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            String query = "SELECT * FROM drivers WHERE email = ? AND password = ?";
            statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
            	System.out.println("Login successful for: " + email); 
                HttpSession session = request.getSession();
                session.setAttribute("driver", email); // Or store driver object
                response.sendRedirect("DashboardServlet");
            } else {
                // User is invalid, handle login failure
                response.sendRedirect("truck_registration.html");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("SQL error", e);
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
