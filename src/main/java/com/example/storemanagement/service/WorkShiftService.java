package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertWorkShiftRequest;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.WorkShift;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.WorkShiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkShiftService {

    private final StoreRepository storeRepository;
    private final WorkShiftRepository workShiftRepository;

    public WorkShiftService(StoreRepository storeRepository,
                            WorkShiftRepository workShiftRepository) {
        this.storeRepository = storeRepository;
        this.workShiftRepository = workShiftRepository;
    }

    public WorkShiftPublic createWorkShift(Integer storeId, UpsertWorkShiftRequest upsertWorkShiftRequest) {
        WorkShift workShift = new WorkShift();

        System.out.println(upsertWorkShiftRequest.getStartShift());
        workShift.setName(upsertWorkShiftRequest.getName());
        workShift.setStartShift(upsertWorkShiftRequest.getStartShift());
        System.out.println(workShift.getStartShift());
        workShift.setEndShift(upsertWorkShiftRequest.getEndShift());
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        workShift.setStore(store);
        workShift.setOrderShift(upsertWorkShiftRequest.getOrderShift());
        workShiftRepository.save(workShift);
        return WorkShiftPublic.of(workShift);
    }

    public WorkShiftPublic updateWorkShift(Integer storeId, UpsertWorkShiftRequest upsertWorkShiftRequest) {
        WorkShift workShift = workShiftRepository.findById(upsertWorkShiftRequest.getWorkShiftId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy ca làm việc");
        });
        workShift.setStartShift(upsertWorkShiftRequest.getStartShift());
        workShift.setEndShift(upsertWorkShiftRequest.getEndShift());
        workShiftRepository.save(workShift);
        return WorkShiftPublic.of(workShift);
    }

    public List<WorkShiftPublic> getAllWorkShiftFromStore(Integer storeId) {
        List<WorkShift> workShifts = workShiftRepository.findAllByStoreId(storeId);
        return workShifts.stream().map(WorkShiftPublic::of).collect(Collectors.toList());
    }
}
