package it.korea.app_boot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.korea.app_boot.common.listner.P6SpyEventListner;

@Configuration
public class P6SpyConfig {

    @Bean
    public P6SpyEventListner pSpyEventListner(){
        return new P6SpyEventListner();
    }

    @Bean
    public P6spySqlFormatter p6spySqlFormatter(){
        return new P6spySqlFormatter();
    }

}
