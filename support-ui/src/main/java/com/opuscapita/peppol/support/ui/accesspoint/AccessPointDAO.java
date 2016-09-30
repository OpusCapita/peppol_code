package com.opuscapita.peppol.support.ui.accesspoint;

import com.opuscapita.peppol.support.ui.dao.DAO;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * User: KACENAR1
 */
public interface AccessPointDAO extends DAO<AccessPoint> {
    AccessPoint getByApId(String accessPointId) throws HibernateException;

    List<AccessPoint> getAll(TableParameters tableParameters) throws HibernateException;

    int getCount() throws HibernateException;
}
