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
            query.setParameter("stateCode", stateCode);
            List<State> results = query.getResultList();
            if (results != null && results.size() > 0) {
                state = results.get(0);
                statesByCode.put(stateCode, state);
            }
        }
        return state;
    }

    public District getDistrict(int districtId, StateCode stateCode) {
        State state = getState(stateCode);
        if (state == null) {
            return null;
        }
        Map<Integer, District> districtById = state.getDistrictById();
        return districtById.get(districtId);
    }

    public Precinct getPrecinct(int precinctId, int districtId, StateCode stateCode) {
        District district = getDistrict(districtId, stateCode);
        if (district == null) {
            return null;
        }
        Map<Integer, Precinct> precinctById = district.getPrecinctById();
        return precinctById.get(precinctId);
    }

}
