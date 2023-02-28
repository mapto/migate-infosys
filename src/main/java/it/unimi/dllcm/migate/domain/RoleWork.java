package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A RoleWork.
 */
@Entity
@Table(name = "role_work")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rolework")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleWork implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start")
    private LocalDate start;

    @Column(name = "jhi_end")
    private LocalDate end;

    @ManyToMany(mappedBy = "responsibilities")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "jobs", "responsibilities", "works" }, allowSetters = true)
    private Set<Person> people = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "responsibles", "sponsors", "roles" }, allowSetters = true)
    private Set<Work> works = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RoleWork id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public RoleWork name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStart() {
        return this.start;
    }

    public RoleWork start(LocalDate start) {
        this.setStart(start);
        return this;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public RoleWork end(LocalDate end) {
        this.setEnd(end);
        return this;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Set<Person> getPeople() {
        return this.people;
    }

    public void setPeople(Set<Person> people) {
        if (this.people != null) {
            this.people.forEach(i -> i.removeResponsibility(this));
        }
        if (people != null) {
            people.forEach(i -> i.addResponsibility(this));
        }
        this.people = people;
    }

    public RoleWork people(Set<Person> people) {
        this.setPeople(people);
        return this;
    }

    public RoleWork addPerson(Person person) {
        this.people.add(person);
        person.getResponsibilities().add(this);
        return this;
    }

    public RoleWork removePerson(Person person) {
        this.people.remove(person);
        person.getResponsibilities().remove(this);
        return this;
    }

    public Set<Work> getWorks() {
        return this.works;
    }

    public void setWorks(Set<Work> works) {
        if (this.works != null) {
            this.works.forEach(i -> i.removeRole(this));
        }
        if (works != null) {
            works.forEach(i -> i.addRole(this));
        }
        this.works = works;
    }

    public RoleWork works(Set<Work> works) {
        this.setWorks(works);
        return this;
    }

    public RoleWork addWork(Work work) {
        this.works.add(work);
        work.getRoles().add(this);
        return this;
    }

    public RoleWork removeWork(Work work) {
        this.works.remove(work);
        work.getRoles().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleWork)) {
            return false;
        }
        return id != null && id.equals(((RoleWork) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleWork{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", end='" + getEnd() + "'" +
            "}";
    }
}
