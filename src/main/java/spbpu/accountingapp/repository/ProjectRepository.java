package spbpu.accountingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spbpu.accountingapp.entity.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p LEFT JOIN p.department d ORDER BY d.name, p.dateBeg")
    List<Project> findAllByOrderByDepartmentNameAscDateBegAsc();

    List<Project> findByDepartmentIdOrderByDateBeg(Long departmentId);
}