package com.dccorp.pepipostcontrib.driver;

import com.pepipost.api.PepipostClient;
import com.pepipost.api.attachementProvider.AttachSourceProvider;
import com.pepipost.api.attachementProvider.S3AttachSourceProvider;
import com.pepipost.api.exceptions.APIException;
import com.pepipost.api.models.EmailBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Slf4j
@Configuration
@ComponentScan
@PropertySource("classpath:app.properties")
public class AppConfiguration {

    // read from properties file.
    // more options can be added similary fashion.
    @Value( "${aws.region}" )
    private String awsRegion;

    // Pepipost Client
    // please see API for more options.
    @Bean
    public PepipostClient getPepipostClient()
    {
        log.info("instantiating PepipostClient");
        return new PepipostClient();
    }

    // Pepipost EmailBody
    // please see API for more options.
    @Bean
    public EmailBody getEmailBody()
    {
        log.info("instantiating EmailBody");
        return new EmailBody();
    }

    // S3 connection.
    // for now only supports ~/.aws/credentials file.
    // future releases might support more flexible options.
    @Bean
    public AttachSourceProvider getAWSSourceProvider() throws APIException
    {
        log.info("instantiating AttachSourceProvider {}", awsRegion);
        return new S3AttachSourceProvider(awsRegion, 10);
    }

    //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfiguration() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
