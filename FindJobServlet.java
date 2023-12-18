package JobPackage;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FindJobsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FindJobsServlet: doGet started");

        // JDBC driver name and database URL
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking";

        // Database credentials
        String USER = "root";
        String PASS = "";

        HttpSession session = request.getSession();
        String driverEmail = (String) session.getAttribute("driver");

        if (driverEmail == null) {
            // Handle the case where the driverEmail is not set in the session
            response.sendRedirect("login.html");
            return;
        }

        Connection conn = null;
        PreparedStatement jobStmt = null;

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Fetch the filter parameters from the request
            String truckSize = request.getParameter("truckSize");
            String drivingRangeStr = request.getParameter("drivingRange");
            int drivingRange = drivingRangeStr != null ? Integer.parseInt(drivingRangeStr) : 0;

            // Fetch available jobs matching the truck type and within preferred distance
            String jobsQuery = "SELECT * FROM jobs WHERE required_truck_type = ? AND distance <= ?";
            jobStmt = conn.prepareStatement(jobsQuery);
            jobStmt.setString(1, truckSize);
            jobStmt.setInt(2, drivingRange);
            ResultSet jobsRs = jobStmt.executeQuery();

            List<Job> availableJobs = new ArrayList<>();
            while (jobsRs.next()) {
                Job job = new Job(
                        jobsRs.getInt("job_id"),
                        jobsRs.getString("description"),
                        jobsRs.getString("origin"),
                        jobsRs.getString("destination"),
                        jobsRs.getInt("distance"),
                        jobsRs.getDouble("pay"),
                        jobsRs.getString("required_truck_type")
                );
                availableJobs.add(job);
            }

            request.setAttribute("availableJobs", availableJobs);

            // Forward to job-listings.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("job-listings.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("FindJobsServlet: SQL or ClassNotFound Exception occurred");
            e.printStackTrace();
            throw new ServletException("SQL error", e);
        } finally {
            // Close resources
            try {
                if (jobStmt != null) jobStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("FindJobsServlet: Error closing resources");
                e.printStackTrace();
            }
        }
    }
}
