package com.strizhonovapps.skinsearcher.osteamdia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@Slf4j
@EnableCaching
@SpringBootApplication
public class OsteamdiaApp {

    public static void main(String[] args) {
        log.info("Running app with args {}", String.join(" ", args));
        ApplicationContext context = SpringApplication.run(OsteamdiaApp.class, args);
        System.exit(SpringApplication.exit(context));
    }
}
