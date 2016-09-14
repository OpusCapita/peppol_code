package com.opuscapita.peppol.support.ui.dao;

import com.sun.istack.Nullable;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.26.11
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public interface DAO<T> {
    void add(T t) throws HibernateException;

    List<T> getAll() throws HibernateException;

    @Nullable
    T getById(Integer id) throws HibernateException;

    void update(T t) throws HibernateException;

    void delete(T t) throws HibernateException;

    void delete(Integer id) throws HibernateException;

}
