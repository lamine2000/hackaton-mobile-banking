package sn.finedev.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class NotificationSettingsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationSettings.class);
        NotificationSettings notificationSettings1 = new NotificationSettings();
        notificationSettings1.setId(1L);
        NotificationSettings notificationSettings2 = new NotificationSettings();
        notificationSettings2.setId(notificationSettings1.getId());
        assertThat(notificationSettings1).isEqualTo(notificationSettings2);
        notificationSettings2.setId(2L);
        assertThat(notificationSettings1).isNotEqualTo(notificationSettings2);
        notificationSettings1.setId(null);
        assertThat(notificationSettings1).isNotEqualTo(notificationSettings2);
    }
}
