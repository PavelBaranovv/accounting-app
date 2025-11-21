package spbpu.accountingapp.repository.projection;

import java.math.BigDecimal;

public interface DepartmentSalary {
    Long getDepartmentId();
    BigDecimal getTotalSalary();
}

