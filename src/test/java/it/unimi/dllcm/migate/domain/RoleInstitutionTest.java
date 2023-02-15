package it.unimi.dllcm.migate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.unimi.dllcm.migate.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoleInstitutionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleInstitution.class);
        RoleInstitution roleInstitution1 = new RoleInstitution();
        roleInstitution1.setId(1L);
        RoleInstitution roleInstitution2 = new RoleInstitution();
        roleInstitution2.setId(roleInstitution1.getId());
        assertThat(roleInstitution1).isEqualTo(roleInstitution2);
        roleInstitution2.setId(2L);
        assertThat(roleInstitution1).isNotEqualTo(roleInstitution2);
        roleInstitution1.setId(null);
        assertThat(roleInstitution1).isNotEqualTo(roleInstitution2);
    }
}
