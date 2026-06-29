package com.nikoruborasu.chat_app.repository;

import com.nikoruborasu.chat_app.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message>findAllByOrderByIdDesc();//全部取得→条件開始→idで並べる→降順
}