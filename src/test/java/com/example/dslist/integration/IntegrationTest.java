package com.example.dslist.integration;

import com.example.dslist.DslistApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = DslistApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8091",
                "server.servlet.contextPath=/api"
        })
@ExtendWith({ PostgresTestContainer.class })
@AutoConfigureWebMvc
@AutoConfigureMockMvc()
@Import({ BuildProperties.class })
@ActiveProfiles("dev")
public @interface IntegrationTest {
}
