package com.tinqinacademy.hotel.api.services;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;

public interface SystemService {

    RegisterVisitorOutput registerVisitors(RegisterVisitorInput input);
    GetVisitorsOutput getVisitors(GetVisitorsInput input);
}