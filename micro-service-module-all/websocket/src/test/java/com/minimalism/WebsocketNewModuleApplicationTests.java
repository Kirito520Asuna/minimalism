package com.minimalism;

import cn.hutool.json.JSONUtil;
import com.minimalism.domain.Message;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebsocketNewModuleApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
		Message message = new Message().setContent("110").setTargetId("2").setSenderId("1");
		System.out.println(JSONUtil.toJsonStr(message));
	}

}
