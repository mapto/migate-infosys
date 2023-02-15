package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Institution.
 */
@Entity
@Table(name = "institution")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "institution")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Institution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "sites")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "sites", "places" }, allowSetters = true)
    private Set<Location> locations = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_institution__role_institution",
        joinColumns = @JoinColumn(name = "institution_id"),
        inverseJoinColumns = @JoinColumn(name = "role_institution_id")
    )
    @JsonIgnoreProperties(value = { "people", "institutions" }, allowSetters = true)
    private Set<RoleInstitution> roleInstitutions = new HashSet<>();

    @ManyToMany(mappedBy = "institutions")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "people", "institutions", "roleWorks" }, allowSetters = true)
    private Set<Work> works = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Institution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Institution name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(Set<Location> locations) {
        if (this.locations != null) {
            this.locations.forEach(i -> i.setSites(null));
        }
        if (locations != null) {
            locations.forEach(i -> i.setSites(this));
        }
        this.locations = locations;
    }

    public Institution locations(Set<Location> locations) {
        this.setLocations(locations);
        return this;
    }

    public Institution addLocation(Location location) {
        this.locations.add(location);
        location.setSites(this);
        return this;
    }

    public Institution removeLocation(Location location) {
        this.locations.remove(location);
        location.setSites(null);
        return this;
    }

    public Set<RoleInstitution> getRoleInstitutions() {
        return this.roleInstitutions;
    }

    public void setRoleInstitutions(Set<RoleInstitution> roleInstitutions) {
        this.roleInstitutions = roleInstitutions;
    }

    public Institution roleInstitutions(Set<RoleInstitution> roleInstitutions) {
        this.setRoleInstitutions(roleInstitutions);
        return this;
    }

    public Institution addRoleInstitution(RoleInstitution roleInstitution) {
        this.roleInstitutions.add(roleInstitution);
        roleInstitution.getInstitutions().add(this);
        return this;
    }

    public Institution removeRoleInstitution(RoleInstitution roleInstitution) {
        this.roleInstitutions.remove(roleInstitution);
        roleInstitution.getInstitutions().remove(this);
        return this;
    }

    public Set<Work> getWorks() {
        return this.works;
    }

    public void setWorks(Set<Work> works) {
        if (this.works != null) {
            this.works.forEach(i -> i.removeInstitution(this));
        }
        if (works != null) {
            works.forEach(i -> i.addInstitution(this));
        }
        this.works = works;
    }

    public Institution works(Set<Work> works) {
        this.setWorks(works);
        return this;
    }

    public Institution addWork(Work work) {
        this.works.add(work);
        work.getInstitutions().add(this);
        return this;
    }

    public Institution removeWork(Work work) {
        this.works.remove(work);
        work.getInstitutions().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution)) {
            return false;
        }
        return id != null && id.equals(((Institution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Institution{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
