package com.opuscapita.peppol.support.ui.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.27.11
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public interface Service<T> {
    public void add(T t);

    public List<T> getAll();

    public T getById(Integer id);

    public void update(T t);

    public void delete(T t);

    public void delete(Integer id);
}
