package sn.finedev.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.finedev.java.web.rest.TestUtil;

class NotificationSettingsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationSettingsDTO.class);
        NotificationSettingsDTO notificationSettingsDTO1 = new NotificationSettingsDTO();
        notificationSettingsDTO1.setId(1L);
        NotificationSettingsDTO notificationSettingsDTO2 = new NotificationSettingsDTO();
        assertThat(notificationSettingsDTO1).isNotEqualTo(notificationSettingsDTO2);
        notificationSettingsDTO2.setId(notificationSettingsDTO1.getId());
        assertThat(notificationSettingsDTO1).isEqualTo(notificationSettingsDTO2);
        notificationSettingsDTO2.setId(2L);
        assertThat(notificationSettingsDTO1).isNotEqualTo(notificationSettingsDTO2);
        notificationSettingsDTO1.setId(null);
        assertThat(notificationSettingsDTO1).isNotEqualTo(notificationSettingsDTO2);
    }
}
