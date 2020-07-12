package com.example.demo.mapper;

import com.example.demo.domain.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author chei1
 */

@Mapper
@Repository
public interface PaymentMapper {
    int Create(Payment payments);
    int put(Payment payments);
}
