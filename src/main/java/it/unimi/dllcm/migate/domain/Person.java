package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "person")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private Instant dob;

    @OneToMany(mappedBy = "person")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "person" }, allowSetters = true)
    private Set<Language> languages = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "locations", "person" }, allowSetters = true)
    private Set<Country> countries = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_person__role_institution",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "role_institution_id")
    )
    @JsonIgnoreProperties(value = { "people", "institutions" }, allowSetters = true)
    private Set<RoleInstitution> roleInstitutions = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_person__role_work",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "role_work_id")
    )
    @JsonIgnoreProperties(value = { "people", "works" }, allowSetters = true)
    private Set<RoleWork> roleWorks = new HashSet<>();

    @ManyToMany(mappedBy = "people")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "people", "institutions", "roleWorks" }, allowSetters = true)
    private Set<Work> works = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Person id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Person name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return this.gender;
    }

    public Person gender(String gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Instant getDob() {
        return this.dob;
    }

    public Person dob(Instant dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(Instant dob) {
        this.dob = dob;
    }

    public Set<Language> getLanguages() {
        return this.languages;
    }

    public void setLanguages(Set<Language> languages) {
        if (this.languages != null) {
            this.languages.forEach(i -> i.setPerson(null));
        }
        if (languages != null) {
            languages.forEach(i -> i.setPerson(this));
        }
        this.languages = languages;
    }

    public Person languages(Set<Language> languages) {
        this.setLanguages(languages);
        return this;
    }

    public Person addLanguage(Language language) {
        this.languages.add(language);
        language.setPerson(this);
        return this;
    }

    public Person removeLanguage(Language language) {
        this.languages.remove(language);
        language.setPerson(null);
        return this;
    }

    public Set<Country> getCountries() {
        return this.countries;
    }

    public void setCountries(Set<Country> countries) {
        if (this.countries != null) {
            this.countries.forEach(i -> i.setPerson(null));
        }
        if (countries != null) {
            countries.forEach(i -> i.setPerson(this));
        }
        this.countries = countries;
    }

    public Person countries(Set<Country> countries) {
        this.setCountries(countries);
        return this;
    }

    public Person addCountry(Country country) {
        this.countries.add(country);
        country.setPerson(this);
        return this;
    }

    public Person removeCountry(Country country) {
        this.countries.remove(country);
        country.setPerson(null);
        return this;
    }

    public Set<RoleInstitution> getRoleInstitutions() {
        return this.roleInstitutions;
    }

    public void setRoleInstitutions(Set<RoleInstitution> roleInstitutions) {
        this.roleInstitutions = roleInstitutions;
    }

    public Person roleInstitutions(Set<RoleInstitution> roleInstitutions) {
        this.setRoleInstitutions(roleInstitutions);
        return this;
    }

    public Person addRoleInstitution(RoleInstitution roleInstitution) {
        this.roleInstitutions.add(roleInstitution);
        roleInstitution.getPeople().add(this);
        return this;
    }

    public Person removeRoleInstitution(RoleInstitution roleInstitution) {
        this.roleInstitutions.remove(roleInstitution);
        roleInstitution.getPeople().remove(this);
        return this;
    }

    public Set<RoleWork> getRoleWorks() {
        return this.roleWorks;
    }

    public void setRoleWorks(Set<RoleWork> roleWorks) {
        this.roleWorks = roleWorks;
    }

    public Person roleWorks(Set<RoleWork> roleWorks) {
        this.setRoleWorks(roleWorks);
        return this;
    }

    public Person addRoleWork(RoleWork roleWork) {
        this.roleWorks.add(roleWork);
        roleWork.getPeople().add(this);
        return this;
    }

    public Person removeRoleWork(RoleWork roleWork) {
        this.roleWorks.remove(roleWork);
        roleWork.getPeople().remove(this);
        return this;
    }

    public Set<Work> getWorks() {
        return this.works;
    }

    public void setWorks(Set<Work> works) {
        if (this.works != null) {
            this.works.forEach(i -> i.removePerson(this));
        }
        if (works != null) {
            works.forEach(i -> i.addPerson(this));
        }
        this.works = works;
    }

    public Person works(Set<Work> works) {
        this.setWorks(works);
        return this;
    }

    public Person addWork(Work work) {
        this.works.add(work);
        work.getPeople().add(this);
        return this;
    }

    public Person removeWork(Work work) {
        this.works.remove(work);
        work.getPeople().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", gender='" + getGender() + "'" +
            ", dob='" + getDob() + "'" +
            "}";
    }
}
