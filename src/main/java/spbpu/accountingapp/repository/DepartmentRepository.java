package spbpu.accountingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spbpu.accountingapp.entity.Department;
import spbpu.accountingapp.repository.projection.DepartmentSalary;

import java.util.Collection;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("""
        SELECT d
        FROM Department d
        LEFT JOIN FETCH d.employees
        ORDER BY d.name
        """)
    List<Department> getAllWithEmployees();

    @Query("""
        SELECT d
        FROM Department d
        LEFT JOIN FETCH d.employees
        ORDER BY d.name
        """)
    List<Department> getAllWithProjects();

    @Query("""
            SELECT d.id AS departmentId, COALESCE(SUM(e.salary), 0) AS totalSalary
            FROM Department d
            LEFT JOIN d.employees e
            WHERE d.id IN :departmentIds
            GROUP BY d.id
            """)
    List<DepartmentSalary> sumEmployeeSalaries(@Param("departmentIds") Collection<Long> departmentIds);
}