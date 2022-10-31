package com.example.demo.repo;

import com.example.demo.models.Theme;
import org.springframework.data.repository.CrudRepository;

public interface ThemeRepository extends CrudRepository<Theme, Long> {

}