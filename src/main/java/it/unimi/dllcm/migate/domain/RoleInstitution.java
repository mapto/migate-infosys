package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A RoleInstitution.
 */
@Entity
@Table(name = "role_institution")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "roleinstitution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleInstitution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start")
    private Instant start;

    @Column(name = "jhi_end")
    private Instant end;

    @ManyToMany(mappedBy = "roleInstitutions")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "languages", "countries", "roleInstitutions", "roleWorks", "works" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @ManyToMany(mappedBy = "roleInstitutions")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "locations", "roleInstitutions", "works" }, allowSetters = true)
    private Set<Institution> institutions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RoleInstitution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public RoleInstitution name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStart() {
        return this.start;
    }

    public RoleInstitution start(Instant start) {
        this.setStart(start);
        return this;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return this.end;
    }

    public RoleInstitution end(Instant end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        if (this.people != null) {
            this.people.forEach(i -> i.removeRoleInstitution(this));
        }
        if (people != null) {
            people.forEach(i -> i.addRoleInstitution(this));
        }
        this.people = people;
    }

    public RoleInstitution people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public RoleInstitution addPerson(Person person) {
        this.people.add(person);
        person.getRoleInstitutions().add(this);
        return this;
    }

    public RoleInstitution removePerson(Person person) {
        this.people.remove(person);
        person.getRoleInstitutions().remove(this);
        return this;
    }

    public Set<Institution> getInstitutions() {
        return this.institutions;
    }

    public void setInstitutions(Set<Institution> institutions) {
        if (this.institutions != null) {
            this.institutions.forEach(i -> i.removeRoleInstitution(this));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.addRoleInstitution(this));
        }
        this.institutions = institutions;
    }

    public RoleInstitution institutions(Set<Institution> institutions) {
        this.setInstitutions(institutions);
        return this;
    }

    public RoleInstitution addInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.getRoleInstitutions().add(this);
        return this;
    }

    public RoleInstitution removeInstitution(Institution institution) {
        this.institutions.remove(institution);
        institution.getRoleInstitutions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleInstitution)) {
            return false;
        }
        return id != null && id.equals(((RoleInstitution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleInstitution{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
