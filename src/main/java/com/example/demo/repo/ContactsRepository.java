package com.example.demo.repo;

import com.example.demo.models.Contacts;
import org.springframework.data.repository.CrudRepository;

public interface ContactsRepository extends CrudRepository<Contacts, Long>
{
    Contacts findByEmail(String email);
    Contacts findByPhoneNumber(String phoneNumber);
}
