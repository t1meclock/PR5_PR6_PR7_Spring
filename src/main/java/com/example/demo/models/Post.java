package com.example.demo.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Post {
    public Post(String title, User user, String anons, String full_text) {
        this.title = title;
        this.user = user;
        this.anons = anons;
        this.full_text = full_text;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Поле не может быть пустым")
    @NotBlank(message = "Поле не должно состоять из одних пробелов")
    @Size(min=2, max=50, message = "Размер данного поля должен быть в диапазоне от 2 до 50")
    private String title, anons, full_text;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    public User user;

    @NotNull
    @ManyToMany
    @JoinTable (name="themes_posts",
            joinColumns=@JoinColumn (name="post_id"),
            inverseJoinColumns=@JoinColumn(name="theme_id"))
    public Set<Theme> themes;

    private int views;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnons() {
        return anons;
    }

    public void setAnons(String anons) {
        this.anons = anons;
    }

    public String getFull_text() {
        return full_text;
    }

    public void setFull_text(String full_text) {
        this.full_text = full_text;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}