package com.opuscapita.peppol.support.ui.accesspoint;

import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by KACENAR1 on 22.12.2015
 */
@Component
public class AccessPointServiceImpl implements AccessPointService {

    @Autowired
    private AccessPointDAO accessPointDAO;

    @Override
    public List<AccessPoint> getAll(TableParameters tableParameters) throws HibernateException {
        return accessPointDAO.getAll(tableParameters);
    }

    @Override
    public int getCount() throws HibernateException {
        return accessPointDAO.getCount();
    }

    @Override
    public AccessPoint getByApId(String accessPointId) throws HibernateException {
        return accessPointDAO.getByApId(accessPointId);
    }

    @Override
    public String getEmails(String accessPointId) {
        AccessPoint accessPoint = getByApId(accessPointId);
        if (accessPoint != null) {
            return accessPoint.getEmailList();
        }
        return null;
    }

    @Override
    public void add(AccessPoint accessPoint) {
        accessPointDAO.add(accessPoint);
    }

    @Override
    public List<AccessPoint> getAll() {
        return accessPointDAO.getAll();
    }

    @Override
    public AccessPoint getById(Integer id) {
        return accessPointDAO.getById(id);
    }

    @Override
    public void update(AccessPoint accessPoint) {
        accessPointDAO.update(accessPoint);
    }

    @Override
    public void delete(AccessPoint accessPoint) {
        accessPointDAO.delete(accessPoint);
    }

    @Override
    public void delete(Integer id) {
        accessPointDAO.delete(id);
    }
}
