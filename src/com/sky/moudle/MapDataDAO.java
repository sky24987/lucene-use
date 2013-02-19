package com.sky.moudle;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MapDataDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory.getLogger(MapDataDAO.class);
	// property constants
	public static final String EAST = "east";
	public static final String NORTH = "north";
	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String PHONE = "phone";
	public static final String CITY = "city";
	public static final String TOWNSHIP = "township";
	public static final String DATATYPE = "datatype";
	public static final String NUM = "num";
	public static final String CONTENT = "content";
	public static final String EAST_NEW = "eastNew";
	public static final String NORTH_NEW = "northNew";
	public static final String DATA_TYPE = "dataType";
	public static final String COM_TYPE = "comType";
	public static final String CODE = "code";
	public static final String TYPE = "type";

	public void save(MapBean transientInstance) {
		log.debug("saving MapData instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(MapBean persistentInstance) {
		log.debug("deleting MapData instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public MapBean findById(java.lang.Integer id) {
		log.debug("getting MapData instance with id: " + id);
		try {
			MapBean instance = (MapBean) getSession().get(
					"com.hib.moudle.MapData", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<MapBean> findByExample(MapBean instance) {
		log.debug("finding MapData instance by example");
		try {
			List<MapBean> results = (List<MapBean>) getSession()
					.createCriteria("com.hib.moudle.MapData").add(
							create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding MapData instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from MapData as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<MapBean> findByEast(Object east) {
		return findByProperty(EAST, east);
	}

	public List<MapBean> findByNorth(Object north) {
		return findByProperty(NORTH, north);
	}

	public List<MapBean> findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List<MapBean> findByAddress(Object address) {
		return findByProperty(ADDRESS, address);
	}

	public List<MapBean> findByPhone(Object phone) {
		return findByProperty(PHONE, phone);
	}

	public List<MapBean> findByCity(Object city) {
		return findByProperty(CITY, city);
	}

	public List<MapBean> findByTownship(Object township) {
		return findByProperty(TOWNSHIP, township);
	}

	public List<MapBean> findByDatatype(Object datatype) {
		return findByProperty(DATATYPE, datatype);
	}

	public List<MapBean> findByNum(Object num) {
		return findByProperty(NUM, num);
	}

	public List<MapBean> findByContent(Object content) {
		return findByProperty(CONTENT, content);
	}

	public List<MapBean> findByEastNew(Object eastNew) {
		return findByProperty(EAST_NEW, eastNew);
	}

	public List<MapBean> findByNorthNew(Object northNew) {
		return findByProperty(NORTH_NEW, northNew);
	}

	public List<MapBean> findByDataType(Object dataType) {
		return findByProperty(DATA_TYPE, dataType);
	}

	public List<MapBean> findByComType(Object comType) {
		return findByProperty(COM_TYPE, comType);
	}

	public List<MapBean> findByCode(Object code) {
		return findByProperty(CODE, code);
	}

	public List<MapBean> findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	public List findAll() {
		log.debug("finding all MapData instances");
		try {
			String queryString = "from MapData";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public MapBean merge(MapBean detachedInstance) {
		log.debug("merging MapData instance");
		try {
			MapBean result = (MapBean) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(MapBean instance) {
		log.debug("attaching dirty MapData instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(MapBean instance) {
		log.debug("attaching clean MapData instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}