package apt.project.frontend.view.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.controller.ExamController;
import apt.project.frontend.controller.ExamDialogController;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.swing.dialog.DialogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(mixinStandardHelpOptions = true)
public class SchoolManagerApp implements Callable<Void> {

    private static final Logger LOGGER = LogManager
            .getLogger(SchoolManagerApp.class);

    @Override
    public Void call() throws Exception {

        EventQueue.invokeLater(() -> {
            try {

                EntityManagerFactory entityManagerFactory = Persistence
                        .createEntityManagerFactory("H2");
                TransactionManager<Student> studentTransactionManager = new TransactionManager<>(
                        entityManagerFactory);

                TransactionManager<Course> courseTransactionManager = new TransactionManager<>(
                        entityManagerFactory);

                CourseRepository courseRepository = new CourseRepository(
                        courseTransactionManager);
                StudentRepository studentRepository = new StudentRepository(
                        studentTransactionManager);

                DialogManager dialogManager = new DialogManager();

                ExamPanel examPanel = new ExamPanel(new JPanel(), dialogManager,
                        "List of Exams");

                ExamController examController = new ExamController(examPanel,
                        studentRepository);

                ExamDialogController examDialogController = new ExamDialogController(
                        examPanel, courseRepository);

                examPanel.setExamDialogController(examDialogController);
                examPanel.setController(examController);

                StudentPanel studentPanel = new StudentPanel(new JPanel(),
                        examPanel, dialogManager, examController,
                        "List of Students");

                CoursePanel coursePanel = new CoursePanel(new JPanel(),
                        dialogManager, "List of Courses");

                StudentController studentController = new StudentController(
                        studentPanel, studentRepository);
                CourseController courseController = new CourseController(
                        coursePanel, courseRepository);

                studentPanel.setController(studentController);
                coursePanel.setController(courseController);

                SwingMainFrame frame = new SwingMainFrame(coursePanel,
                        studentPanel);

                examPanel.setMainFrame(frame);
                coursePanel.setMainFrame(frame);
                studentPanel.setMainFrame(frame);

                frame.setVisible(true);

            } catch (Exception e) {
                LOGGER.error(e);
            }
        });

        return null;
    }

    public static void main(String[] args) {
        new CommandLine(new SchoolManagerApp()).execute(args);
    }

}
