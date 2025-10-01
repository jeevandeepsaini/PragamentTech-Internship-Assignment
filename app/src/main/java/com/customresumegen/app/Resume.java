package com.customresumegen.app;

import java.util.List;

public class Resume {
    public String name;
    public String phone;
    public String email;
    public String twitter;
    public String address;
    public String summary;
    public List<String> skills;

    public static class Project {
        public String title;
        public String description;
        public String startDate;
        public String endDate;
    }

    public List<Project> projects;
}