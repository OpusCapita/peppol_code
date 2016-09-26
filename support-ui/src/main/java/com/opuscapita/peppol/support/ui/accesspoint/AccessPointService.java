package com.opuscapita.peppol.support.ui.accesspoint;


import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.service.Service;
import org.hibernate.HibernateException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.27.11
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public interface AccessPointService extends Service<AccessPoint> {

    AccessPoint getByApId(String accessPointId) throws HibernateException;

    List<AccessPoint> getAll(TableParameters tableParameters) throws HibernateException;

    int getCount() throws HibernateException;

    String getEmails(String accessPointId);
}
