package com.broncos.gerrymandering.util;

import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.model.StateCode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mpokr on 11/24/2018.
 */
public class StateManager {

    private Map<StateCode, State> statesByCode;
    private Map<StateCode, Integer> idByStateCode;
    private static StateManager sm;

    private StateManager() {
        idByStateCode = new HashMap<>();
        statesByCode = new HashMap<>();
        EntityManager em = DefaultEntityManagerFactory.getEntityManager();
        //Session session = DefaultEntityManagerFactory.getSessionFactory().openSession();
        final String qText = "SELECT s.id, s.stateCode FROM STATE s WHERE original = 1";
        Query query = em.createQuery(qText);
        List<Object[]> results = query.getResultList();
        for (Object[] result : results) {
            Integer id = (Integer) result[0];
            StateCode stateCode = (StateCode) result[1];
            if (id == null) {
                throw new IllegalStateException("id for a state cannot be null.");
            }
            idByStateCode.put(stateCode, id);
        }
        if (idByStateCode.size() != StateCode.values().length) {
            throw new IllegalStateException("id to state code map not initialized for all codes.");
        }
    }

    public static StateManager getInstance() {
        if (sm == null) {
            sm = new StateManager();
        }
        return sm;
    }

    public State getState(StateCode stateCode) {
        State state = statesByCode.get(stateCode);
        if (state == null) {
            EntityManager em = DefaultEntityManagerFactory.getEntityManager();
            final String qText = "SELECT s FROM STATE s WHERE s.id = :stateId";
            Query query = em.createQuery(qText);
            Integer stateId = idByStateCode.get(stateCode);
            query.setParameter("stateId", stateId).setMaxResults(1);
            List<State> results = query.getResultList();
            if (results != null && results.size() > 0) {
                state = results.get(0);
                statesByCode.put(stateCode, state);
            }
        }
        return state;
    }

    public District getDistrict(Integer districtId, StateCode stateCode) {
        State state = getState(stateCode);
        District district = state.getDistrictById(districtId);
        return district;
    }

    public Precinct getPrecinct(Integer precinctId, Integer districtId, StateCode stateCode) {
        State state = getState(stateCode);
        District district = state.getDistrictById(districtId);
        return district.getPrecinctById(precinctId);
    }

}
