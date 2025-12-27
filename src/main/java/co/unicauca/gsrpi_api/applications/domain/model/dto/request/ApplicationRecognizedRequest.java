package co.unicauca.gsrpi_api.applications.domain.model.dto.request;

import java.time.LocalDateTime;

public class ApplicationRecognizedRequest {
    private String title;
    private String resolutionName;
    private LocalDateTime date;
    private String authors;
    private Long applicationId;
    //Relacion many to one con docente
    private TeacherRequest teacher;

    public ApplicationRecognizedRequest() {
    }

    public ApplicationRecognizedRequest(String title, String resolutionName, LocalDateTime date, String authors, Long applicationId, TeacherRequest teacher) {
        this.title = title;
        this.resolutionName = resolutionName;
        this.date = date;
        this.authors = authors;
        this.applicationId = applicationId;
        this.teacher = teacher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResolutionName() {
        return resolutionName;
    }

    public void setResolutionName(String resolutionName) {
        this.resolutionName = resolutionName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public TeacherRequest getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherRequest teacher) {
        this.teacher = teacher;
    }
}
