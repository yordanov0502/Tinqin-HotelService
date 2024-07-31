package com.tinqinacademy.hotel.api.services;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;

public interface SystemService {

    RegisterVisitorOutput registerVisitors(RegisterVisitorInput input);
    GetVisitorsOutput getVisitors(GetVisitorsInput input);
    UpdateRoomPartiallyOutput updateRoomPartially(UpdateRoomPartiallyInput input);
}
