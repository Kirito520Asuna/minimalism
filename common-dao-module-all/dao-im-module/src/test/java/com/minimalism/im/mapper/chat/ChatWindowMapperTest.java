package com.minimalism.im.mapper.chat;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.minimalism.im.domain.chat.ChatWindow;
import com.minimalism.im.domain.enums.ChatType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @Author yan
 * @Date 2023/9/18 0018 15:43
 * @Description
 */
public class ChatWindowMapperTest {
    private static ChatWindowMapper mapper;

    @BeforeClass
    public static void setUpMybatisDatabase() {
        SqlSessionFactory builder = new MybatisSqlSessionFactoryBuilder().build(ChatWindowMapperTest.class.getClassLoader().getResourceAsStream("mybatisTestConfiguration/ChatWindowMapperTestConfiguration.xml"));
        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        builder.getConfiguration().addInterceptor(interceptor);
        //you can use builder.openSession(false) to not commit to database
        mapper = builder.getConfiguration().getMapper(ChatWindowMapper.class, builder.openSession(true));

    }

    @Test
    public void testGetChatWindow() {

        ChatWindow chatWindow = mapper.getChatWindow(1L,1L, ChatType.ONE_ON_ONE_CHAT);
    }
}
