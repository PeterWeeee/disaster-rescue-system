package vn.edu.cuunan.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class GeneralFundSummaryDto {

    private BigDecimal initialBaseFund;
    private BigDecimal totalDonationsReceived;
    private BigDecimal totalGrossFund;
    private BigDecimal totalDisbursedFunds;
    private BigDecimal availableGeneralFund;
    private long pendingRequestsCount;

    public GeneralFundSummaryDto() {
    }

    public GeneralFundSummaryDto(BigDecimal initialBaseFund,
                                 BigDecimal totalDonationsReceived,
                                 BigDecimal totalDisbursedFunds,
                                 long pendingRequestsCount) {
        this.initialBaseFund = initialBaseFund != null ? initialBaseFund : BigDecimal.ZERO;
        this.totalDonationsReceived = totalDonationsReceived != null ? totalDonationsReceived : BigDecimal.ZERO;
        this.totalGrossFund = this.initialBaseFund.add(this.totalDonationsReceived);
        this.totalDisbursedFunds = totalDisbursedFunds != null ? totalDisbursedFunds : BigDecimal.ZERO;
        this.availableGeneralFund = this.totalGrossFund.subtract(this.totalDisbursedFunds);
        if (this.availableGeneralFund.compareTo(BigDecimal.ZERO) < 0) {
            this.availableGeneralFund = BigDecimal.ZERO;
        }
        this.pendingRequestsCount = pendingRequestsCount;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        DecimalFormat df = new DecimalFormat("#,### ₫");
        return df.format(amount);
    }

    public String getFormattedInitialBaseFund() {
        return formatCurrency(initialBaseFund);
    }

    public String getFormattedTotalDonationsReceived() {
        return formatCurrency(totalDonationsReceived);
    }

    public String getFormattedTotalGrossFund() {
        return formatCurrency(totalGrossFund);
    }

    public String getFormattedTotalDisbursedFunds() {
        return formatCurrency(totalDisbursedFunds);
    }

    public String getFormattedAvailableGeneralFund() {
        return formatCurrency(availableGeneralFund);
    }

    public BigDecimal getInitialBaseFund() {
        return initialBaseFund;
    }

    public void setInitialBaseFund(BigDecimal initialBaseFund) {
        this.initialBaseFund = initialBaseFund;
    }

    public BigDecimal getTotalDonationsReceived() {
        return totalDonationsReceived;
    }

    public void setTotalDonationsReceived(BigDecimal totalDonationsReceived) {
        this.totalDonationsReceived = totalDonationsReceived;
    }

    public BigDecimal getTotalGrossFund() {
        return totalGrossFund;
    }

    public void setTotalGrossFund(BigDecimal totalGrossFund) {
        this.totalGrossFund = totalGrossFund;
    }

    public BigDecimal getTotalDisbursedFunds() {
        return totalDisbursedFunds;
    }

    public void setTotalDisbursedFunds(BigDecimal totalDisbursedFunds) {
        this.totalDisbursedFunds = totalDisbursedFunds;
    }

    public BigDecimal getAvailableGeneralFund() {
        return availableGeneralFund;
    }

    public void setAvailableGeneralFund(BigDecimal availableGeneralFund) {
        this.availableGeneralFund = availableGeneralFund;
    }

    public long getPendingRequestsCount() {
        return pendingRequestsCount;
    }

    public void setPendingRequestsCount(long pendingRequestsCount) {
        this.pendingRequestsCount = pendingRequestsCount;
    }
}
