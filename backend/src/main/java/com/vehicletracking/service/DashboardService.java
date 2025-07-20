package com.vehicletracking.service;

import com.vehicletracking.dto.*;
import com.vehicletracking.model.Role;

import java.time.LocalDate;

public interface DashboardService {
    
    /**
     * Get dashboard statistics for admin
     */
    AdminDashboardStats getAdminDashboardStats();
    
    /**
     * Get dashboard statistics for driver
     */
    DriverDashboardStats getDriverDashboardStats(String driverId);
    
    /**
     * Get vehicle usage statistics
     */
    VehicleUsageStats getVehicleUsageStats(String vehicleId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get system overview for admin
     */
    SystemOverview getSystemOverview();
    
    /**
     * Get fleet performance metrics
     */
    FleetPerformanceMetrics getFleetPerformanceMetrics();
    
    /**
     * Get user activity statistics
     */
    UserActivityStats getUserActivityStats(Role role);
    
    /**
     * Get trip analytics
     */
    TripAnalytics getTripAnalytics(LocalDate startDate, LocalDate endDate);
}
