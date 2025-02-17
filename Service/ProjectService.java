package com.freelancer.Service;

import com.freelancer.Entity.Project;
import com.freelancer.Repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    private String directoryPath;

    public Project findById(long id) {
        Optional<Project> exist = projectRepository.findById(id);
        return exist.orElse(null);
    }

    public Project save(Project project) {
        if (project.getTitle() == null || project.getDescription() == null ||
            project.getSkill() == null || project.getRange() == null || project.getPeriod() == null) {
            logger.error("Cannot save project, missing required fields: {}", project);
            throw new IllegalArgumentException("Missing required fields");
        }

        logger.info("Saving project: {}", project);
        return projectRepository.save(project);
    }

    public Iterable<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Long uploadFile(MultipartFile file) {
        try {
            // Set up the directory to save the file
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();  // Create the directory if it doesn't exist
            }

            // Save the file with the original filename
            String filePath = directoryPath + "/" + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);  // Save the file

            // For simplicity, you could return the file path or a file ID
            // If you want to return a file ID, you could save the file metadata to a database
            return System.currentTimeMillis();  // Example: return the timestamp as an ID

        } catch (IOException e) {
            logger.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}