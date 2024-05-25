package projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import projectcalculationtool.model.Project;
import projectcalculationtool.service.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectCalculationToolApplicationTests {

    @Autowired
    private ProjectService projectService;

    @Test
    void contextLoads() {
        // Verifies that the application context loads successfully
    }

    @Test
    void testGetProject() {
        // Given
        int projectId = 1;

        // When
        Project project = projectService.getProject(projectId);

        // Then
        assertThat(project).isNotNull();
        assertThat(project.getProjectId()).isEqualTo(projectId);
    }
}
