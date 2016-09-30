package com.opuscapita.peppol.support.ui.service;


import com.opuscapita.peppol.support.ui.dao.SmtpDAO;
import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.SmtpListTmp;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class SmtpService implements Service<SmtpListTmp> {
    private final String mapFileName = "/smtprcpt.map";

    @Autowired
    private SmtpDAO smtpDAO;


    @Override
    public void add(SmtpListTmp smtpListTmp) {
        smtpDAO.add(smtpListTmp);
    }

    @Override
    public List<SmtpListTmp> getAll() {
        List<SmtpListTmp> list = smtpDAO.getAll();
        return list;
    }

    @Override
    public SmtpListTmp getById(Integer id) {
        SmtpListTmp smtp = smtpDAO.getById(id);
        return smtp;
    }

    @Override
    public void update(SmtpListTmp smtpListTmp) {
        smtpDAO.update(smtpListTmp);
    }

    @Override
    public void delete(SmtpListTmp smtpListTmp) {
        smtpDAO.delete(smtpListTmp);
    }

    @Override
    public void delete(Integer id) {
        smtpDAO.delete(id);
    }

    public SmtpListTmp getBySenderId(String senderId) throws HibernateException {
        SmtpListTmp smtp = smtpDAO.getBySender(senderId);
        return smtp;
    }


    public void updateCustomerEmails(Customer customer) {
        SmtpListTmp smtpListTmp = smtpDAO.getBySender(customer.getIdentifier());
        if (smtpListTmp != null) {
            customer.setOutboundEmails(smtpListTmp.getEmails());
        }
    }


    // TODO: remove
    /*public int addSmtpOnce() {
        Transaction tx = null;
        int counter = 0;
        try {
            tx = HibernateUtil.getSession().beginTransaction();
            File mapFile = new Util().findFile(mapFileName);
            BufferedReader br = new BufferedReader(new FileReader(mapFile));
            String line;
            while ((line = br.readLine()) != null) {
                SmtpListTmp smtp = new SmtpListTmp();
                smtp.setSenderId(line.split("=")[0]);
                smtp.setEmails(line.split("=")[1]);
                smtpDAO.add(smtp);
                counter++;
            }
            br.close();
            tx.commit();
        } catch (Exception e) {
            if(tx!=null)
                tx.rollback();
        } finally {
            HibernateUtil.closeSession();
        }
        return counter;
    }*/
}
