<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="JobPackage.Job" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Driver Dashboard</title>
    <!-- Add CSS and JS files as needed -->
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .find-jobs-btn button {
            background-color: blue;
            color: white;
            border: none;
            padding: 10px 20px;
            text-transform: uppercase;
            font-weight: bold;
            cursor: pointer;
        }
        .job-card {
            border: 1px solid #ddd;
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 5px;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }
        .action-buttons form {
            margin: 0;
        }
        button {
            padding: 5px 10px;
            cursor: pointer;
        }
        h2 {
            color: #333;
        }
        .company-logo {
    position: absolute;
    top: 0; /* Move up to the top edge of the parent container */
    right: 0; /* Move to the right edge of the parent container */
    transform: scale(0.75);
}
    </style>
</head>
<body>
    <div class="find-jobs-btn">
        <form action="FindJobsServlet" method="get">
            <button type="submit">Find Jobs</button>
        </form>
    </div>
    <h1>Driver Dashboard</h1>

    <%-- User Information --%>
    <h2>Welcome, <%= request.getAttribute("driverName") %></h2>
    <p>Email: <%= request.getAttribute("driverEmail") %></p>
    <p>Truck Type: <%= request.getAttribute("truckType") %></p>
    <p>License Plate: <%= request.getAttribute("licensePlate") %></p>

    <div class="company-logo">
            <img src="images/OIP.jpg" alt="Company Logo">
    </div>
    <%-- Accepted Job Listings --%>
    <h2>Your Accepted Job Applications</h2>
    <% 
    List<Job> acceptedJobs = (List<Job>) request.getAttribute("acceptedJobs");
    if(acceptedJobs != null){
        for (Job job : acceptedJobs) { 
    %>
        <div class="job-card">
            <p>ID: <%= job.getJobId() %>, Description: <%= job.getDescription() %></p>
            <p>Origin: <%= job.getOrigin() %>, Destination: <%= job.getDestination() %></p>
            <p>Distance: <%= job.getDistance() %>, Pay: <%= job.getPay() %></p>
            <p>Required Truck Type: <%= job.getRequiredTruckType() %></p>
            <div class="action-buttons">
                <form action="CompleteJobServlet" method="post">
                    <input type="hidden" name="jobId" value="<%= job.getJobId() %>" />
                    <button type="submit">Complete Job</button>
                </form>
                <form action="RemoveJobServlet" method="post">
                    <input type="hidden" name="jobId" value="<%= job.getJobId() %>" />
                    <button type="submit">Remove Job</button>
                </form>
            </div>
        </div>
    <% 
        } 
    } else { 
    %>
        <p>No accepted job applications found.</p>
    <% 
    } 
    %>

    <%-- Completed Jobs --%>
    <h2>Completed Jobs</h2>
    <% 
    List<Job> completedJobs = (List<Job>) request.getAttribute("completedJobs");
    if(completedJobs != null && !completedJobs.isEmpty()){
        for(Job job : completedJobs){
    %>
        <div class="job-card">
            <p>ID: <%= job.getJobId() %>, Description: <%= job.getDescription() %></p>
            <p>Origin: <%= job.getOrigin() %>, Destination: <%= job.getDestination() %></p>
            <p>Distance: <%= job.getDistance() %>, Pay: <%= job.getPay() %></p>
            <p>Required Truck Type: <%= job.getRequiredTruckType() %></p>
        </div>
    <%
        }
    } else {
    %>
        <p>No completed jobs found.</p>
    <%
    }
    %>
    <!-- Rest of the existing content -->
</body>
</html>
