package com.example.demo.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
public class Theme
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="author_id")
    public User user;

    @NotBlank
    @Size(min = 1, max = 70)
    public String name;

    @ManyToMany
    @JoinTable (name="themes_posts",
            joinColumns=@JoinColumn (name="theme_id"),
            inverseJoinColumns=@JoinColumn(name="post_id"))
    private Set<Post> posts;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
