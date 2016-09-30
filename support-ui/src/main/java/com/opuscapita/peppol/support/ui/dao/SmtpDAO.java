package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.SmtpListTmp;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */

public interface SmtpDAO extends DAO<SmtpListTmp> {
    public void add(SmtpListTmp smtp);

    public SmtpListTmp getBySender(String senderId);
}
