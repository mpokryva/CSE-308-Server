package com.broncos.gerrymandering.util;

import com.broncos.gerrymandering.model.District;
import com.broncos.gerrymandering.model.Precinct;
import com.broncos.gerrymandering.model.State;
import com.broncos.gerrymandering.model.StateCode;

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
    private static StateManager sm;

    private StateManager() {
        statesByCode = new HashMap<>();
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
            EntityManager em = DefaultEntityManager.getDefaultEntityManager();
            final String qText = "SELECT s FROM STATE s WHERE s.stateCode = :stateCode";
            Query query = em.createQuery(qText);
            query.setParameter("stateCode", stateCode).setMaxResults(1);
            List<State> results = query.getResultList();
            if (results != null && results.size() > 0) {
                state = results.get(0);
                statesByCode.put(stateCode, state);
            }
        }
        return state;
    }

    public District getDistrict(Integer districtId, StateCode stateCode) {
        EntityManager em = DefaultEntityManager.getDefaultEntityManager();
        final String qText = "SELECT d FROM DISTRICT d WHERE d.districtId = :districtId and " +
                "d.state.stateCode = :stateCode";
        Query query = em.createQuery(qText);
        query.setParameter("districtId", districtId)
                .setParameter("stateCode", stateCode)
                .setMaxResults(1);
        List<District> results = query.getResultList();
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public Precinct getPrecinct(Integer precinctId, Integer districtId, StateCode stateCode) {
        EntityManager em = DefaultEntityManager.getDefaultEntityManager();
        final String qText = "SELECT p FROM PRECINCT p WHERE p.precinctId = :precinctId and " +
                "p.district.districtId = :districtId and " +
                "p.state.stateCode = :stateCode";
        Query query = em.createQuery(qText);
        query.setParameter("precinctId", precinctId)
                .setParameter("districtId", districtId)
                .setParameter("stateCode", stateCode)
                .setMaxResults(1);
        List<Precinct> results = query.getResultList();
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

}
