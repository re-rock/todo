package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.demo.domain.Task;
import com.example.demo.form.TaskForm;
import com.example.demo.service.TodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TodoWebController{

    private static final String TASKS = "tasks";
    private static final String REDIRECT_TO = "redirect:/" + TASKS;

    @Autowired
    TodoService todoService;

    // get all tasks
    @GetMapping(value="/tasks")
    public ModelAndView readAllTasks() {
        TaskForm form = createInitialForm();

        ModelAndView modelAndView = toTasksPage();
        modelAndView.addObject("form", form);
        List<Task> tasks = todoService.findAllTasks();
        modelAndView.addObject(TASKS, tasks);

        return modelAndView;
    }

    private ModelAndView toTasksPage() {
        return new ModelAndView(TASKS);
    }

    private TaskForm createInitialForm() {
        String formSubject = "";
        LocalDate formDeadLine = LocalDate.now();
        Boolean hasDone = false;
        Boolean isNewTask = true;
        return new TaskForm(formSubject, formDeadLine, hasDone, isNewTask);
    }
    // create one task
    @PostMapping(value="/tasks")
    public ModelAndView createOneTask(@ModelAttribute TaskForm form) {
        createTaskFromForm(form);
        return new ModelAndView(REDIRECT_TO);
    }

    private void createTaskFromForm(TaskForm form){
        String subject = form.getSubject();
        LocalDate deadLine = form.getDeadLine();
        Boolean hasDone = form.getHasDone();
        Task task = new Task(subject, deadLine, hasDone);
        todoService.createTask(task);
    }

    // read one task
    @GetMapping(value="/tasks/{id}")
    public ModelAndView readOneTask(@PathVariable Integer id) {
        Optional<TaskForm> form = readTaskFromId(id);
        if (!form.isPresent()) {
            return new ModelAndView(REDIRECT_TO);
        }
    ModelAndView modelAndView = toTasksPage();
    modelAndView.addObject("taskId", id);
    modelAndView.addObject("form", form.get());
    List<Task> tasks = todoService.findAllTasks();
    modelAndView.addObject(TASKS, tasks);
    return modelAndView;
    }

    private Optional<TaskForm> readTaskFromId(Integer id) {
        Optional<Task> task = todoService.findOneTask(id);
        if (!task.isPresent()) {
            return Optional.ofNullable(null);
        }
        String formSubject = task.get().getSubject();
        LocalDate formDeadLine = task.get().getDeadLine();
        Boolean hasDone = task.get().getHasDone();
        Boolean isNewTask = false;
        TaskForm form = new TaskForm(formSubject, formDeadLine, hasDone, isNewTask);
        return Optional.ofNullable(form);
    }

    // update one task
    @PutMapping(value = "/tasks/{id}")
    public ModelAndView updateOneTask(@PathVariable Integer id, @ModelAttribute TaskForm form) {
        updateTask(id, form);
        return new ModelAndView(REDIRECT_TO);
    }

    private void updateTask(Integer id, TaskForm form) {
        String subject = form.getSubject();
        LocalDate deadLine = form.getDeadLine();
        Boolean hasDone = form.getHasDone();
        Task task = new Task(id, subject, deadLine, hasDone);
        todoService.updateTask(task);
    }

    // delete one task
    @DeleteMapping(value = "/tasks/{id}")
    public ModelAndView deleteOneTask(@PathVariable Integer id) {
        deleteTask(id);
        return new ModelAndView(REDIRECT_TO);
    }

    private void deleteTask(Integer id) {
        Optional<Task>  task = todoService.findOneTask(id);
        if (task.isPresent()) {
            todoService.deleteTask(id);
        }
    }
}