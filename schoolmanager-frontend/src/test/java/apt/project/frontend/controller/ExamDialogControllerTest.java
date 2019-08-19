package apt.project.frontend.controller;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.swing.ExamDialog;

public class ExamDialogControllerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ExamDialog dialog;

    @InjectMocks
    private ExamDialogController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

}
