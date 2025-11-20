package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.entity.Project;
import spbpu.accountingapp.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByName();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public List<Project> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findByDepartmentId(departmentId);
    }
}