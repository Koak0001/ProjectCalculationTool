package projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.User;
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
    @Test
    void testGetUser() {
        // arrange
        int userId = 2;

        // act
        User user = projectService.getUser(userId);

        // assert
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(userId);
    }

    @Test
    void testGetSubProject() {
        // arrange
        int subprojectId = 1;

        // act
        SubProject subproject = projectService.getSubProject(subprojectId);

        // assert
        assertThat(subproject).isNotNull();
        assertThat(subproject.getProjectId()).isEqualTo(subprojectId);
    }



}
