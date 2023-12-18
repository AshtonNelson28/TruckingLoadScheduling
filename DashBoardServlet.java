package JobPackage;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DashboardServlet: doGet started");

        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/trucking";
        String USER = "root";
        String PASS = "";

        HttpSession session = request.getSession();
        String driverEmail = (String) session.getAttribute("driver");
        System.out.println("Fetching dashboard for driver email: " + driverEmail);

        Connection conn = null;
        PreparedStatement driverStmt = null;
        PreparedStatement truckStmt = null;
        PreparedStatement acceptedJobsStmt = null;
        PreparedStatement completedJobsStmt = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            String driverQuery = "SELECT * FROM drivers WHERE email = ?";
            driverStmt = conn.prepareStatement(driverQuery);
            driverStmt.setString(1, driverEmail);
            ResultSet rs = driverStmt.executeQuery();

            if (rs.next()) {
                int driverId = rs.getInt("driver_id");
                request.setAttribute("driverName", rs.getString("name"));
                request.setAttribute("driverEmail", rs.getString("email"));

                String truckQuery = "SELECT * FROM trucks WHERE driver_id = ?";
                truckStmt = conn.prepareStatement(truckQuery);
                truckStmt.setInt(1, driverId);
                ResultSet truckRs = truckStmt.executeQuery();

                if (truckRs.next()) {
                    request.setAttribute("truckType", truckRs.getString("truck_type"));
                    request.setAttribute("licensePlate", truckRs.getString("license_plate"));
                }

                // Fetch accepted jobs
                String acceptedJobsQuery = "SELECT jobs.*, job_applications.* FROM jobs JOIN job_applications ON jobs.job_id = job_applications.job_id WHERE job_applications.driver_id = ? AND job_applications.status = 'accepted'";
                acceptedJobsStmt = conn.prepareStatement(acceptedJobsQuery);
                acceptedJobsStmt.setInt(1, driverId);
                ResultSet acceptedJobsRs = acceptedJobsStmt.executeQuery();

                List<Job> acceptedJobs = new ArrayList<>();
                while (acceptedJobsRs.next()) {
                    acceptedJobs.add(extractJob(acceptedJobsRs));
                }
                request.setAttribute("acceptedJobs", acceptedJobs);

                // Fetch completed jobs
                String completedJobsQuery = "SELECT jobs.*, job_applications.* FROM jobs JOIN job_applications ON jobs.job_id = job_applications.job_id WHERE job_applications.driver_id = ? AND job_applications.status = 'completed'";
                completedJobsStmt = conn.prepareStatement(completedJobsQuery);
                completedJobsStmt.setInt(1, driverId);
                ResultSet completedJobsRs = completedJobsStmt.executeQuery();

                List<Job> completedJobs = new ArrayList<>();
                while (completedJobsRs.next()) {
                    completedJobs.add(extractJob(completedJobsRs));
                }
                request.setAttribute("completedJobs", completedJobs);

                RequestDispatcher dispatcher = request.getRequestDispatcher("Dashboard.jsp");
                dispatcher.forward(request, response);
            } else {
                System.out.println("No driver found with email: " + driverEmail);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("DashboardServlet: SQL or ClassNotFound Exception occurred");
            e.printStackTrace();
            throw new ServletException("SQL error", e);
        } finally {
            closeResources(conn, driverStmt, truckStmt, acceptedJobsStmt, completedJobsStmt);
        }
    }

    private Job extractJob(ResultSet rs) throws SQLException {
        return new Job(
            rs.getInt("job_id"),
            rs.getString("description"),
            rs.getString("origin"),
            rs.getString("destination"),
            rs.getInt("distance"),
            rs.getDouble("pay"),
            rs.getString("required_truck_type")
        );
    }

    private void closeResources(Connection conn, PreparedStatement... statements) {
        for (PreparedStatement stmt : statements) {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
