package com.openclassrooms.starterjwt.session.repository;

import com.openclassrooms.starterjwt.session.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
}
