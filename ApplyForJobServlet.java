package JobPackage;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class ApplyForJobServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("ApplyForJobServlet: doPost started");

        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking";
        String USER = "root";
        String PASS = "";

        String jobIdStr = request.getParameter("jobSelect");
        System.out.println("Job ID selected: " + jobIdStr);
        
        int jobId = jobIdStr != null ? Integer.parseInt(jobIdStr) : -1;
        HttpSession session = request.getSession();
        String driverEmail = (String) session.getAttribute("driver");
        System.out.println("Driver email from session: " + driverEmail);
        
        if (driverEmail == null) {
            System.out.println("Driver email is null, redirecting to login");
            response.sendRedirect("login.html");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement driverStmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Fetch the driver ID using the email
            String fetchDriverIdSQL = "SELECT driver_id FROM drivers WHERE email = ?";
            driverStmt = conn.prepareStatement(fetchDriverIdSQL);
            driverStmt.setString(1, driverEmail);
            ResultSet rs = driverStmt.executeQuery();
            int driverId = -1;
            if (rs.next()) {
                driverId = rs.getInt("driver_id");
            }

            if (driverId == -1) {
                throw new ServletException("Driver ID not found");
            }

            // Inserting job application with status 'accepted'
            String insertTableSQL = "INSERT INTO job_applications (job_id, driver_id, status) VALUES (?, ?, 'accepted')";
            pstmt = conn.prepareStatement(insertTableSQL);
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, driverId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            response.sendRedirect("DashboardServlet");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ApplyForJobServlet: Exception occurred");
            e.printStackTrace();
            throw new ServletException("DB error", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (driverStmt != null) driverStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("ApplyForJobServlet: Error closing resources");
                e.printStackTrace();
            }
        }
    }
}
