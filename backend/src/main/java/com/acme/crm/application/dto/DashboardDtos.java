package com.acme.crm.application.dto;

import java.math.BigDecimal;
import java.util.List;

public final class DashboardDtos {
  private DashboardDtos() {}

  public record SeriesPoint(String period, BigDecimal revenue, long won) {}

  public record View(
      long totalCustomers,
      long newLeads,
      long openOpportunities,
      long wonSales,
      BigDecimal forecastRevenue,
      double conversionRate,
      long overdueActivities,
      List<SeriesPoint> performance) {}
}
