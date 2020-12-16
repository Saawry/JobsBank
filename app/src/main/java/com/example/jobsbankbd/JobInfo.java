package com.example.jobsbankbd;

public class JobInfo {
    String title, organization, deadline, vacancy, category, imgUrl;

    public JobInfo(){}

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JobInfo(String title, String organization, String deadline, String vacancy, String category){
        this.title=title;
        this.organization=organization;
        this.deadline = deadline;
        this.vacancy= vacancy;
        this.category = category;
    }
    public JobInfo(String title,String organization, String deadline, String vacancy, String category, String imgUrl){
        this.title=title;
        this.organization=organization;
        this.deadline = deadline;
        this.vacancy= vacancy;
        this.category = category;
        this.imgUrl = imgUrl;
    }
}
