package com.acme.crm.application.service;

import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final EntityManager em;

  @Transactional(readOnly = true)
  public byte[] salesCsv(LocalDate from, LocalDate to) {
    @SuppressWarnings("unchecked")
    List<Object[]> rows =
        em.createNativeQuery(
                "select"
                    + " o.updated_at::date,o.name,coalesce(c.name,''),o.value,coalesce(u.name,''),o.status"
                    + " from opportunities o left join customers c on c.id=o.customer_id left join"
                    + " users u on u.id=o.owner_id where o.updated_at::date between :from and :to"
                    + " order by o.updated_at")
            .setParameter("from", from)
            .setParameter("to", to)
            .getResultList();
    var b = new StringBuilder("data,oportunidade,cliente,valor,vendedor,status\n");
    for (var r : rows)
      b.append(csv(r[0]))
          .append(',')
          .append(csv(r[1]))
          .append(',')
          .append(csv(r[2]))
          .append(',')
          .append(csv(r[3]))
          .append(',')
          .append(csv(r[4]))
          .append(',')
          .append(csv(r[5]))
          .append('\n');
    return b.toString().getBytes(StandardCharsets.UTF_8);
  }

  private String csv(Object v) {
    return "\"" + String.valueOf(v).replace("\"", "\"\"") + "\"";
  }
}
