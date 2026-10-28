package com.serendib.model.dto;

/** Simple object for the numbers shown on the admin dashboard. */
public class AdminStatsDto {
    private long totalPackages;
    private long totalDestinations;
    private long totalHotels;
    private long totalTransports;
    private long totalUsers;
    private long totalBookings;
    private long activeListings;

    public AdminStatsDto() {}

    public AdminStatsDto(long packages, long destinations, long hotels, long transports, long users) {
        totalPackages = packages;
        totalDestinations = destinations;
        totalHotels = hotels;
        totalTransports = transports;
        totalUsers = users;
        activeListings = packages + destinations + hotels + transports;
    }

    public long getTotalPackages() { return totalPackages; }
    public void setTotalPackages(long value) { totalPackages = value; }
    public long getTotalDestinations() { return totalDestinations; }
    public void setTotalDestinations(long value) { totalDestinations = value; }
    public long getTotalHotels() { return totalHotels; }
    public void setTotalHotels(long value) { totalHotels = value; }
    public long getTotalTransports() { return totalTransports; }
    public void setTotalTransports(long value) { totalTransports = value; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long value) { totalUsers = value; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long value) { totalBookings = value; }
    public long getActiveListings() { return activeListings; }
    public void setActiveListings(long value) { activeListings = value; }
}
