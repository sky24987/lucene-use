package com.sky.hib.util;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;


public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    public static final ThreadLocal<Session> session;
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
    static {
        try {
            Configuration config = new AnnotationConfiguration().configure(CONFIG_FILE_LOCATION);
            sessionFactory = config.buildSessionFactory();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }

        session = new ThreadLocal<Session>();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static synchronized Session currentSession()
            throws HibernateException {
        Session s = session.get();

        if ((s == null) || (!s.isOpen())) {
            s = sessionFactory.openSession();
            session.set(s);
        }

        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = session.get();
        session.set(null);

        if (s != null) {
            s.close();
        }
    }

    public static List<?> getDatas(Class<?> clazz, Criterion[] criterions) {
        Session session = currentSession();
        Criteria crit = session.createCriteria(clazz);

        if ((criterions != null) && (criterions.length > 0)) {
            for (Criterion criterion : criterions) {
                crit = crit.add(criterion);
            }
        }

        List<?> datas = crit.list();
        closeSession();
        return datas;
    }

    public static void insert(Object obj) {
        Session session = currentSession();
        Transaction tx = session.beginTransaction();
        session.save(obj);
        tx.commit();
        closeSession();
    }

    public static void delete(Object obj) {
        Session session = currentSession();
        Transaction tx = session.beginTransaction();
        session.delete(obj);
        tx.commit();
        closeSession();
    }

    public static void update(Object obj) {
        Session session = currentSession();
        Transaction tx = session.beginTransaction();
        session.update(obj);
        tx.commit();
        closeSession();
    }
}