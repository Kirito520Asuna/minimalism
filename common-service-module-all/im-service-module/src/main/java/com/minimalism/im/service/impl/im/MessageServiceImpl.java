package com.minimalism.im.service.impl.im;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minimalism.im.domain.im.Message;
import com.minimalism.im.mapper.im.MessageMapper;
import com.minimalism.im.service.im.MessageService;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
