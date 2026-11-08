package com.serendib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userEmail;
    private String customerName;
    private String phone;

    private String bookingType;
    private Long itemId;
    private String itemName;
    private String optionName;

    private String startDate;
    private String endDate;
    private Integer guests = 1;
    private Integer quantity = 1;
    private Double unitPrice = 0.0;
    private Double total = 0.0;

    private String status = "PENDING_PAYMENT";
    private String paymentMethod = "UNPAID";
    private String paymentStatus = "PENDING";
    private String paymentSlipUrl;
    private String paymentSlipFile;
    private String transactionReference;
    private String customerNote;
    private String adminNote;

    public Booking() {
        super();
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getBookingType() {
        return bookingType;
    }
    public void setBookingType(String bookingType) {
        this.bookingType = bookingType == null ? null : bookingType.toUpperCase();
    }
    public Long getItemId() {
        return itemId;
    }
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getOptionName() {
        return optionName;
    }
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public Integer getGuests() {
        return guests != null ? guests : 1;
    }
    public void setGuests(Integer guests) {
        this.guests = guests == null || guests < 1 ? 1 : guests;
    }
    public Integer getQuantity() {
        return quantity != null ? quantity : 1;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null || quantity < 1 ? 1 : quantity;
    }
    public Double getUnitPrice() {
        return unitPrice != null ? unitPrice : 0.0;
    }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice == null || unitPrice < 0 ? 0.0 : unitPrice;
    }
    public Double getTotal() {
        return total != null ? total : 0.0;
    }
    public void setTotal(Double total) {
        this.total = total == null || total < 0 ? 0.0 : total;
    }
    public String getStatus() {
        return status != null ? status : "PENDING_PAYMENT";
    }
    public void setStatus(String status) {
        this.status = status == null ? "PENDING_PAYMENT" : status.toUpperCase();
    }
    public String getPaymentMethod() {
        return paymentMethod != null ? paymentMethod : "UNPAID";
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod == null ? "UNPAID" : paymentMethod.toUpperCase();
    }
    public String getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : "PENDING";
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus == null ? "PENDING" : paymentStatus.toUpperCase();
    }
    public String getPaymentSlipUrl() {
        return paymentSlipUrl;
    }
    public void setPaymentSlipUrl(String paymentSlipUrl) {
        this.paymentSlipUrl = paymentSlipUrl;
    }
    public String getPaymentSlipFile() {
        return paymentSlipFile;
    }
    public void setPaymentSlipFile(String paymentSlipFile) {
        this.paymentSlipFile = paymentSlipFile;
    }
    public String getTransactionReference() {
        return transactionReference;
    }
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
    public String getCustomerNote() {
        return customerNote;
    }
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }
    public String getAdminNote() {
        return adminNote;
    }
    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
