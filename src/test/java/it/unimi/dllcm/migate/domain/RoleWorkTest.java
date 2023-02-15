package it.unimi.dllcm.migate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.unimi.dllcm.migate.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoleWorkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleWork.class);
        RoleWork roleWork1 = new RoleWork();
        roleWork1.setId(1L);
        RoleWork roleWork2 = new RoleWork();
        roleWork2.setId(roleWork1.getId());
        assertThat(roleWork1).isEqualTo(roleWork2);
        roleWork2.setId(2L);
        assertThat(roleWork1).isNotEqualTo(roleWork2);
        roleWork1.setId(null);
        assertThat(roleWork1).isNotEqualTo(roleWork2);
    }
}
