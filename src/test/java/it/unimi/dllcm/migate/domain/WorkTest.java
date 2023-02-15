package it.unimi.dllcm.migate.domain;

import static org.assertj.core.api.Assertions.assertThat;

import it.unimi.dllcm.migate.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Work.class);
        Work work1 = new Work();
        work1.setId(1L);
        Work work2 = new Work();
        work2.setId(work1.getId());
        assertThat(work1).isEqualTo(work2);
        work2.setId(2L);
        assertThat(work1).isNotEqualTo(work2);
        work1.setId(null);
        assertThat(work1).isNotEqualTo(work2);
    }
}
