package spbpu.accountingapp.dto;

import java.math.BigDecimal;
import spbpu.accountingapp.enums.ProfitStatus;

public record ProjectProfitInfo(BigDecimal profit, ProfitStatus status) {
}

