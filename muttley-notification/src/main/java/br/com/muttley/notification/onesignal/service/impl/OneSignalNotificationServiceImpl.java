package br.com.muttley.notification.onesignal.service.impl;

import br.com.muttley.model.hermes.notification.onesignal.Notification;
import br.com.muttley.notification.onesignal.service.OneSignalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class OneSignalNotificationServiceImpl implements OneSignalNotificationService {
    private final Validator validator;
    private final OneSignalNotificationServiceClient oneSignalNotificationServiceClient;
    private final String APP_ID;

    @Autowired
    public OneSignalNotificationServiceImpl(final Validator validator, final OneSignalNotificationServiceClient oneSignalNotificationServiceClient, @Value("${muttley.notification.onesignal.appId:#null}") final String APP_ID) {
        this.validator = new SpringValidatorAdapter(validator);
        this.oneSignalNotificationServiceClient = oneSignalNotificationServiceClient;
        this.APP_ID = APP_ID;
    }

    @Override
    public void sendNotification(final Notification notification) {
        notification.setAppId(APP_ID);
        this.validate(notification);
        this.oneSignalNotificationServiceClient.sendNotification(notification);
    }

    private final void validate(final Object o) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (!isEmpty(violations)) {
            throw new ConstraintViolationException("test", violations);
        }
    }
}
