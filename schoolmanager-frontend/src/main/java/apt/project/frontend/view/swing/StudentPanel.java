package apt.project.frontend.view.swing;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import apt.project.backend.domain.Student;
import apt.project.frontend.view.MainFrame;

public class StudentPanel extends BasePanel<Student> {

    private JButton btnOpen;
    private JPanel cardsPanel;
    private JPanel examPanel;
    private CardLayout cardLayout;

    public StudentPanel(JPanel studentPanel, JPanel examPanel,
            MainFrame parentMainFrame, DialogManager dialogManager,
            String headerText) {

        super(studentPanel, parentMainFrame, dialogManager, headerText);

        this.examPanel = examPanel;

        btnAdd.addActionListener(e -> {
            String name = dialogManager.manageDialog("Name");
            if (name != null) {
                controller.newEntity(new Student(name));
            }
        });

        btnModify.addActionListener(e -> {
            Student selectedStudent = list.getSelectedValue();
            String name = dialogManager.manageDialog("Name",
                    selectedStudent.getName());
            if (name != null) {
                controller.updateEntity(selectedStudent, new Student(name));
            }
        });

        btnDelete.addActionListener(
                e -> controller.deleteEntity(list.getSelectedValue()));

        examPanel.setName("examPanel");
        // JButton btnBack = new JButton("Back");
        // examPanel.add(btnBack);
        ActionListener switchPanel = e -> cardLayout.next(cardsPanel);
        // btnBack.addActionListener(switchPanel);

        cardsPanel = new JPanel();
        cardsPanel.setName("cardsPanel");
        cardsPanel.setLayout(new CardLayout());
        cardsPanel.add(studentPanel);
        cardLayout = (CardLayout) (cardsPanel.getLayout());
        cardsPanel.add(examPanel);

        btnOpen = new JButton("Open");
        btnOpen.setEnabled(false);
        GridBagConstraints gbc_btnOpen = new GridBagConstraints();
        gbc_btnOpen.gridx = 3;
        gbc_btnOpen.gridy = 1;
        studentPanel.add(btnOpen, gbc_btnOpen);
        btnOpen.addActionListener(switchPanel);

        list.addListSelectionListener(e -> {
            boolean enable = list.getSelectedIndex() != -1;
            btnOpen.setEnabled(enable);
        });

    }

    public JPanel getCardsPanel() {
        return cardsPanel;
    }

    public JPanel getExamPanel() {
        return examPanel;
    }

}
