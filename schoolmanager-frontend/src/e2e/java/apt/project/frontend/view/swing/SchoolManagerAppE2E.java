package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;

@RunWith(GUITestRunner.class)
public class SchoolManagerAppE2E extends AssertJSwingJUnitTestCase {

    private static final String COURSE_FIXTURE_1_TITLE = "HCI";
    private static final String COURSE_FIXTURE_2_TITLE = "APT";
    private static final String COURSE_FIXTURE_3_TITLE = "DDM";
    private static final String STUDENT_FIXTURE_1_NAME = "John";
    private static final String STUDENT_FIXTURE_2_NAME = "Jane";

    private static final String PERSISTENCE_UNIT_NAME = "POSTGRES";

    public static EntityManagerFactory emf;

    private EntityManager em;

    private FrameFixture window;

    private Exam exam1;
    private Course course1;
    private Course course2;

    private void removeTestStudentFromDatabase(String studentName) {
        em.getTransaction().begin();
        Student retrievedStudent = em
                .createQuery("from Student where name = :name ", Student.class)
                .setParameter("name", studentName).getSingleResult();
        em.remove(retrievedStudent);
        em.getTransaction().commit();
    }

    private void insertTestStudentInDatabase(String studentName) {
        em.getTransaction().begin();
        Student newStudent = new Student(studentName);
        em.persist(newStudent);
        em.getTransaction().commit();
    }

    private void removeTestCourseFromDatabase(String courseTitle) {
        em.getTransaction().begin();
        Course retrievedCourse = em
                .createQuery("from Course where title = :title ", Course.class)
                .setParameter("title", courseTitle).getSingleResult();
        em.remove(retrievedCourse);
        em.getTransaction().commit();
    }

    @BeforeClass
    public static void onSetUpClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @Override
    protected void onSetUp() {
        em = emf.createEntityManager();

        // Clear tables
        em.getTransaction().begin();
        em.createNativeQuery("TRUNCATE TABLE Exam, Course, Student CASCADE")
                .executeUpdate();
        em.getTransaction().commit();

        // Re-populate database
        course1 = new Course(COURSE_FIXTURE_1_TITLE);
        course2 = new Course(COURSE_FIXTURE_2_TITLE);
        Student student = new Student(STUDENT_FIXTURE_1_NAME);

        exam1 = new Exam(course1, 23);
        student.addExam(exam1);

        em.getTransaction().begin();
        em.persist(course1);
        em.persist(course2);
        em.persist(student);
        em.getTransaction().commit();

        // Launch application
        application("apt.project.frontend.view.swing.SchoolManagerApp")
                .withArgs("--persistence-unit-name=" + PERSISTENCE_UNIT_NAME)
                .start();

        window = WindowFinder.findFrame(
                new GenericTypeMatcher<SwingMainFrame>(SwingMainFrame.class) {

                    @Override
                    protected boolean isMatching(SwingMainFrame frame) {
                        return "SchoolManagerApp".equals(frame.getTitle())
                                && frame.isShowing();
                    }
                }).using(robot());

    }

    @Test
    @GUITest
    public void onStartAllDatabaseElementAreShown() {
        // setup
        window.tabbedPane().selectTab("Courses");

        // verify
        assertThat(window.list().contents())
                .anySatisfy(e -> assertThat(e).contains(COURSE_FIXTURE_1_TITLE))
                .anySatisfy(
                        e -> assertThat(e).contains(COURSE_FIXTURE_2_TITLE));

        // setup
        window.tabbedPane().selectTab("Students");

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(STUDENT_FIXTURE_1_NAME));

