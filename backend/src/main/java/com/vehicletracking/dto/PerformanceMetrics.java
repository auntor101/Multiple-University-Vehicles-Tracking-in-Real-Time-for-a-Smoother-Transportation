package com.vehicletracking.dto;

public class PerformanceMetrics {
    private Double onTimePercentage;
    private Double averageRating;
    private Long totalPassengers;
    private Double safetyScore;
    
    public PerformanceMetrics() {}
    
    public Double getOnTimePercentage() { return onTimePercentage; }
    public void setOnTimePercentage(Double onTimePercentage) { this.onTimePercentage = onTimePercentage; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }
    
    public Double getSafetyScore() { return safetyScore; }
    public void setSafetyScore(Double safetyScore) { this.safetyScore = safetyScore; }
}
