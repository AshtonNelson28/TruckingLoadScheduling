<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="JobPackage.Job" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Job Listings</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        h1 {
            color: #333;
            text-align: center;
        }
        form {
            margin: 20px auto;
            width: 300px;
            align-items: center;
            justify-content: center;
            display: flex;
            flex-direction: column;
        }
        select {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
        }
        input[type=submit] {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        input[type=submit]:hover {
            background-color: #45a049;
        }
        label {
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <h1>Job Listings</h1>
    <!-- Form for filters -->
    <form action="FindJobsServlet" method="get">
        <label for="truckSize">Truck Size:</label>
        <select name="truckSize" id="truckSize">
            <option value="small">Small</option>
            <option value="medium">Medium</option>
            <option value="large">Large</option>
        </select>
        <label for="drivingRange">Driving Range (in miles):</label>
        <input type="number" name="drivingRange" id="drivingRange" value="100">
        <input type="submit" value="Filter Jobs">
    </form>
    <!-- List of jobs as a dropdown -->
    <form action="ApplyForJobServlet" method="post">
        <label for="jobSelect">Available Jobs:</label>
        <select name="jobSelect" id="jobSelect" size="5">
            <% 
                List<Job> availableJobs = (List<Job>) request.getAttribute("availableJobs");
                if (availableJobs != null && !availableJobs.isEmpty()) {
                    for (Job job : availableJobs) {
            %>
                        <option value="<%= job.getJobId() %>">
                            <%= job.getDescription() %> - Truck Type: <%= job.getRequiredTruckType() %>
                        </option>
            <% 
                    }
                } else { 
            %>
                    <option>No jobs available.</option>
            <% 
                } 
            %>
        </select>
        <input type="submit" value="Apply for Job">
    </form>
</body>
</html>
