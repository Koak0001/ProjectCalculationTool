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
        assertThat(projectService).isNotNull();
    }

    @Test
    void testGetProject() {
        // arrange
        int projectId = 1;

        // act
        Project project = projectService.getProject(projectId);

        // assert
        assertThat(project).isNotNull();
        assertThat(project.getProjectId()).isEqualTo(projectId);
    }
}
