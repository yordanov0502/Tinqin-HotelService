package com.tinqinacademy.hotel.api.services;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
public interface SystemService {
    GetVisitorsOutput getVisitors(GetVisitorsInput input);
}