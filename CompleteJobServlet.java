// CompleteJobServlet.java
package JobPackage;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class CompleteJobServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking";
        String USER = "root";
        String PASS = "";

        HttpSession session = request.getSession();
        String driverEmail = (String) session.getAttribute("driver");

        int jobId = Integer.parseInt(request.getParameter("jobId"));

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

            String updateSQL = "UPDATE job_applications SET status = 'completed' WHERE job_id = ? AND driver_id = ?";
            pstmt = conn.prepareStatement(updateSQL);
            pstmt.setInt(1, jobId);
            pstmt.setInt(2, driverId);
            pstmt.executeUpdate();

            response.sendRedirect("DashboardServlet");
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("DB error", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (driverStmt != null) driverStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
