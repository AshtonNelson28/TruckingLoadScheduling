import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class TruckingRegistrationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // JDBC driver name and database URL
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking"; // Change to your database name

        // Database credentials
        String USER = "root";
        String PASS = ""; // Update with your database password

        // User data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); // Consider hashing the password

        // Truck data
        String truckType = request.getParameter("truckType");
        String truckPlate = request.getParameter("truckPlate");
        int capacity = Integer.parseInt(request.getParameter("capacity"));

        Connection conn = null;
        PreparedStatement driverStmt = null;
        PreparedStatement truckStmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Begin transaction
            conn.setAutoCommit(false);

            //STEP 4: Execute a query - Insert user data into 'drivers' table
            String insertDriver = "INSERT INTO drivers (name, email, password) VALUES (?, ?, ?)";
            driverStmt = conn.prepareStatement(insertDriver, Statement.RETURN_GENERATED_KEYS);
            driverStmt.setString(1, name);
            driverStmt.setString(2, email);
            driverStmt.setString(3, password); // Store hashed password
            int affectedRows = driverStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Get the driver ID
            int driverId;
            try (ResultSet generatedKeys = driverStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    driverId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // Insert truck data into 'trucks' table and link it to the driver
            String insertTruck = "INSERT INTO trucks (truck_type, license_plate, capacity, driver_id) VALUES (?, ?, ?, ?)";
            truckStmt = conn.prepareStatement(insertTruck);
            truckStmt.setString(1, truckType);
            truckStmt.setString(2, truckPlate);
            truckStmt.setInt(3, capacity);
            truckStmt.setInt(4, driverId);
            truckStmt.executeUpdate();

            // Commit transaction
            conn.commit();

            // Initialize user session with new account information
            HttpSession session = request.getSession();
            session.setAttribute("driver", email); // Set the driver's email in the session

            // Redirect to the DashboardServlet to display the user dashboard
            response.sendRedirect("DashboardServlet"); // Note: Redirect to the servlet, not JSP

        } catch (SQLException | ClassNotFoundException e) {
            // Rollback transaction if exception occurs
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
            throw new ServletException("Database error", e);
        } finally {
            // Reset auto-commit to true and close resources
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    if (driverStmt != null) driverStmt.close();
                    if (truckStmt != null) truckStmt.close();
                    conn.close();
                } catch (SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
}
