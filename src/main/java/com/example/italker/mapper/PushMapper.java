package com.example.italker.mapper;

import com.example.italker.pojo.entity.PushHistory;
import org.springframework.stereotype.Component;

@Component
public interface PushMapper {


     void saveHistory(PushHistory history);
}
