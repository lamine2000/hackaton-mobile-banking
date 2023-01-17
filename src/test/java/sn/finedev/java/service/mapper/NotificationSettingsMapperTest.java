package sn.finedev.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationSettingsMapperTest {

    private NotificationSettingsMapper notificationSettingsMapper;

    @BeforeEach
    public void setUp() {
        notificationSettingsMapper = new NotificationSettingsMapperImpl();
    }
}