        // setup
        window.list().selectItem(0);
        window.button(JButtonMatcher.withText("Open")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_1_TITLE, "23"));
    }

    @Test
    @GUITest
    public void testStudentAddButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");

        // exercise
        window.button(JButtonMatcher.withText("Add").andShowing()).click();
        window.dialog().textBox("NameTextField")
                .enterText(STUDENT_FIXTURE_2_NAME);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(STUDENT_FIXTURE_2_NAME));
    }

    @Test
    @GUITest
    public void testStudentAddButtonError() {
        // setup
        window.tabbedPane().selectTab("Students");

        // exercise
        window.button(JButtonMatcher.withText("Add").andShowing()).click();
        window.dialog().textBox("NameTextField")
                .enterText(STUDENT_FIXTURE_1_NAME);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(STUDENT_FIXTURE_1_NAME);
    }

    @Test
    @GUITest
    public void testStudentDeleteButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.list().contents())
                .noneMatch(e -> e.contains(STUDENT_FIXTURE_1_NAME));
    }

    @Test
    @GUITest
    public void testStudentDeleteButtonError() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        removeTestStudentFromDatabase(STUDENT_FIXTURE_1_NAME);

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(STUDENT_FIXTURE_1_NAME);
    }

    @Test
    @GUITest
    public void testStudentModifyButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().textBox("NameTextField").deleteText()
                .enterText(STUDENT_FIXTURE_2_NAME);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(STUDENT_FIXTURE_2_NAME));
    }

    @Test
    @GUITest
    public void testStudentModifyButtonError() {
        // setup
        window.tabbedPane().selectTab("Students");
        insertTestStudentInDatabase(STUDENT_FIXTURE_2_NAME);
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().textBox("NameTextField").deleteText()
                .enterText(STUDENT_FIXTURE_2_NAME);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(STUDENT_FIXTURE_2_NAME);
    }

    @Test
    @GUITest
    public void testCourseAddButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Courses");

        // exercise
        window.button(JButtonMatcher.withText("Add").andShowing()).click();
        window.dialog().textBox("TitleTextField")
                .enterText(COURSE_FIXTURE_3_TITLE);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_3_TITLE));
    }

    @Test
    @GUITest
    public void testCourseAddButtonError() {
        // setup
        window.tabbedPane().selectTab("Courses");

        // exercise
        window.button(JButtonMatcher.withText("Add").andShowing()).click();
        window.dialog().textBox("TitleTextField")
                .enterText(COURSE_FIXTURE_2_TITLE);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(COURSE_FIXTURE_2_TITLE);
    }

    @Test
    @GUITest
    public void testCourseDeleteButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Courses");
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.list().contents())
                .noneMatch(e -> e.contains(COURSE_FIXTURE_1_TITLE));
    }

    @Test
    @GUITest
    public void testCourseDeleteButtonError() {
        // setup
        window.tabbedPane().selectTab("Courses");
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));
        removeTestCourseFromDatabase(COURSE_FIXTURE_1_TITLE);

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(COURSE_FIXTURE_1_TITLE);
    }

    @Test
    @GUITest
    public void testCourseModifyButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Courses");
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().textBox("TitleTextField").deleteText()
                .enterText(COURSE_FIXTURE_3_TITLE);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_3_TITLE));
    }

    @Test
    @GUITest
    public void testCourseModifyButtonError() {
        // setup
        window.tabbedPane().selectTab("Courses");
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().textBox("TitleTextField").deleteText()
                .enterText(COURSE_FIXTURE_2_TITLE);
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_2_TITLE));
    }

    @Test
    @GUITest
    public void testExamAddButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        window.button(JButtonMatcher.withText("Open").andShowing()).click();

        // exercise
        window.button(JButtonMatcher.withText("Add").andShowing()).click();
        window.dialog().comboBox("rateComboBox").selectItem("25");
        window.dialog().comboBox("examComboBox").selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_2_TITLE + ".*"));
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_2_TITLE));
    }

    @Test
    @GUITest
    public void testExamDeleteButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        window.button(JButtonMatcher.withText("Open").andShowing()).click();
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.list().contents())
                .noneMatch(e -> e.contains(COURSE_FIXTURE_1_TITLE));
    }

    @Test
    @GUITest
    public void testExamDeleteButtonError() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        window.button(JButtonMatcher.withText("Open").andShowing()).click();
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));
        removeTestCourseFromDatabase(COURSE_FIXTURE_1_TITLE);

        // exercise
        window.button(JButtonMatcher.withText("Delete").andShowing()).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(COURSE_FIXTURE_1_TITLE);
    }

    @Test
    @GUITest
    public void testExamModifyButtonSuccess() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        window.button(JButtonMatcher.withText("Open").andShowing()).click();
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().comboBox("rateComboBox").selectItem("25");
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.list().contents()).anySatisfy(
                e -> assertThat(e).contains(COURSE_FIXTURE_1_TITLE, "25"));
    }

    @Test
    @GUITest
    public void testExamModifyButtonError() {
        // setup
        window.tabbedPane().selectTab("Students");
        window.list().selectItem(
                Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
        window.button(JButtonMatcher.withText("Open").andShowing()).click();
        window.list().selectItem(
                Pattern.compile(".*" + COURSE_FIXTURE_1_TITLE + ".*"));
        removeTestCourseFromDatabase(COURSE_FIXTURE_1_TITLE);

        // exercise
        window.button(JButtonMatcher.withText("Modify").andShowing()).click();
        window.dialog().comboBox("rateComboBox").selectItem("25");
        window.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(window.label("errorLabel").text())
                .contains(COURSE_FIXTURE_1_TITLE);
    }

}
