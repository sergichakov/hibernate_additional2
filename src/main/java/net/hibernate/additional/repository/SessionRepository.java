package net.hibernate.additional.repository;

import org.hibernate.SessionFactory;

public interface SessionRepository {
     SessionFactory getSession();

}
