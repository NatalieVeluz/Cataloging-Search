package com.catalog.serviceimpl;

import com.catalog.service.CutterNumberService;
import org.springframework.stereotype.Service;

@Service
public class CutterNumberServiceImpl implements CutterNumberService {

    @Override
    public String generate(String input) {
        if (input == null || input.isBlank()) {
            return "X000";
        }
        return input.substring(0, 1).toUpperCase() + "123";
    }
}