package com.opuscapita.peppol.support.ui.dao;

import com.opuscapita.peppol.support.ui.domain.MessageStatus;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.25.11
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class HibernateUtil {

    public static Criteria createCriteria(TableParameters tableParameters, Class type, Session session) {
        Criteria criteria = session.createCriteria(type);

        // Sorting
        if (tableParameters.getSorting().size() > 0) {
            for (Map.Entry<String, String> entry : tableParameters.getSorting().entrySet()) {
                if (entry.getValue().equals("asc")) {
                    criteria.addOrder(Order.asc(entry.getKey()));
                } else {
                    criteria.addOrder(Order.desc(entry.getKey()));
                }
            }
        }

        criteria.setFirstResult((tableParameters.getPage() - 1) * tableParameters.getCount());
        criteria.setMaxResults(tableParameters.getCount());

        // Filtering
        Conjunction conjunction = Restrictions.conjunction();
        for (Map.Entry<String, String> entry : tableParameters.getFilter().entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if ("status".equals(entry.getKey())) {
                    conjunction.add(Restrictions.eq(entry.getKey(), MessageStatus.valueOf(entry.getValue())));
                } else if ("fileName".equals(entry.getKey())) {
                    conjunction.add(Restrictions.like("file.filename", getFormedFileNameRestriction(entry.getValue())));
                } else if ("invoiceDate".equals(entry.getKey())) {
                    try {
                        conjunction.add(Restrictions.like(entry.getKey(), getFormedDateRestriction(entry.getValue())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String search = tableParameters.isExactSearch() ? "" : "%";
                    conjunction.add(Restrictions.like(entry.getKey(), search + entry.getValue() + search).ignoreCase());
                }
            }
        }
        criteria.add(conjunction);
        return criteria;
    }

    private static Date getFormedDateRestriction(String dateString) throws ParseException {
        dateString = dateString.replace("\"", "");
        dateString = dateString.substring(0, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.parse(dateString);
    }

    private static String getFormedFileNameRestriction(String fileName) {
        fileName = fileName.trim().replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
        return "%" + fileName + "%";
    }
}
