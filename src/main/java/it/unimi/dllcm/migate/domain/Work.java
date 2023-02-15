package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Work.
 */
@Entity
@Table(name = "work")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "work")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Work implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "published")
    private Instant published;

    @ManyToMany
    @JoinTable(name = "rel_work__person", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
    @JsonIgnoreProperties(value = { "languages", "countries", "roleInstitutions", "roleWorks", "works" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_work__institution",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    @JsonIgnoreProperties(value = { "locations", "roleInstitutions", "works" }, allowSetters = true)
    private Set<Institution> institutions = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_work__role_work",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "role_work_id")
    )
    @JsonIgnoreProperties(value = { "people", "works" }, allowSetters = true)
    private Set<RoleWork> roleWorks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Work id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Work name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getPublished() {
        return this.published;
    }

    public Work published(Instant published) {
        this.setPublished(published);
        return this;
    }

    public void setPublished(Instant published) {
        this.published = published;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
    }

    public Work people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public Work addPerson(Person person) {
        this.people.add(person);
        person.getWorks().add(this);
        return this;
    }

    public Work removePerson(Person person) {
        this.people.remove(person);
        person.getWorks().remove(this);
        return this;
    }

    public Set<Institution> getInstitutions() {
        return this.institutions;
    }

    public void setInstitutions(Set<Institution> institutions) {
        this.institutions = institutions;
    }

    public Work institutions(Set<Institution> institutions) {
        this.setInstitutions(institutions);
        return this;
    }

    public Work addInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.getWorks().add(this);
        return this;
    }

    public Work removeInstitution(Institution institution) {
        this.institutions.remove(institution);
        institution.getWorks().remove(this);
        return this;
    }

    public Set<RoleWork> getRoleWorks() {
        return this.roleWorks;
    }

    public void setRoleWorks(Set<RoleWork> roleWorks) {
        this.roleWorks = roleWorks;
    }

    public Work roleWorks(Set<RoleWork> roleWorks) {
        this.setRoleWorks(roleWorks);
        return this;
    }

    public Work addRoleWork(RoleWork roleWork) {
        this.roleWorks.add(roleWork);
        roleWork.getWorks().add(this);
        return this;
    }

    public Work removeRoleWork(RoleWork roleWork) {
        this.roleWorks.remove(roleWork);
        roleWork.getWorks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Work)) {
            return false;
        }
        return id != null && id.equals(((Work) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Work{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", published='" + getPublished() + "'" +
            "}";
    }
}
