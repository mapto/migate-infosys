package it.unimi.dllcm.migate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimi.dllcm.migate.domain.enumeration.Country;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

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

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country;

    @ManyToMany
    @JoinTable(
        name = "rel_institution__role",
        joinColumns = @JoinColumn(name = "institution_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnoreProperties(value = { "people", "institutions" }, allowSetters = true)
    private Set<RoleInstitution> roles = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "owners" }, allowSetters = true)
    private Location site;

    @ManyToMany(mappedBy = "sponsors")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "responsibles", "sponsors", "roles" }, allowSetters = true)
    private Set<Work> products = new HashSet<>();

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

    public Country getCountry() {
        return this.country;
    }

    public Institution country(Country country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<RoleInstitution> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<RoleInstitution> roleInstitutions) {
        this.roles = roleInstitutions;
    }

    public Institution roles(Set<RoleInstitution> roleInstitutions) {
        this.setRoles(roleInstitutions);
        return this;
    }

    public Institution addRole(RoleInstitution roleInstitution) {
        this.roles.add(roleInstitution);
        roleInstitution.getInstitutions().add(this);
        return this;
    }

    public Institution removeRole(RoleInstitution roleInstitution) {
        this.roles.remove(roleInstitution);
        roleInstitution.getInstitutions().remove(this);
        return this;
    }

    public Location getSite() {
        return this.site;
    }

    public void setSite(Location location) {
        this.site = location;
    }

    public Institution site(Location location) {
        this.setSite(location);
        return this;
    }

    public Set<Work> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Work> works) {
        if (this.products != null) {
            this.products.forEach(i -> i.removeSponsor(this));
        }
        if (works != null) {
            works.forEach(i -> i.addSponsor(this));
        }
        this.products = works;
    }

    public Institution products(Set<Work> works) {
        this.setProducts(works);
        return this;
    }

    public Institution addProduct(Work work) {
        this.products.add(work);
        work.getSponsors().add(this);
        return this;
    }

    public Institution removeProduct(Work work) {
        this.products.remove(work);
        work.getSponsors().remove(this);
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
            ", country='" + getCountry() + "'" +
            "}";
    }
}
