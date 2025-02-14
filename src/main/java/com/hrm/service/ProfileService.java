package com.hrm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrm.dao.ProfileDao;
import com.hrm.dto.EmployeeDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileDao profileDao;

    public EmployeeDto getEmployeeByEmail(String email) {
        return profileDao.getEmployeeByEmail(email);
    }

    public void updateProfile(EmployeeDto employee) {
        log.info("Attempting to update profile - Email: {}, ProfileImage: {}", 
                 employee.getEmail(), employee.getProfileImage());
        profileDao.updateProfile(employee);
    }
}