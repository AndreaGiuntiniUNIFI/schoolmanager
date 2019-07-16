package apt.project.frontend.controller;

import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class CourseController implements Controller{

    private View courseView;
    private Repository courseRepository;

    public CourseController(View courseView, Repository courseRepository) {
        this.courseView = courseView;
        this.courseRepository = courseRepository;
        
    }
    
    @Override
    public void allEntities() {
        courseView.showAll(courseRepository.findAll());
   }

}
