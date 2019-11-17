package com.dccorp.pepipostcontrib.driver;

import com.pepipost.api.PepipostClient;
import com.pepipost.api.attachementProvider.AttachSourceProvider;
import com.pepipost.api.controllers.EmailController;
import com.pepipost.api.exceptions.APIException;
import com.pepipost.api.http.client.APICallBack;
import com.pepipost.api.http.client.HttpContext;
import com.pepipost.api.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Sample class demonstrating Pepipost Java SDK Integration from Spring.
 */
@Slf4j
@Component
class PepiPostDriver {
    @Autowired
    private PepipostClient client;

    @Autowired
    private EmailBody body;

    @Autowired
    private AttachSourceProvider awsSourceProvider;

    /**
     * Method for downloading S3 files and sending emails.
     *
     * @throws APIException Exception
     */
    private void doFun() throws APIException {
        log.info("invoking doFun");
        EmailController emailController = client.getEmail();
        String apiKey = "3743f77f6e288cf8085b89da97a0efdd";

        body.setPersonalizations(new LinkedList<Personalizations>());
        Personalizations bodyPersonalizations0 = new Personalizations();
        bodyPersonalizations0.setRecipient("chaudharydeepak08@gmail.com");
        body.getPersonalizations().add(bodyPersonalizations0);
        body.setTags("tagsjava");
        body.setFrom(new From());
        body.getFrom().setFromEmail("info@pepisandbox.com");
        body.getFrom().setFromName("info");
        body.setSubject("JAVA SDK 3.0 w/ AWS ok encrypted content.");
        body.setContent("Test mail ready to sent");
        body.setSettings(new Settings());

        /* list of files in specific bucket. */
        // 2 files in same bucket. They have no encryption applied.
        List<String> filesList = new ArrayList<String>();
        filesList.add("sampleText.txt");
        filesList.add("sampleText2.txt");
        FileObjects fileObjects1 = new FileObjects("pepipostawsinteg", filesList);
        fileObjects1.setEncyrptionType("NONE");

        /* list of all buckets. User might use multiple buckets. */
        List<FileObjects> fileObjectsList = new ArrayList<FileObjects>();
        fileObjectsList.add(fileObjects1);

        body.setAttachments(awsSourceProvider.getFileObjects(fileObjectsList));

        body.getSettings().setFooter(0);
        body.getSettings().setClicktrack(1);
        body.getSettings().setOpentrack(1);
        body.getSettings().setUnsubscribe(1);

        emailController.createSendEmailAsync(apiKey, body, "/v2/sendEmail", new APICallBack<SendEmailResponse>() {
            public void onSuccess(HttpContext context, SendEmailResponse response) {
                log.info("Message :: {} Error :: {}", response.getMessage(), response.getErrorInfo().getErrorMessage());
                System.exit(0);
            }

            public void onFailure(HttpContext context, Throwable error) {
                log.info("response: {}", context.getResponse());
                System.exit(1);
            }

        });
    }

    /**
     * main method to run this app.
     *
     * @param args args
     * @throws APIException exception
     */
    public static void main(String[] args) throws APIException {
        log.info("loading Spring Context~");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        log.info("finished loading Spring Context~");
        PepiPostDriver pepiPostDriver0 = context.getBean(PepiPostDriver.class);
        log.info("pepiPostDriver0: {}", pepiPostDriver0);
        pepiPostDriver0.doFun();
        log.info("finished running app.");
    }
}