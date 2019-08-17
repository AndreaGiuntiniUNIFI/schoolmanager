package apt.project.frontend.view.swing;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;

public class StudentPanel extends BasePanel<Student> {

    private StudentController controller;

    private JButton btnOpen;
    private JPanel cardsPanel;
    private ExamPanel examPanel;
    private CardLayout cardLayout;

    public StudentPanel(JPanel studentPanel, ExamPanel examPanel,
            MainFrame parentMainFrame, DialogManager dialogManager,
            String headerText) {

        super(studentPanel, parentMainFrame, dialogManager, headerText);

        this.examPanel = examPanel;

        btnAdd.addActionListener(e -> {
            String name = dialogManager.manageDialog("Name");
            if (name != null) {
                getController().newEntity(new Student(name));
            }
        });

        btnModify.addActionListener(e -> {
            Student selectedStudent = list.getSelectedValue();
            String name = dialogManager.manageDialog("Name",
                    selectedStudent.getName());
            if (name != null) {
                selectedStudent.merge(new Student(name));
                controller.updateEntity(selectedStudent);
            }
        });

        btnDelete.addActionListener(
                e -> controller.deleteEntity(list.getSelectedValue()));

        ActionListener switchPanel = e -> cardLayout.next(cardsPanel);

        cardsPanel = new JPanel();
        cardsPanel.setName("cardsPanel");
        cardsPanel.setLayout(new CardLayout());
        cardsPanel.add(studentPanel);
        cardLayout = (CardLayout) (cardsPanel.getLayout());
        cardsPanel.add(examPanel.getPanel());
        examPanel.getBtnBack().addActionListener(switchPanel);

        btnOpen = new JButton("Open");
        btnOpen.setEnabled(false);
        GridBagConstraints gbc_btnOpen = new GridBagConstraints();
        gbc_btnOpen.gridx = 3;
        gbc_btnOpen.gridy = 1;
        studentPanel.add(btnOpen, gbc_btnOpen);
        btnOpen.addActionListener(e -> {
            examPanel.setStudent(list.getSelectedValue());
            examPanel.showAll();
            switchPanel.actionPerformed(e);
        });

        list.addListSelectionListener(
                e -> btnOpen.setEnabled(list.getSelectedIndex() != -1));

    }

    public JPanel getCardsPanel() {
        return cardsPanel;
    }

    public JPanel getExamPanel() {
        return examPanel.getPanel();
    }

    public StudentController getController() {
        return controller;
    }

    public void setController(StudentController controller) {
        this.controller = controller;
    }

}
